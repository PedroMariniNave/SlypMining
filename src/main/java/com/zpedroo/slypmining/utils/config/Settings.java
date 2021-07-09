package com.zpedroo.slypmining.utils.config;

import com.zpedroo.slypmining.FileUtils;

import java.util.List;

public class Settings {

    /*
     * Returns the give key
     *
     * <cmd> <key>
     */
    public static final String GIVE_KEY = FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.keys.give");

    /*
     * Returns main command
     */
    public static final String COMMAND = FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.command");

    /*
     * Returns main command aliases
     */
    public static final List<String> ALIASES = FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Settings.aliases");
}