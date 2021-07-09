package com.zpedroo.slypmining.listeners;

import com.zpedroo.slypmining.data.PlayerData;
import com.zpedroo.slypmining.managers.DataManager;
import com.zpedroo.slypmining.objects.Key;
import com.zpedroo.slypmining.utils.config.Messages;
import com.zpedroo.slypmining.utils.config.Sounds;
import de.tr7zw.nbtapi.NBTItem;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerGeneralListeners implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        DataManager.getInstance().save(DataManager.getInstance().load(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getItem().getType().equals(Material.AIR)) return;
        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK) && !(event.getAction() == Action.RIGHT_CLICK_AIR)) return;

        ItemStack item = event.getItem().clone();
        NBTItem nbt = new NBTItem(item);

        if (!nbt.hasKey("SlypMiningKey")) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        String type = nbt.getString("SlypMiningKey");
        Key key = DataManager.getInstance().getCache().getKey(type);
        PlayerData data = DataManager.getInstance().load(event.getPlayer());

        Integer amount = 1;

        if (player.isSneaking()) {
            amount = item.getAmount();
        }

        item.setAmount(amount);
        player.getInventory().removeItem(item);

        player.sendMessage(StringUtils.replaceEach(Messages.KEY_USED, new String[]{
                "{amount}"
        }, new String[]{
                amount.toString()
        }));

        data.giveKey(key, amount);

        Sounds sounds = new Sounds("activate");
        sounds.play(player);
    }
}