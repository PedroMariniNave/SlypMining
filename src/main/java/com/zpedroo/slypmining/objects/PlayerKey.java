package com.zpedroo.slypmining.objects;

import com.zpedroo.slypmining.data.PlayerData;
import com.zpedroo.slypmining.managers.DataManager;
import com.zpedroo.slypmining.utils.config.Messages;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class PlayerKey {

    private Key key;
    private Integer amount;

    public PlayerKey(Key key, Integer amount) {
        this.key = key;
        this.amount = amount;
    }

    public Key getKey() {
        return key;
    }

    public Integer getAmount() {
        return amount;
    }

    public void addAmount(Integer amount) {
        this.amount += amount;
    }

    public void removeAmount(Integer amount) {
        this.amount -= amount;
    }

    public void open(Player player, Integer amount) {
        PlayerData data = DataManager.getInstance().load(player);
        data.removeKey(getKey(), amount);

        for (int i = 0; i < amount; ++i) {
            boolean prize = false;

            while (!prize) {
                for (Reward reward : getKey().getRewards()) {
                    Double chance = reward.getChance();

                    if (new Random().nextDouble() * 100D <= chance) {
                        prize = true;
                        data.giveReward(reward);

                        for (String msg : reward.getMessages()) {
                            if (msg == null) break;

                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                        }
                        break;
                    }
                }
            }
        }
    }

    public void collect(Player player, Integer amount) {
        PlayerData data = DataManager.getInstance().load(player);

        Integer toGive = getFreeSpace(player, getKey().getItem(), amount);
        if (toGive <= 0) return;

        data.removeKey(getKey(), toGive);

        ItemStack item = getKey().getItem();
        item.setAmount(toGive);

        player.getInventory().addItem(item);
        player.sendMessage(StringUtils.replaceEach(Messages.KEY_COLLECT, new String[]{
                "{amount}"
        }, new String[]{
                toGive.toString()
        }));
    }

    private int getFreeSpace(Player player, ItemStack item, Integer limit) {
        int maxStack = item.getMaxStackSize();
        int freeSlots = 0;

        for (ItemStack items : player.getInventory().getContents()) {
            if (freeSlots * maxStack >= limit) break;
            if (items == null || items.getType().equals(Material.AIR)) freeSlots += 1;
        }

        int ret = freeSlots * maxStack;

        return ret > limit ? limit : ret;
    }
}