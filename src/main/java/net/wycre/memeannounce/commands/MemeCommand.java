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
    private Map<String, String> staticMessage;
    private Map<String, String> dynamicMessage;
    private List<String> staticMessageList;

    // Const
    public MemeCommand(Main main) {
        this.main = main;
        this.config = main.getConfig();
        this.log = main.getLogger();
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

            /*
            * TODO: logic paths
            *  - args[0] not set:
            *    - get random key
            *    - get string from static map using key
            *    - convert string to color and broadcast
            *  - args[0] == static:
            *    - display tab completions from staticMessageList
            *    - staticMessage.containsKey(args[1]) must return true
            *    - if args[2] == preview:
            *      - send staticMessage.get(args[1]) to sender
            *    - else:
            *      - broadcast staticMessage.get(args[1])
            *  - args[0] == dynamic:
            *    - display tab completions from dynamicMessageList
            *    - dynamicMessage.containsKey(args[1]) must return true
            *    - set dynamic replacement to args[2]
            *    - replace all instances of "%fill%" with dynamic replacement with String.replace
            *    - if args[3] == preview:
            *      - send dynamicMessage.get(args[1]) with replacements to sender
            *    - else:
            *      - broadcast dynamicMessage.get(args[1]) with replacements
            *  - args[0] == help:
            *    - send help message
            *  - args[0] else:
            *    - [default to dynamic selection]
            *    - set dynamic replacement to string in rest of args
            *    - get random key
            *    - get string from dynamic map using key
            *    - replace all instances of "%fill%" with dynamic replacement with String.replace
            *    - broadcast string
            */


        }






        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
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
                staticMessage.put(split[0], split[1]);
                staticMessageList.add(split[0]);
            }
        }


        // dynamic logic
        else {
            //TODO finish this
        }

    }

    private static String catStrings(int firstArgIndex, String[] strings) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(strings[firstArgIndex]); // Create initial word
        for (int i = firstArgIndex+1; i < strings.length; i++) { // Add all other words
            stringBuilder.append(": ").append(strings[i]);
        } // Add all other words
        // convert the stringBuilder into a string
        return stringBuilder.toString();
    }

    private String getRandomKey(boolean isStatic) {
        if (isStatic) {
            Random       random    = new Random();
            List<String> keys      = new ArrayList<String>(staticMessage.keySet());
            return keys.get( random.nextInt(keys.size()) );
        }
        else {
            Random       random    = new Random();
            List<String> keys      = new ArrayList<String>(dynamicMessage.keySet());
            return keys.get( random.nextInt(keys.size()) );
        }

    }

}
