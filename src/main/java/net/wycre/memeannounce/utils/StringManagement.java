package net.wycre.memeannounce.utils;

import org.bukkit.ChatColor;

import java.util.List;

/**
 * Static class containing some methods useful to string modifications
 * @author Wycre
 */
public class StringManagement {

    // Build String From Args

    /**
     * Takes array of values and concatenates all values after firstArgIndex into a single string
     * @param firstArgIndex
     * @param args array of values
     * @return
     */
    public static String argsToString(int firstArgIndex, String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(args[firstArgIndex]); // Create initial word
        for (int i = firstArgIndex+1; i < args.length; i++) { // Add all other words
            stringBuilder.append(" ").append(args[i]);
        } // Add all other words
        // convert the stringBuilder into a string
        return stringBuilder.toString();
    }

    /**
     * Translates '&' from color codes within a List to the correct char
     * Call with <code> this.color()</code>
     * @param stringList String List containing color codes
     * @return String List with the correct color code delimiters
     */
    public static List<String> color(List<String> stringList){

        for (int i = 0; i < stringList.size(); i++) {
            stringList.set(i, color(stringList.get(i)));
        }
        return stringList;
    }

    /**
     * Translates '&' from color codes within a string to the correct char
     * @param string That contains alternate color codes
     * @return String that has replaced the color codes with the correct ones
     * @author Remceau
     */
    public static String color(String string){
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
