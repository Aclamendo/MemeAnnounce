package net.wycre.memeannounce.utils;

import net.wycre.memeannounce.Main;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.List;

import static net.wycre.memeannounce.utils.CommonStrings.*;

public class ConfigChecker {

    private FileConfiguration config;
    private String relevant; // String copy of the relevant line in a config syntax error
    private Main main;

    // Constructor
    public ConfigChecker(Main main, FileConfiguration config) {
        this.config = config;
        this.main = main;
    }



    /**
     * Comprehensive check of entire config. <br> If no exception is thrown, the check passes successfully
     * @throws InvalidConfigurationException if the config file has an error
     */
    public void compCheck() throws InvalidConfigurationException {

        // Check version (Future releases may have different config defaults
        if (!checkConfigVersion()) { throw new InvalidConfigurationException(CONFIG_VER_INVALID + relevant); }
        if (!checkItemLoreProtected()) { throw new InvalidConfigurationException(CONFIG_IL_PROTECT_INVALID + relevant); }
    }









    public boolean checkConfigVersion() {
        int configVer = config.getInt(CONF_VER);
        return configVer == 1;
    }

    public boolean checkItemLoreProtected() {
        List<String> list = config.getStringList(IL_PROTECT);
        for (String current : list) {
            String[] split = current.split(":");
            if (split.length != 2) {
                relevant = current;
                return false;
            }
        }
        return true;
    }


}
