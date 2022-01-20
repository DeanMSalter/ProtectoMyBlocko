package McEssence.ProtectoMyBlocko;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class Commands implements CommandExecutor {
    Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("ProtectoMyBlocko");
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            commandSender.sendMessage("Reload Complete");
        }
        return true;
    }
}
