package com.zpedroo.slypmining.objects;

import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Key {

    private String name;
    private ItemStack item;
    private ItemStack display;
    private List<Reward> rewards;

    public Key(String name, ItemStack item, ItemStack display, List<Reward> rewards) {
        this.name = name;
        this.item = item;
        this.display = display;
        this.rewards = rewards;
    }

    public String getName() {
        return name;
    }

    public ItemStack getItem() {
        return item.clone();
    }

    public ItemStack getDisplay(Integer amount) {
        ItemStack ret = display.clone();
        if (ret.getItemMeta() != null) {
            String displayName = ret.getItemMeta().hasDisplayName() ? ret.getItemMeta().getDisplayName() : null;
            List<String> lore = ret.getItemMeta().hasLore() ? ret.getItemMeta().getLore() : null;
            ItemMeta meta = ret.getItemMeta();

            if (displayName != null) meta.setDisplayName(StringUtils.replaceEach(displayName, new String[] {
                    "{amount}"
            }, new String[] {
                    amount.toString()
            }));

            if (lore != null) {
                List<String> newLore = new ArrayList<>(lore.size());

                for (String str : lore) {
                    newLore.add(StringUtils.replaceEach(str, new String[] {
                            "{amount}"
                    }, new String[] {
                            amount.toString()
                    }));
                }

                meta.setLore(newLore);
            }

            ret.setItemMeta(meta);
        }
        return ret;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public Reward getReward(String reward) {
        for (Reward rewards : getRewards()) {
            if (rewards == null) continue;
            if (!StringUtils.equals(rewards.getName(), reward)) continue;

            return rewards;
        }

        return null;
    }
}