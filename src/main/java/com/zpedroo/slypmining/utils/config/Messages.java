package com.zpedroo.slypmining.utils.config;

import com.zpedroo.slypmining.FileUtils;
import org.bukkit.ChatColor;

public class Messages {

    /*
     * Returns the messages when
     * target is offline
     */
    public static final String OFFLINE_PLAYER = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.offline-player"));

    /*
     * Returns the messages when
     * amount is invalid
     */
    public static final String INVALID_AMOUNT = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.invalid-amount"));

    /*
     * Returns the messages when
     * virtual key is invalid
     */
    public static final String INVALID_KEY = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.invalid-key"));

    /*
     * Returns the messages when
     * player collects key
     */
    public static final String KEY_COLLECT = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.successful-key-collect"));

    /*
     * Returns the messages when
     * player collects reward
     */
    public static final String REWARD_COLLECT = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.successful-reward-collect"));

    /*
     * Returns the messages when
     * player collects all rewards
     */
    public static final String REWARD_COLLECT_ALL = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.successful-reward-collect-all"));

    /*
     * Returns the messages when
     * player no have rewards
     */
    public static final String WITHOUT_REWARDS = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.without-rewards"));

    /*
     * Returns the messages when
     * player uses the key
     */
    public static final String KEY_USED = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.key-used"));

    /*
     * Translate all String colors
     */
    private static String getColored(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
