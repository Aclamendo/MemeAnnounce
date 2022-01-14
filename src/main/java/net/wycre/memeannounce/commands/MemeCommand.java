package net.wycre.memeannounce.commands;

import net.wycre.memeannounce.Main;
import net.wycre.memeannounce.utils.StringManagement;
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
 *  - Handle permissions for different command options
 *  - Add a help context
 *  - Add color support for dynamic input
 */

/**
 * Handle /meme command
 */
public class MemeCommand implements TabExecutor {
    // Instance vars
    private final Main main;
    private FileConfiguration config;
    private final Logger log;
    private final Map<String, String> staticMap = new HashMap<>();
    private final Map<String, String> dynamicMap = new HashMap<>();
    private final List<String> staticKeyList = new ArrayList<>();
    private final List<String> dynamicKeyList = new ArrayList<>();
    private final List<String> NO_ARGS_TAB_COMPLETE = new ArrayList<>(Arrays.asList("static", "dynamic"));

    // Const
    public MemeCommand(Main main) {
        this.main = main;
        this.config = main.getConfig();
        this.log = main.getLogger();
        getMessageLists(true);
        getMessageLists(false);
    }

    // Command Logic
    @Override
    public boolean onCommand( @NonNull CommandSender sender,
                              @NonNull Command command,
                              @NonNull String label,
                              String[] args) {
        config = main.getConfig();

        // Check if caller has permission to run command
        if (!(sender.isOp() || sender.hasPermission("wycre.meme") || sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(PLAYER_NEEDS_PERMISSION);
            return true;
        }

        // Check for /meme name
        if (command.getName().equalsIgnoreCase("meme")) {
            // No args specified gets random static
            if (args.length == 0) {
                getMessageLists(true); // Generate the list of static strings
                String key = getRandomKey(true); // get random key from static strings map
                String message = staticMap.get(key); // get string with key and add color
                main.getServer().broadcastMessage(message); // broadcast message
                return true;
            } // Broadcast random static message

            // Choose static message
            if (args[0].equalsIgnoreCase("static")) {
                getMessageLists(true);

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
                return true;
            } // Handle manual message selection

            // Choose dynamic message
            if (args[0].equalsIgnoreCase("dynamic")) {
                getMessageLists(false);

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
                return true;
            }

            // Help message
            if (args[0].equalsIgnoreCase("help")) {
                helpMessage(sender);
            }

            // if an arg is present, default to dynamic
            else {
                getMessageLists(false); // Generate the list of static strings
                String key = getRandomKey(false); // get random key from static strings map
                String message = dynamicMap.get(key); // get string with key and add color
                String dynRep = StringManagement.argsToString(0, args); // get the replacement for %fill%

                String fMessage = message.replaceAll("%fill%", dynRep);

                main.getServer().broadcastMessage(fMessage); // broadcast message
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
        sender.sendMessage("HELP MESSAGE"); //TODO make help context
    }

}
