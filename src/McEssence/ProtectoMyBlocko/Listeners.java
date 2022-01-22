package McEssence.ProtectoMyBlocko;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
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
        final PersistentDataContainer customBlockData = new CustomBlockData(event.getBlock(), plugin);
        if (!customBlockData.has(blockOwnerKey, PersistentDataType.BYTE_ARRAY)) return;
        if (Instant.now().getEpochSecond() - customBlockData.get(protectionDateTimeKey, PersistentDataType.LONG) < config.getProtectionDelay()) return;
        try {
            if (!Arrays.equals(Util.getBytesFromUUID(event.getPlayer().getUniqueId()), (customBlockData.get(blockOwnerKey, PersistentDataType.BYTE_ARRAY)))) {
                if (!config.getTrustedPlayers(Util.getUUIDFromBytes(customBlockData.get(blockOwnerKey, PersistentDataType.BYTE_ARRAY))).contains(String.valueOf(event.getPlayer().getUniqueId()))) {
                    if (!event.getPlayer().hasPermission("ProtectoMyBlocko.bypass")) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(config.getCanNotBreak());
                    }
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("Could not get owner from block.");
            e.printStackTrace();
        }
    }
}
