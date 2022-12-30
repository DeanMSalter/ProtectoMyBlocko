package McEssence.ProtectoMyBlocko;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Dropper;
import org.bukkit.block.Hopper;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.UUID;

public class Listeners implements Listener {
    private final Config config;
    Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("ProtectoMyBlocko");
    private final NamespacedKey blockOwnerKey = new NamespacedKey(plugin, "blockOwner");
    private final NamespacedKey protectionDateTimeKey = new NamespacedKey(plugin, "protectionDateTime");

    public Listeners(Config configTemp){
        config = configTemp;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onBlockPlace(BlockPlaceEvent event){
        CustomBlockData customBlockData = new CustomBlockData(event.getBlock(), plugin);
        customBlockData.clear();
        if (event.getPlayer().hasPermission("ProtectoMyBlocko.protect")) {
            if (!config.getExcludedBlocks().contains(event.getBlock().getType())) {
                customBlockData.set(blockOwnerKey, PersistentDataType.BYTE_ARRAY, Util.getBytesFromUUID(event.getPlayer().getUniqueId()));
                customBlockData.set(protectionDateTimeKey, PersistentDataType.LONG, Instant.now().getEpochSecond());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event){
        Block block = event.getBlock();
        if (config.getExcludedBlocks().contains(block.getType())){
            CustomBlockData customBlockData = new CustomBlockData(block, plugin);
            customBlockData.clear();
            return;
        }
        if (!IsAllowed(block,event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(config.getCanNotBreak());
            return;
        }
        if (config.getHighValueBlocks().contains(block.getType())) {
            if (!IsAllowedHighValue(block, event.getPlayer())){
                event.setCancelled(true);
                event.getPlayer().sendMessage(config.getCanNotBreakHighValue());
                return;
            }
        }
        CustomBlockData customBlockData = new CustomBlockData(block, plugin);
        customBlockData.clear();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onItemFramePlace(HangingPlaceEvent event){
        Block block = event.getBlock().getRelative(event.getBlockFace());
        CustomBlockData customBlockData = new CustomBlockData(block, plugin);
        customBlockData.clear();

        if (event.getPlayer().hasPermission("ProtectoMyBlocko.protect")) {
            customBlockData.set(blockOwnerKey, PersistentDataType.BYTE_ARRAY, Util.getBytesFromUUID(event.getPlayer().getUniqueId()));
            customBlockData.set(protectionDateTimeKey, PersistentDataType.LONG, Instant.now().getEpochSecond());
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onItemFrameBreak(HangingBreakByEntityEvent event){
        if (!(event.getEntity() instanceof ItemFrame)){
            return;
        }
        if (!(event.getRemover() instanceof Player || event.getRemover() instanceof Projectile)) {
            return;
        }
        if (event.getRemover() instanceof Projectile) {
            if (!(((Projectile) event.getRemover()).getShooter() instanceof Player)) {
                return;
            }
        }
        if (!IsAllowed(event.getEntity().getLocation().getBlock(), (Player) event.getRemover())) {
            event.setCancelled(true);
            event.getRemover().sendMessage(config.getCanNotBreak());
        } else {
            CustomBlockData customBlockData = new CustomBlockData(event.getEntity().getLocation().getBlock(), plugin);
            customBlockData.clear();
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onItemFrameItemBreak(EntityDamageByEntityEvent event){
        if (!(event.getEntity() instanceof ItemFrame)){
            return;
        }
        if (!(event.getDamager() instanceof Player || event.getDamager() instanceof Projectile)) {
            return;
        }
        if (event.getDamager() instanceof Projectile) {
            if (!(((Projectile) event.getDamager()).getShooter() instanceof Player)) {
                return;
            }
        }
        if (!IsAllowed(event.getEntity().getLocation().getBlock(), (Player) event.getDamager())) {
            event.setCancelled(true);
            event.getDamager().sendMessage(config.getCanNotBreak());
        }
    }
    @EventHandler
    public void onInventoryOpenEvent(InventoryOpenEvent event){
        InventoryHolder holder = event.getInventory().getHolder();
        Block block = null;
        if (holder instanceof Chest) {
            Chest chest  = (Chest) holder;
            block = chest.getBlock();
        } else if(holder instanceof DoubleChest){
            DoubleChest doubleChest  = (DoubleChest) holder;
            block = doubleChest.getLocation().getBlock();
        } else if(holder instanceof ShulkerBox){
            ShulkerBox shulkerBox  = (ShulkerBox) holder;
            block = shulkerBox.getLocation().getBlock();
        } else if(holder instanceof Hopper){
            Hopper hopper  = (Hopper) holder;
            block = hopper.getLocation().getBlock();
        } else if(holder instanceof Dispenser){
            Dispenser dispenser  = (Dispenser) holder;
            block = dispenser.getLocation().getBlock();
        } else if(holder instanceof Dropper){
            Dropper dropper  = (Dropper) holder;
            block = dropper.getLocation().getBlock();
        }
        else {
            return;
        }
        if (!IsAllowed(block, (Player) event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(config.getCanNotOpen());
        }
    }

    private boolean IsAllowed(Block block, Player player) {
        if (block == null || player == null) {
            return true;
        }
        try {
            final PersistentDataContainer customBlockData = new CustomBlockData(block, plugin);
            if (!customBlockData.has(blockOwnerKey, PersistentDataType.BYTE_ARRAY)) return true;
            if (!customBlockData.has(protectionDateTimeKey, PersistentDataType.LONG)) return true;

            if (Instant.now().getEpochSecond() - customBlockData.get(protectionDateTimeKey, PersistentDataType.LONG) < config.getProtectionDelay()) return true;

            byte[] playerBytes = Util.getBytesFromUUID(player.getUniqueId());
            byte[] blockOwnerBytes = customBlockData.get(blockOwnerKey, PersistentDataType.BYTE_ARRAY);
            UUID blockOwnerUUID = Util.getUUIDFromBytes(blockOwnerBytes);

            if (!Arrays.equals(playerBytes, blockOwnerBytes)) {
                if (!config.getTrustedPlayers(blockOwnerUUID).contains(String.valueOf(player.getUniqueId()))) {
                    if (!player.hasPermission("ProtectoMyBlocko.bypass")) {
                        return false;
                    }
                }
            }
            return true;
        }catch(Exception e){
            Bukkit.getLogger().severe("An error occured when checking if a player can interact with block.");
            e.printStackTrace();
        }
        return true;
    }
    private boolean IsAllowedHighValue(Block block, Player player) {
        if (block == null || player == null) {
            return true;
        }
        try {
            final PersistentDataContainer customBlockData = new CustomBlockData(block, plugin);
            if (!customBlockData.has(blockOwnerKey, PersistentDataType.BYTE_ARRAY)) return true;
            if (!customBlockData.has(protectionDateTimeKey, PersistentDataType.LONG)) return true;

            if (Instant.now().getEpochSecond() - customBlockData.get(protectionDateTimeKey, PersistentDataType.LONG) < config.getProtectionDelay()) return true;

            byte[] playerBytes = Util.getBytesFromUUID(player.getUniqueId());
            byte[] blockOwnerBytes = customBlockData.get(blockOwnerKey, PersistentDataType.BYTE_ARRAY);
            UUID blockOwnerUUID = Util.getUUIDFromBytes(blockOwnerBytes);

            if (!Arrays.equals(playerBytes, blockOwnerBytes)) {
                if (!config.getTrustedPlayers(blockOwnerUUID).contains(String.valueOf(player.getUniqueId()))) {
                    if (player.hasPermission("ProtectoMyBlocko.bypass.highvalue") ) {
                        return true;
                    }
                    Player blockOwner = Bukkit.getPlayer(blockOwnerUUID);
                    if (blockOwner == null) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(blockOwnerUUID);
                        LocalDateTime lastOnline = Instant.ofEpochMilli(offlinePlayer.getLastPlayed())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime();
                        LocalDateTime abandonedDate = LocalDateTime.now().minusSeconds(config.getAbandonedDays());
                        if (lastOnline.isAfter(abandonedDate)) {
                            return false;
                        }else {
                            return true;
                        }
                    } else {
                        return false;
                    }
                }
            }
            return true;
        }catch(Exception e){
            Bukkit.getLogger().severe("An error occured when checking if a player can interact with block.");
            e.printStackTrace();
        }
        return true;
    }
}
