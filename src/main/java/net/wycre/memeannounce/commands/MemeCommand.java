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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import static net.wycre.memeannounce.utils.CommonStrings.*;

/**
 * Handle /meme command
 */
public class MemeCommand implements TabExecutor {
    // Instance vars
    private final Main main;
    private FileConfiguration config;
    private Logger log;
    private Map<String, String> staticMap;
    private Map<String, String> dynamicMap;
    private List<String> staticKeyList;
    private List<String> dynamicKeyList;

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
        Player player;

        // Check if caller is player
        if (sender instanceof Player) {
            player = (Player) sender;
        }

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
                String message = StringManagement.color(staticMap.get(key)); // get string with key and add color
                main.getServer().broadcastMessage(message); // broadcast message
                return true;
            } // Broadcast random static message

            // Choose static message
            if (args[0].equalsIgnoreCase("static")) {
                getMessageLists(true);
                if (!staticMap.containsKey(args[1])) {
                    sender.sendMessage(MSG_KEY_NULL);
                    return true;
                } // Bad key

                String message = StringManagement.color(staticMap.get(args[1])); // get string with key and add color

                if (args[2].equalsIgnoreCase("preview")) {
                    sender.sendMessage(message);
                } else main.getServer().broadcastMessage(message);
                return true;
            } // Handle manual message selection

            // Choose dynamic message
            if (args[0].equalsIgnoreCase("dynamic")) {
                getMessageLists(false);
                if (!dynamicMap.containsKey(args[1])) {
                    sender.sendMessage(MSG_KEY_NULL);
                    return true;
                } // Bad key
                String message = StringManagement.color(dynamicMap.get(args[1])); // get string with key and add color
                String dynRep = StringManagement.argsToString(2, args); // get the replacement for %fill%

                String fMessage = message.replaceAll("%fill%", dynRep);

                if (args[3].equalsIgnoreCase("preview")) {
                    sender.sendMessage(fMessage);
                } else main.getServer().broadcastMessage(fMessage);
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
                String message = StringManagement.color(staticMap.get(key)); // get string with key and add color
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

        //TODO logic for handling tab completions for messages

        return null;
    }


    // Misc Methods
    /**
     * Obtain list of strings from config.yml, Fill sthe instanced map
     * @param isStatic true: will pull from staticMessages <br> false: will pull from dynamic messages
     */
    private void getMessageLists(boolean isStatic) {
        Map<String, String> map;

        // static logic
        if (isStatic) {
            List<String> staticList = config.getStringList(MEME_STATIC);
            // Loop through List, fix strings within, store in map
            for (String current : staticList) {
                String[] split = current.split(": ",2);
                staticMap.put(split[0], split[1]);
                staticKeyList.add(split[0]);
            }
        }

        // dynamic logic
        else {
            //TODO finish this
        }

    }

    private String getRandomKey(boolean isStatic) {
        Random       random    = new Random();
        List<String> keys;
        if (isStatic) {
            keys = new ArrayList<String>(staticMap.keySet());
        }
        else {
            keys = new ArrayList<String>(dynamicMap.keySet());
        }
        return keys.get( random.nextInt(keys.size()) );

    }

    private static void helpMessage(CommandSender sender) {
        sender.sendMessage("HELP MESSAGE"); //TODO make help context
    }

}
