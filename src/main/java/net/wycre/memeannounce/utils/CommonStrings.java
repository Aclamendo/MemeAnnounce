package net.wycre.memeannounce.utils;

import org.bukkit.ChatColor;

/**
 * A collection of strings intended to be used throughout the plugin
 */
public class CommonStrings {
    /**
     * Intended if the command caller is the console
     */
    public final static String CALLER_MUST_BE_PLAYER = "Only players can run this command";

    /**
     * If the player should lack the permission for a command
     */
    public final static String PLAYER_NEEDS_PERMISSION = ChatColor.RED + "You do not have permission to use this command!";
    // --Commented out by Inspection (1/12/2022 12:17 PM):public final static String PLAYER_ASKS_PERMISSION = ChatColor.RED + "Contact an administrator if you think you should have permission.";

    /**
     * If the player has made an error in command execution
     */
    public final static String PLAYER_HAND_EMPTY = ChatColor.RED + "You must be holding an item!";
    public final static String ARG_REQUIRES_INT = ChatColor.RED + "Line number must be an integer!";
    public final static String ARG_LESS_THAN_ONE = ChatColor.RED + "Line number must be greater than zero!";
    public final static String MISSING_LINE_ARG = ChatColor.RED + "Line number must be set!";
    public final static String MISSING_TEXT_ARG = ChatColor.RED + "You must specify some text!";
    public final static String MSG_KEY_NULL = ChatColor.RED + "That is not a valid message key!";

    /**
     * If the command is not applicable to a given item
     */
    public final static String ITEM_HAS_NO_LORE = ChatColor.RED + "Item already has no lore!";
    public final static String ITEM_NOT_ALLOWED = ChatColor.RED + "You are not allowed to do that to this item!";

    /**
     * Config check failures
     */
    public final static String CONFIG_VER_INVALID = ChatColor.LIGHT_PURPLE + "Invalid configVersion! ";
    public final static String CONFIG_IL_PROTECT_INVALID = ChatColor.LIGHT_PURPLE + "itemLore.protectedMaterials invalid element: ";


    /**
     * Config File Paths
     */
    public final static String MEME_STATIC = "meme.staticMessage";
    public final static String MEME_DYNAMIC = "meme.dynamicMessage";

    public final static String CONF_VER = "configVersion";
    public final static String IL_PROTECT = "itemLore.protectedMaterials";
}
