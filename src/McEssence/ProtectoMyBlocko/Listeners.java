package McEssence.ProtectoMyBlocko;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.time.Instant;
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
        if (event.getPlayer().hasPermission("ProtectoMyBlocko.protect")) {
            if (!config.getExcludedBlocks().contains(event.getBlock().getType())) {
                PersistentDataContainer customBlockData = new CustomBlockData(event.getBlock(), plugin);
                customBlockData.set(blockOwnerKey, PersistentDataType.BYTE_ARRAY, Util.getBytesFromUUID(event.getPlayer().getUniqueId()));
                customBlockData.set(protectionDateTimeKey, PersistentDataType.LONG, Instant.now().getEpochSecond());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event){
        if (!IsAllowed(event.getBlock(),event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(config.getCanNotBreak());
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
        } else {
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
}
