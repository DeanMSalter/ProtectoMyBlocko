package McEssence.ProtectoMyBlocko;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.configuration.file.YamlConfiguration.*;

public class Config{
    private final Main main;
    public Config(Main mainTemp){
        main = mainTemp;
    }

    public Boolean getEnabled(){
        return main.getConfig().getBoolean("general.enabled");
    }
    public String getCanNotBreak(){
        return main.getConfig().getString("messages.canNotBreak");
    }
    public String getCanNotOpen(){
        return main.getConfig().getString("messages.canNotOpen");
    }

    public int getProtectionDelay(){
        return main.getConfig().getInt("protectionDelay");
    }

    public ArrayList<Material> getExcludedBlocks(){
        ArrayList<Material> excludedBlocks = new ArrayList<>();
        try{
            ArrayList<String> excludedBlocksNames = (ArrayList<String>) main.getConfig().getList("excludeBlocks");
            for (String blockName : excludedBlocksNames) {
                excludedBlocks.add(Material.getMaterial(blockName));
            }
        }catch(Exception e){
            Bukkit.getLogger().info(ChatColor.RED + "Error occured when getting excluded blocks");

        }
        return excludedBlocks;
    }
    public ArrayList<String> getTrustedPlayers(UUID playerUUID) {
        ArrayList<String> trustedPlayers = new ArrayList<>();

        File trustsFile = new File(main.getDataFolder(), "trusts.yml");
        FileConfiguration trustsConfig = YamlConfiguration.loadConfiguration(trustsFile);
        if (trustsConfig.getList(String.valueOf(playerUUID)) != null) {
            trustedPlayers = (ArrayList<String>) trustsConfig.getList(String.valueOf(playerUUID));
        }
        return trustedPlayers;
    }
}
