package McEssence.ProtectoMyBlocko;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Config{
    private final Main main;
    public Config(Main mainTemp){
        main = mainTemp;
    }

    public Boolean getEnabled(){
        return main.getConfig().getBoolean("general.enabled");
    }
}
