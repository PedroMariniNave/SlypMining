package com.zpedroo.slypmining.utils.config;

import com.zpedroo.slypmining.FileUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Sounds {

    private Sound sound;
    private Float volume;
    private Float pitch;

    public Sounds(String soundName) {
        if (FileUtils.get().getBoolean(FileUtils.Files.CONFIG, "Sounds." + soundName + ".use")) {
            String[] split = FileUtils.get().getString(FileUtils.Files.CONFIG, "Sounds." + soundName + ".sound").split(",");

            this.sound = Sound.valueOf(split[0]);
            this.volume = Float.valueOf(split[1]);
            this.pitch = Float.valueOf(split[2]);
        }
    }

    public void play(Player player) {
        if (sound == null || volume == null || pitch == null) return;
        if (player == null) return;

        player.playSound(player.getLocation(), sound, volume, pitch);
    }
}