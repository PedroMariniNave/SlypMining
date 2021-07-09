package com.zpedroo.slypmining.utils.menu;

import com.zpedroo.slypmining.FileUtils;
import com.zpedroo.slypmining.data.PlayerData;
import com.zpedroo.slypmining.managers.DataManager;
import com.zpedroo.slypmining.objects.PlayerKey;
import com.zpedroo.slypmining.objects.Reward;
import com.zpedroo.slypmining.utils.InventoryUtils;
import com.zpedroo.slypmining.utils.builder.ItemBuilder;
import com.zpedroo.slypmining.utils.config.Messages;
import com.zpedroo.slypmining.utils.config.Sounds;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Menus {

    private static Menus instance;
    public static Menus getInstance() { return instance; }

    public Menus() {
        instance = this;
        new InventoryUtils();
    }

    public void openMainMenu(Player player) {
        FileUtils.Files file = FileUtils.Files.MAIN;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        Inventory defaultInventory = Bukkit.createInventory(null, size, title);

        List<Integer> slots = new ArrayList<>(54);
        List<ItemStack> items = new ArrayList<>(54);
        List<InventoryUtils.Action> actions = new ArrayList<>(54);

        List<PlayerKey> keys = DataManager.getInstance().load(player).getKeys();

        if (keys.size() <= 0) {
            int slot = FileUtils.get().getInt(file, "Nothing.slot");
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Nothing");

            slots.add(slot);
            items.add(item);
        } else {
            String[] slotsSplit = FileUtils.get().getString(file, "Inventory.key-slots").replace(" ", "").split(",");
            int i = -1;

            for (PlayerKey key : keys) {
                int slot = Integer.parseInt(slotsSplit[++i]);
                ItemStack item = key.getKey().getDisplay(key.getAmount());
                InventoryUtils.Action action = new InventoryUtils.Action(InventoryUtils.ActionClick.ALL, item, () -> {
                    openKeyMenu(player, key);
                });

                slots.add(slot);
                items.add(item);
                actions.add(action);
            }
        }

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str);

            String actionStr = FileUtils.get().getString(file, "Inventory.items." + str + ".action");

            if (!StringUtils.equals(actionStr, "NULL")) {
                switch (actionStr.toUpperCase()) {
                    case "REWARDS":
                        InventoryUtils.Action action = new InventoryUtils.Action(InventoryUtils.ActionClick.ALL, item, () -> {
                            openRewardsMenu(player);
                        });

                        actions.add(action);
                        break;
                }
            }

            slots.add(slot);
            items.add(item);
        }

        InventoryCreator creator = new InventoryCreator(player, defaultInventory, items, slots, actions);
        creator.open(1);
    }

    private void openKeyMenu(Player player, PlayerKey key) {
        FileUtils.Files file = FileUtils.Files.KEY;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        Inventory defaultInventory = Bukkit.createInventory(null, size, title);

        List<Integer> slots = new ArrayList<>(54);
        List<ItemStack> items = new ArrayList<>(54);
        List<InventoryUtils.Action> actions = new ArrayList<>(54);

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            if (StringUtils.equals(str, "key")) {
                int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");
                ItemStack item = key.getKey().getDisplay(key.getAmount());

                slots.add(slot);
                items.add(item);
                continue;
            }

            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str, new String[]{
                    "{keys}"
            }, new String[]{
                    key.getAmount().toString()
            });

            String actionStr = FileUtils.get().getString(file, "Inventory.items." + str + ".action");

            if (!StringUtils.equals(actionStr, "NULL")) {
                InventoryUtils.Action action = null;
                switch (actionStr.toUpperCase()) {
                    case "OPEN_ONE":
                        action = new InventoryUtils.Action(InventoryUtils.ActionClick.ALL, item, () -> {
                            key.open(player, 1);

                            Sounds sounds = new Sounds("open");
                            sounds.play(player);

                            if (key.getAmount() <= 0) {
                                openMainMenu(player);
                                return;
                            }

                            openKeyMenu(player, key);
                        });
                        break;
                    case "OPEN_ALL":
                        action = new InventoryUtils.Action(InventoryUtils.ActionClick.ALL, item, () -> {
                            key.open(player, key.getAmount());

                            Sounds sounds = new Sounds("open");
                            sounds.play(player);

                            openMainMenu(player);
                        });
                        break;
                    case "COLLECT_ONE":
                        action = new InventoryUtils.Action(InventoryUtils.ActionClick.ALL, item, () -> {
                            key.collect(player, 1);

                            Sounds sounds = new Sounds("collect");
                            sounds.play(player);

                            if (key.getAmount() <= 0) {
                                openMainMenu(player);
                                return;
                            }

                            openKeyMenu(player, key);
                        });
                        break;
                    case "COLLECT_ALL":
                        action = new InventoryUtils.Action(InventoryUtils.ActionClick.ALL, item, () -> {
                            key.collect(player, key.getAmount());

                            Sounds sounds = new Sounds("collect");
                            sounds.play(player);

                            openMainMenu(player);
                        });
                        break;
                }

                if (action != null) actions.add(action);
            }

            slots.add(slot);
            items.add(item);
        }

        InventoryCreator creator = new InventoryCreator(player, defaultInventory, items, slots, actions);
        creator.open(1);
    }

    private void openRewardsMenu(Player player) {
        FileUtils.Files file = FileUtils.Files.REWARDS;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        Inventory defaultInventory = Bukkit.createInventory(null, size, title);

        List<Integer> slots = new ArrayList<>(54);
        List<ItemStack> items = new ArrayList<>(512);
        List<InventoryUtils.Action> actions = new ArrayList<>(512);

        List<Reward> rewards = DataManager.getInstance().load(player).getRewards();

        String[] slotsSplit = FileUtils.get().getString(file, "Inventory.reward-slots").replace(" ", "").split(",");

        if (rewards.size() <= 0) {
            int slot = FileUtils.get().getInt(file, "Nothing.slot");
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Nothing");

            slots.add(slot);
            items.add(item);
        } else {
            int i = -1;

            for (Reward reward : rewards) {
                if (i >= slotsSplit.length - 1) i = -1;

                int slot = Integer.parseInt(slotsSplit[++i]);
                ItemStack item = reward.getDisplay();
                InventoryUtils.Action action = new InventoryUtils.Action(InventoryUtils.ActionClick.ALL, item, () -> {
                    reward.collect(player);
                    PlayerData data = DataManager.getInstance().load(player);
                    data.removeReward(reward);
                    player.sendMessage(StringUtils.replaceEach(Messages.REWARD_COLLECT, new String[]{
                            "{reward}"
                    }, new String[]{
                            reward.getDisplay().hasItemMeta() ? (reward.getDisplay().getItemMeta().hasDisplayName() ? reward.getDisplay().getItemMeta().getDisplayName() : reward.getName()) : reward.getName()
                    }));

                    openMainMenu(player);

                    Sounds sounds = new Sounds("collect");
                    sounds.play(player);
                });

                slots.add(slot);
                items.add(item);
                actions.add(action);
            }
        }

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str);
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            String actionStr = FileUtils.get().getString(file, "Inventory.items." + str + ".action");

            if (!StringUtils.equals(actionStr, "NULL")) {
                switch (actionStr.toUpperCase()) {
                    case "COLLECT_ALL":
                        InventoryUtils.getInstance().addAction(defaultInventory, item, () -> {
                            if (rewards.size() <= 0) {
                                player.sendMessage(Messages.WITHOUT_REWARDS);
                                return;
                            }

                            for (Reward reward : rewards) {
                                reward.collect(player);
                            }

                            rewards.clear();

                            player.sendMessage(Messages.REWARD_COLLECT_ALL);
                            openRewardsMenu(player);

                            Sounds sounds = new Sounds("collect");
                            sounds.play(player);
                        }, InventoryUtils.ActionClick.ALL);
                        break;
                }
            }

            defaultInventory.setItem(slot, item);
        }

        int previousPageSlot = FileUtils.get().getInt(file, "Previous-Page.slot");
        ItemStack previousPageItem = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Previous-Page");

        int nextPageSlot = FileUtils.get().getInt(file, "Next-Page.slot");
        ItemStack nextPageItem = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Next-Page");

        InventoryCreator creator = new InventoryCreator(player, defaultInventory, items, slots, actions, nextPageSlot, previousPageSlot, nextPageItem, previousPageItem, slotsSplit.length);
        creator.open(1);
    }

    public static class InventoryCreator {

        private Player player;
        private String title;
        private Integer size;
        private Integer nextPageSlot;
        private Integer previousPageSlot;
        private ItemStack nextPageItem;
        private ItemStack previousPageItem;
        private Inventory defaultInventory;
        private HashMap<Integer, Inventory> inventories;

        /**
         * Constructor without pages
         */
        public InventoryCreator(Player player, Inventory defaultInventory, List<ItemStack> items, List<Integer> slots, List<InventoryUtils.Action> actions) {
            this(player, defaultInventory, items, slots, actions, null, null, null, null, 999);
        }

        /**
         * Constructor with pages
         */
        public InventoryCreator(Player player, Inventory defaultInventory, List<ItemStack> items, List<Integer> slots, List<InventoryUtils.Action> actions, Integer nextPageSlot, Integer previousPageSlot, ItemStack nextPageItem, ItemStack previousPageItem, Integer itemsPerPage) {
            this.player = player;
            this.defaultInventory = defaultInventory;
            this.title = defaultInventory.getTitle();
            this.size = defaultInventory.getSize();
            this.nextPageSlot = nextPageSlot;
            this.previousPageSlot = previousPageSlot;
            this.nextPageItem = nextPageItem;
            this.previousPageItem = previousPageItem;
            this.inventories = new HashMap<>(32);
            this.create(items, slots, actions, 1, itemsPerPage);
        }

        /**
         * Returns the player
         */
        public Player getPlayer() {
            return player;
        }

        /**
         * Returns the default inventory
         */
        public Inventory getDefaultInventory() {
            return defaultInventory;
        }

        /**
         * Returns the title of inventory
         */
        private String getTitle() {
            return title;
        }

        /**
         * Returns the size of inventory
         */
        private Integer getSize() {
            return size;
        }

        /**
         * Returns the previous page slot
         */
        public Integer getPreviousPageSlot() {
            return previousPageSlot;
        }

        /**
         * Returns the next page slot
         */
        public Integer getNextPageSlot() {
            return nextPageSlot;
        }

        /**
         * Returns the previous page item
         */
        public ItemStack getPreviousPageItem() {
            return previousPageItem;
        }

        /**
         * Returns the next page item
         */
        public ItemStack getNextPageItem() {
            return nextPageItem;
        }

        /**
         * Map with all inventory pages
         *
         * Key = Page number
         * Value = Inventory
         */
        public HashMap<Integer, Inventory> getInventories() {
            return inventories;
        }

        /**
         * Function to open a inventory page
         *
         * @param page Inventory page
         */
        public void open(Integer page) {
            player.openInventory(getInventories().get(page));
        }

        /**
         * Function to create all inventories and pages
         *
         * @param items List of ItemStacks (if items size > slots size a new page will be created)
         * @param slots Array of slots
         * @param actions List of Actions
         * @param page Current page
         */
        private void create(List<ItemStack> items, List<Integer> slots, List<InventoryUtils.Action> actions, Integer page, Integer itemsPerPage) {
            Inventory inventory = Bukkit.createInventory(null, getSize(), getTitle());

            // clone all items
            for (int slot = 0; slot < getDefaultInventory().getSize(); ++slot) {
                ItemStack item = getDefaultInventory().getItem(slot);
                if (item == null || item.getType().equals(Material.AIR)) continue;

                inventory.setItem(slot, item);
            }

            if (InventoryUtils.getInstance().getInventoryActions().containsKey(getDefaultInventory())) {
                // clone all actions
                for (InventoryUtils.Action action : InventoryUtils.getInstance().getInventoryActions().get(getDefaultInventory())) {
                    if (action == null) continue;

                    InventoryUtils.getInstance().addAction(inventory, action.getItem(), action.getAction(), action.getClick());
                }
            }

            List<ItemStack> remaining = new ArrayList<>(items);

            for (int i = 0; i < items.size(); ++i) {
                if (i >= itemsPerPage) {
                    Validate.notNull(getNextPageItem(), "Next page item cannot be null!");
                    Validate.notNull(getNextPageSlot(), "Next page slot cannot be null!");

                    ItemStack item = getNextPageItem();
                    Integer slot = getNextPageSlot();

                    InventoryUtils.getInstance().addAction(inventory, item, () -> {
                        open(page + 1);
                    }, InventoryUtils.ActionClick.ALL);

                    inventory.setItem(slot, item);

                    getInventories().put(page, inventory);
                    create(remaining, slots, actions, page + 1, itemsPerPage);
                    break;
                }

                ItemStack item = items.get(i);
                Integer slot = slots.get(i);

                for (InventoryUtils.Action action : actions) {
                    if (!item.isSimilar(action.getItem())) continue;

                    InventoryUtils.getInstance().addAction(inventory, item, action.getAction(), action.getClick());
                }

                inventory.setItem(slot, item);
                remaining.remove(item);
            }

            if (page > 1) {
                Validate.notNull(getPreviousPageItem(), "Previous page item cannot be null!");
                Validate.notNull(getPreviousPageSlot(), "Previous page slot cannot be null!");

                ItemStack item = getPreviousPageItem();
                Integer slot = getPreviousPageSlot();

                InventoryUtils.getInstance().addAction(inventory, item, () -> {
                    open(page - 1);
                }, InventoryUtils.ActionClick.ALL);

                inventory.setItem(slot, item);
            }

            getInventories().put(page, inventory);
        }
    }
}