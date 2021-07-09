package com.zpedroo.slypmining.commands;

import com.zpedroo.slypmining.managers.DataManager;
import com.zpedroo.slypmining.objects.Key;
import com.zpedroo.slypmining.utils.config.Messages;
import com.zpedroo.slypmining.utils.config.Settings;
import com.zpedroo.slypmining.utils.menu.Menus;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MainCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = sender instanceof Player ? (Player) sender : null;

        if (args.length > 3 && sender.hasPermission("slypmining.admin")) {
            CommandKeys cmdKey = getKey(args[0].toUpperCase());
            if (cmdKey != null) {
                Player target =  Bukkit.getPlayer(args[1]);

                switch (cmdKey) {
                    case GIVE:
                        if (target == null) {
                            sender.sendMessage(Messages.OFFLINE_PLAYER);
                            return true;
                        }

                        Key key = DataManager.getInstance().getCache().getKey(args[2]);

                        if (key == null) {
                            sender.sendMessage(Messages.INVALID_KEY);
                            return true;
                        }

                        Integer amount = null;

                        try {
                            amount = Integer.parseInt(args[3]);
                        } catch (Exception ex) {
                            // ignore
                        }

                        if (amount == null || amount <= 0) {
                            sender.sendMessage(Messages.INVALID_AMOUNT);
                            return true;
                        }

                        ItemStack item = key.getItem();
                        item.setAmount(amount);

                        target.getInventory().addItem(item);
                        return true;
                }
            }
        }

        if (player == null) return true;

        Menus.getInstance().openMainMenu(player);
        return false;
    }

    private CommandKeys getKey(String str) {
        for (CommandKeys keys : CommandKeys.values()) {
            if (StringUtils.equals(keys.getKey(), str)) return keys;
        }

        return null;
    }

    public enum CommandKeys {
        GIVE(Settings.GIVE_KEY);

        private String key;

        CommandKeys(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }
}