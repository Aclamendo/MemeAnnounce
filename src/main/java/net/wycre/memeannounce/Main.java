package net.wycre.memeannounce;

import net.wycre.memeannounce.commands.MemeCommand;
import net.wycre.memeannounce.utils.ConfigChecker;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;


/**
 * TODO:
 *  - Properly implement config validation
 *  - properly implement config reloading
 */

public final class Main extends JavaPlugin {

    private FileConfiguration config;
    private Logger log = this.getLogger();
    private boolean validConfig;

    // Primary plugin logic register
    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnable() {

        // Check and Register config
        if (!(new File(getDataFolder(), "config.yml").isFile())) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
    /*    if (!checkLocalConfig()) {
            log.warning(ChatColor.RED + "" + ChatColor.BOLD + "Config check failed! See trace for error!");
            getPluginLoader().disablePlugin(this);
        }
        else { */
            saveDefaultConfig();
            validConfig = true;
        MemeCommand meme = new MemeCommand(this);

        getCommand("meme").setExecutor(meme);

        getCommand("meme").setTabCompleter(meme);

            // Instantiate command classes




        //}
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    // Misc Methods
    /**
     * Check if the instanced config file is valid. Will print stack if error occurred
     * @return true if the file is valid, false if an error occurred
     */
    private boolean checkLocalConfig() {
        File cFile = new File(getDataFolder(), "config.yml");
        FileConfiguration checkFile = YamlConfiguration.loadConfiguration(cFile);
        ConfigChecker confCheck = new ConfigChecker(this, checkFile);
        try {
            confCheck.compCheck(); // Check if config is correct
            return true;
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            return false;
        }
    }

}
