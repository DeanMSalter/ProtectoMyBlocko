package McEssence.ProtectoMyBlocko;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin {
    Config config;

    @Override
    public void onEnable() {
        File f = new File(this.getDataFolder() + "/");
        if(!f.exists()) {
            f.mkdir();
        }
        getConfig().options().copyDefaults(true);
        saveConfig();
        File trustsFile = new File(this.getDataFolder() + File.separator + "trusts.yml");
        if(!trustsFile.exists()){
            try {
                trustsFile.createNewFile();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = new Config(this);
        if (!config.getEnabled()){
            Bukkit.getLogger().info(ChatColor.RED + " Disabled" + this.getName() + " As not enabled in config");
            return;
        }

        Bukkit.getLogger().info(ChatColor.GREEN + "Enabled" + this.getName());
        this.getCommand("ProtectoMyBlocko").setExecutor(new Commands(config, this));

        getServer().getPluginManager().registerEvents(new Listeners(config), this);

    }
    @Override
    public void onDisable() {
        Bukkit.getLogger().info(ChatColor.GREEN + "Disabled " + this.getName());
    }

}
