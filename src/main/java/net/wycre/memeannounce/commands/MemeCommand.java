package net.wycre.memeannounce.commands;

import net.wycre.memeannounce.Main;
import net.wycre.memeannounce.utils.StringManagement;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;
import java.util.logging.Logger;

import static net.wycre.memeannounce.utils.CommonStrings.*;


/*
 * TODO:
 *  - Test permission implementation
 *  - Add color support for dynamic input & default to aqua
 *  - Message prefix & formatting
 *  - handling empty lists
 *  - config reloader method
 */

/**
 * Handle /meme command
 */
public class MemeCommand implements TabExecutor {
    // Instance vars
    private final Main main;
    private FileConfiguration config;
    private final Logger log;
    private final Map<String, String> staticMap = new HashMap<>(); // Stores messages for the static list
    private final Map<String, String> dynamicMap = new HashMap<>(); // Stores messages for the dynamic list
    private final List<String> staticKeyList = new ArrayList<>(); // Stores list of keys for static messages
    private final List<String> dynamicKeyList = new ArrayList<>(); // Stores list of keys for dynamic messages
    private final List<String> NO_ARGS_TAB_COMPLETE = new ArrayList<>(Arrays.asList("static", "dynamic", "help")); // Stores other options for tab completion
    private final Map<UUID, Long> coolDownMap = new HashMap<>(); // PlayerName, time in millis when player last ran command
    private long coolDownTime;

    // Const
    public MemeCommand(Main main) {
        this.main = main;
        this.config = main.getConfig();
        this.log = main.getLogger();
        getMessageLists(true);
        getMessageLists(false);
        coolDownTime = main.getConfig().getLong("meme.cooldown") * 1000;
    }

    // Command Logic
    @Override
    public boolean onCommand( @NonNull CommandSender sender,
                              @NonNull Command command,
                              @NonNull String label,
                              String[] args) {
        // Local vars
        boolean senderPerm = false;
        config = main.getConfig(); // TODO remove this when config reloader is established
        /*
        * !~Checks performed for command execution:
        * Check if sender is player
        *    Check if sender has cool down
        */

        // Check if player has cooldown
        if (sender instanceof Player) {
            if (!(sender.hasPermission("wycre.meme.cooldown"))) {
                if (coolDownMap.containsKey(((Player) sender).getUniqueId())) {
                    long lastTime = coolDownMap.get(((Player) sender).getUniqueId());
                    long timeSince = System.currentTimeMillis() - lastTime;
                    if (timeSince < coolDownTime) {
                        int waitTimeSec = (int) ((coolDownTime - timeSince) / 1000);
                        sender.sendMessage(PLAYER_HAS_COOLDOWN + waitTimeSec + "seconds left");
                        return true;
                    } // Cooldown still in effect
                } // sender has cooldown
            } // Sender is cooldown exempt
        }

        // Unify op and console perm; set senderPerm
        if ((sender.isOp() || sender instanceof ConsoleCommandSender)) {
            senderPerm = true;
        }

        // Check for /meme name
        if (command.getName().equalsIgnoreCase("meme")) {
            // No args specified gets random static
            if (args.length == 0 && (sender.hasPermission("wycre.meme.static") || senderPerm)) {
                getMessageLists(true); // Generate the list of static strings TODO remove this when config reloader is established
                String key = getRandomKey(true); // get random key from static strings map
                String message = staticMap.get(key); // get string with key and add color
                main.getServer().broadcastMessage(message); // broadcast message
                if (sender instanceof Player) {
                    coolDownMap.put(((Player) sender).getUniqueId(), System.currentTimeMillis());
                } // Set cooldown
            } // Broadcast random static message

            // Check for choosy perm
            else if (sender.hasPermission("wycre.meme.choose")) {
                // Choose static message
                if (args[0].equalsIgnoreCase("static") && (sender.hasPermission("wycre.meme.static") || senderPerm)) {
                    getMessageLists(true); // TODO remove this when config reloader is established

                    if (args.length < 3) {
                        sender.sendMessage(MSG_KEY_NULL);
                        return true;
                    } // missing key

                    if (!staticMap.containsKey(args[1])) {
                        sender.sendMessage(MSG_KEY_INVALID);
                        return true;
                    } // Bad key

                    String message = staticMap.get(args[1]); // get string with key and add color

                    main.getServer().broadcastMessage(message);
                } // Handle manual message selection
                // Choose dynamic message
                else if (args[0].equalsIgnoreCase("dynamic") && (sender.hasPermission("wycre.meme.dynamic") || senderPerm)) {
                    getMessageLists(false); // TODO remove this when config reloader is established

                    if (args.length < 3) {
                        sender.sendMessage(MSG_KEY_NULL);
                        return true;
                    } // missing key

                    if (!dynamicMap.containsKey(args[1])) {
                        sender.sendMessage(MSG_KEY_INVALID);
                        return true;
                    } // Bad key


                    String message = dynamicMap.get(args[1]); // get string with key and add color
                    String dynRep = StringManagement.argsToString(2, args); // get the replacement for %fill%

                    String fMessage = message.replaceAll("%fill%", dynRep);

                    main.getServer().broadcastMessage(fMessage);
                }
                if (sender instanceof Player) {
                    coolDownMap.put(((Player) sender).getUniqueId(), System.currentTimeMillis());
                } // Set cooldown
            } // Choosy perm
            // Help message
            else if (args[0].equalsIgnoreCase("help")) {
                helpMessage(sender);
            }

            // if an arg is present, default to dynamic
            else if ((sender.hasPermission("wycre.meme.dynamic") || senderPerm)){
                getMessageLists(false); // Generate the list of static strings TODO remove this when config reloader is established
                String key = getRandomKey(false); // get random key from static strings map
                String message = dynamicMap.get(key); // get string with key and add color
                String dynRep = StringManagement.argsToString(0, args); // get the replacement for %fill%

                String fMessage = message.replaceAll("%fill%", dynRep);

                main.getServer().broadcastMessage(fMessage); // broadcast message
                if (sender instanceof Player) {
                    coolDownMap.put(((Player) sender).getUniqueId(), System.currentTimeMillis());
                } // Set cooldown
            }
            return true;
        }
        return false;
    }


    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender,
                                      @NonNull Command command,
                                      @NonNull String alias,
                                      String[] args) {
        if (args.length == 1) {

            List<String> message = NO_ARGS_TAB_COMPLETE;

            for (Player p : main.getServer().getOnlinePlayers()) {
                message.add(p.getDisplayName());
            }

            return message;
        } // returns list of players and "static" + "dynamic"

        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("static")) {
                return staticKeyList;
            }
            if (args[0].equalsIgnoreCase("dynamic")) {
                return dynamicKeyList;
            }
        }
        return null;
    }


    // Misc Methods
    /**
     * Obtain list of strings from config.yml, Fill the instanced map
     * @param isStatic true: will pull from staticMessages <br> false: will pull from dynamic messages
     */
    private void getMessageLists(boolean isStatic) {
        // static logic
        if (isStatic) {
            List<String> staticList = config.getStringList(MEME_STATIC);
            // Loop through List, fix strings within, store in map
            for (String current : staticList) {
                String[] split = current.split(": ",2);
                staticMap.put(split[0], StringManagement.color(split[1]));
                staticKeyList.add(split[0]);
            }
        }

        // dynamic logic
        else {
            List<String> dynList = config.getStringList(MEME_DYNAMIC);
            // Loop through list. Separate key and values
            for (String current : dynList) {
                String[] cSplit = current.split(": ", 2);
                dynamicMap.put(cSplit[0], StringManagement.color(cSplit[1]));
                dynamicKeyList.add(cSplit[0]);
            }

        }

    }

    private String getRandomKey(boolean isStatic) {
        Random       random    = new Random();
        List<String> keys;
        if (isStatic) {
            keys = new ArrayList<>(staticMap.keySet());
        }
        else {
            keys = new ArrayList<>(dynamicMap.keySet());
        }
        return keys.get( random.nextInt(keys.size()) );

    }

    private static void helpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_RED + "==============================================");
        sender.sendMessage(ChatColor.AQUA + "                         /meme usage");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.DARK_GREEN + "/meme");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "    Will make an important PSA");
        sender.sendMessage(ChatColor.DARK_GREEN + "/meme <text>");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "    Will make an important PSA that includes the text");
        sender.sendMessage(ChatColor.DARK_GREEN + "/meme static");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "    Choose an important PSA to send");
        sender.sendMessage(ChatColor.DARK_GREEN + "/meme dynamic <text>");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "    Choose an important PSA that includes the text");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.DARK_RED + "==============================================");
    }

}
