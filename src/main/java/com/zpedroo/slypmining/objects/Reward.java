package com.zpedroo.slypmining.objects;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Reward {

    private ItemStack display;
    private String name;
    private Double chance;
    private List<String> commands;
    private List<String> messages;
    private Key key;

    public Reward(ItemStack display, String name, Double chance, List<String> commands, List<String> messages) {
        this.display = display;
        this.name = name;
        this.chance = chance;
        this.commands = commands;
        this.messages = messages;
    }

    public ItemStack getDisplay() {
        return display;
    }

    public String getName() {
        return name;
    }

    public Double getChance() {
        return chance;
    }

    public List<String> getCommands() {
        return commands;
    }

    public List<String> getMessages() {
        return messages;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public void collect(Player player) {
        for (String cmd : getCommands()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), StringUtils.replaceEach(cmd, new String[]{
                    "{player}"
            }, new String[]{
                    player.getName()
            }));
        }
    }
}