package com.zpedroo.slypmining.utils;

import com.zpedroo.slypmining.SlypMining;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryUtils {

    private static InventoryUtils instance;
    public static InventoryUtils getInstance() { return instance; }

    private HashMap<Inventory, List<Action>> inventoryActions;

    public InventoryUtils() {
        instance = this;
        this.inventoryActions = new HashMap<>(512);
        SlypMining.get().getServer().getPluginManager().registerEvents(new ActionListeners(), SlypMining.get()); // register inventory listener
    }

    public void addAction(Inventory inventory, ItemStack item, Runnable action, ActionClick click) {
        List<Action> actions = getInventoryActions().containsKey(inventory) ? getInventoryActions().get(inventory) : new ArrayList<>(40);

        actions.add(new Action(click, item, action));

        getInventoryActions().put(inventory, actions);
    }

    public HashMap<Inventory, List<Action>> getInventoryActions() {
        return inventoryActions;
    }

    public Action getAction(Inventory inventory, ItemStack item, ActionClick click) {
        for (Action action : getInventoryActions().get(inventory)) {
            if (action == null) continue;

            if (action.getClick() == click && action.getItem().isSimilar(item)) return action;
        }

        return null;
    }

    public static class Action {

        private ActionClick click;
        private ItemStack item;
        private Runnable action;

        public Action(ActionClick click, ItemStack item, Runnable action) {
            this.click = click;
            this.item = item;
            this.action = action;
        }

        public ActionClick getClick() {
            return click;
        }

        public ItemStack getItem() {
            return item;
        }

        public Runnable getAction() {
            return action;
        }

        public void run() {
            if (action == null) return;

            action.run();
        }
    }

    public class ActionListeners implements Listener {

        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onClick(InventoryClickEvent event) {
            if (!InventoryUtils.getInstance().getInventoryActions().containsKey(event.getInventory())) return;

            event.setCancelled(true);

            Action action = getAction(event.getInventory(), event.getCurrentItem(), ActionClick.ALL);

            if (action == null) {
                // try to found individual actions
                switch (event.getClick()) {
                    case LEFT:
                    case SHIFT_LEFT:
                        action = getAction(event.getInventory(), event.getCurrentItem(), ActionClick.LEFT);
                        break;
                    case RIGHT:
                    case SHIFT_RIGHT:
                        action = getAction(event.getInventory(), event.getCurrentItem(), ActionClick.RIGHT);
                        break;
                }
            }

            if (action == null) return;

            action.run();
        }
    }

    public enum ActionClick {
        LEFT,
        RIGHT,
        ALL
    }
}