package McEssence.ProtectoMyBlocko;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;


public class Commands implements CommandExecutor {
    private final Config config;
    private final Main main;
    Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("ProtectoMyBlocko");
    public Commands(Config configTemp, Main mainTemp){
        config = configTemp;
        main = mainTemp;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args == null || args.length == 0) {
            return false;
        }
        switch(args[0].toUpperCase()) {
            case "RELOAD":
                if (hasPermission(commandSender, "ProtectoMyBlocko.admin.reload", true)) {
                    reload(commandSender, command, label, args);
                }
                break;
            case "TRUST":
                Bukkit.getLogger().info(ChatColor.RED + commandSender.getName());
                Bukkit.getLogger().info(hasPermission(commandSender, "ProtectoMyBlocko.player.trust", false).toString());
                Bukkit.getLogger().info(Arrays.toString(args));
                if (hasPermission(commandSender, "ProtectoMyBlocko.player.trust", false)) {
                    trust(commandSender, command, label, args);
                }
                break;
            case "UNTRUST":
                if (hasPermission(commandSender, "ProtectoMyBlocko.player.untrust", false)) {
                    unTrust(commandSender, command, label, args);
                }
                break;
            case "TRUSTLIST":
                if (hasPermission(commandSender, "ProtectoMyBlocko.player.trustlist", false)) {
                    trustList(commandSender, command, label, args);
                }
                break;
            case "TRUSTLISTOTHER":
                if (hasPermission(commandSender, "ProtectoMyBlocko.admin.trustlistother", false)) {
                    trustListOther(commandSender, command, label, args);
                }
                break;
            default:
                break;
        }
        return true;
    }

    private Boolean trust(CommandSender commandSender, Command command, String s, String[] args){
        try {
            Bukkit.getLogger().info("inside trust");
            if (!checkPlayerRan(commandSender)) {
                commandSender.sendMessage("Command not ran by player");
                return true;
            }

            if (!checkPlayerNameSupplied(args)){
                commandSender.sendMessage("No player name supplied");
                return true;
            }

            Player playerToTrust = Bukkit.getServer().getPlayer(args[1]);

            if (playerToTrust == null) {
                commandSender.sendMessage("Player not found");
                return true;
            }
            Player player = (Player) commandSender;

            File trustsFile = new File(main.getDataFolder(), "trusts.yml");
            FileConfiguration trustsConfig = YamlConfiguration.loadConfiguration(trustsFile);

            ArrayList<String> trustedPlayers = new ArrayList<>();
            if (trustsConfig.getList(String.valueOf(player.getUniqueId())) != null) {
                trustedPlayers = (ArrayList<String>) trustsConfig.getList(String.valueOf(player.getUniqueId()));
            }

            String playerUUIDToAdd = String.valueOf(playerToTrust.getUniqueId());
            Bukkit.getLogger().info(playerUUIDToAdd);
            if (!trustedPlayers.contains(playerUUIDToAdd)) {
                trustedPlayers.add(playerUUIDToAdd);
                trustsConfig.set(String.valueOf(player.getUniqueId()), trustedPlayers);
                trustsConfig.save(trustsFile);
                Bukkit.getLogger().info(trustedPlayers.toString());
                commandSender.sendMessage("Successfully trusted player.");
            } else {
                commandSender.sendMessage("Player already trusted.");
                Bukkit.getLogger().info("already trusted");
                Bukkit.getLogger().info(trustedPlayers.toString());
            }
            return true;
        }catch(Exception e) {
            Bukkit.getLogger().info(ChatColor.RED + "Exception " + e.getMessage());
            return false;
        }
    }

    private Boolean unTrust(CommandSender commandSender, Command command, String s, String[] args){
        try {
            if (!checkPlayerRan(commandSender)) {
                commandSender.sendMessage("Command not ran by player");
                return true;
            }

            if (!checkPlayerNameSupplied(args)){
                commandSender.sendMessage("No player name supplied");
                return true;
            }

            String playerNameToUntrust = args[1];
            Player player = (Player) commandSender;

            File trustsFile = new File(main.getDataFolder(), "trusts.yml");
            FileConfiguration trustsConfig = YamlConfiguration.loadConfiguration(trustsFile);

            ArrayList<String> trustedPlayers = new ArrayList<>();
            if (trustsConfig.getList(String.valueOf(player.getUniqueId())) != null) {
                trustedPlayers = (ArrayList<String>) trustsConfig.getList(String.valueOf(player.getUniqueId()));
            }
            String UUIDOfPlayerToUntrust = null;
            for (String trustedPlayer : trustedPlayers) {
                String trustedPlayerName = Bukkit.getServer().getOfflinePlayer(UUID.fromString(trustedPlayer)).getName();
                if (trustedPlayerName.equalsIgnoreCase(playerNameToUntrust)) {
                    UUIDOfPlayerToUntrust = String.valueOf(UUID.fromString(trustedPlayer));
                    break;
                }
            }
            if (UUIDOfPlayerToUntrust == null) {
                commandSender.sendMessage("You have not trusted that player.");
                return true;
            }
            trustedPlayers.remove(UUIDOfPlayerToUntrust);
            trustsConfig.set(String.valueOf(player.getUniqueId()), trustedPlayers);
            trustsConfig.save(trustsFile);
            commandSender.sendMessage("You have unTrusted " + playerNameToUntrust);

            return true;
        }catch(Exception e) {
            Bukkit.getLogger().info(ChatColor.RED + "Exception " + e.getMessage());
            return false;
        }
    }

    private Boolean trustList(CommandSender commandSender, Command command, String s, String[] args){
        try {
            if (!checkPlayerRan(commandSender)) {
                commandSender.sendMessage("Command not ran by player");
                return true;
            }

            Player player = (Player) commandSender;

            File trustsFile = new File(main.getDataFolder(), "trusts.yml");
            FileConfiguration trustsConfig = YamlConfiguration.loadConfiguration(trustsFile);

            ArrayList<String> trustedPlayers = new ArrayList<>();
            if (trustsConfig.getList(String.valueOf(player.getUniqueId())) != null) {
                trustedPlayers = (ArrayList<String>) trustsConfig.getList(String.valueOf(player.getUniqueId()));
            }
            String trustedPlayersString = "";
            for (String trustedPlayer : trustedPlayers) {
                String trustedPlayerName = Bukkit.getServer().getOfflinePlayer(UUID.fromString(trustedPlayer)).getName();
                trustedPlayersString = trustedPlayersString + trustedPlayerName;
            }
            if (trustedPlayersString == "") {
                commandSender.sendMessage("You have not trusted any players.");
                return true;
            } else {
                commandSender.sendMessage(trustedPlayersString);
            }
            return true;
        }catch(Exception e) {
            Bukkit.getLogger().info(ChatColor.RED + "Exception " + e.getMessage());
            return false;
        }
    }

    private Boolean trustListOther(CommandSender commandSender, Command command, String s, String[] args){
        try {
            File trustsFile = new File(main.getDataFolder(), "trusts.yml");
            FileConfiguration trustsConfig = YamlConfiguration.loadConfiguration(trustsFile);
            Player player = Bukkit.getServer().getPlayer(args[1]);
            ArrayList<String> trustedPlayers = new ArrayList<>();
            if (trustsConfig.getList(String.valueOf(player.getUniqueId())) != null) {
                trustedPlayers = (ArrayList<String>) trustsConfig.getList(String.valueOf(player.getUniqueId()));
            }
            String trustedPlayersString = "";
            for (String trustedPlayer : trustedPlayers) {
                String trustedPlayerName = Bukkit.getServer().getOfflinePlayer(UUID.fromString(trustedPlayer)).getName();
                trustedPlayersString = trustedPlayersString + trustedPlayerName;
            }
            if (trustedPlayersString == "") {
                commandSender.sendMessage("They have not trusted any players.");
                return true;
            } else {
                commandSender.sendMessage(trustedPlayersString);
            }
            return true;
        }catch(Exception e) {
            Bukkit.getLogger().info(ChatColor.RED + "Exception " + e.getMessage());
            return false;
        }
    }

    private Boolean reload(CommandSender commandSender, Command command, String s, String[] args){
        plugin.reloadConfig();
        commandSender.sendMessage("Reload Complete");
        return true;
    }



    private Boolean checkPlayerRan(CommandSender commandSender){
        return commandSender instanceof Player;
    }
    private Boolean checkPlayerNameSupplied(String[] args){
        return args != null && args[1] != null;
    }
    private Boolean hasPermission(CommandSender commandSender, String permission, boolean allowConsole) {
        if (!(commandSender instanceof Player) && !allowConsole) {
            commandSender.sendMessage("This command can not be run from the console.");
            return false;
        }else if(!(commandSender instanceof Player)) {
            return true;
        }
        Player player = (Player) commandSender;
        if (player.hasPermission(permission)) {
            return true;
        }else {
            commandSender.sendMessage("You do not have permission.");
        }
        return false;
    }
}

