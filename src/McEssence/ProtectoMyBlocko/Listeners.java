package McEssence.ProtectoMyBlocko;

import org.bukkit.Bukkit;
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

import java.util.Arrays;

public class Listeners implements Listener {
    private final Config config;

    Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("ProtectoMyBlocko");
    private final NamespacedKey blockOwnerKey = new NamespacedKey(plugin, "blockOwner");

    public Listeners(Config configTemp){
        config = configTemp;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event){
        PersistentDataContainer customBlockData = new CustomBlockData(event.getBlock(), plugin);
        customBlockData.set(blockOwnerKey, PersistentDataType.BYTE_ARRAY, Util.getBytesFromUUID(event.getPlayer().getUniqueId()));
    }
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event){
        final PersistentDataContainer customBlockData = new CustomBlockData(event.getBlock(), plugin);
        if (!customBlockData.has(blockOwnerKey, PersistentDataType.BYTE_ARRAY)) return;
        try {
            if (!Arrays.equals(Util.getBytesFromUUID(event.getPlayer().getUniqueId()), (customBlockData.get(blockOwnerKey, PersistentDataType.BYTE_ARRAY)))) {
                if (!event.getPlayer().hasPermission("ProtectoMyBlocko.bypass")) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("cant break this loser");
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("Could not get owner from block.");
            e.printStackTrace();
        }
    }

//    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
//    public void onItemDrop(PlayerDropItemEvent event){
//        if (!config.getEnabled()){
//            return;
//        }
//        if (event.getItemDrop().getItemStack().getType() == config.getGlowstickMaterial()) {
//            event.getItemDrop().setVelocity(event.getItemDrop().getVelocity().multiply(config.getThrowDistanceMultiplier()));
//        }
//    }
}
