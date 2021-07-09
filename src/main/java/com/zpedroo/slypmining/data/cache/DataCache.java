package com.zpedroo.slypmining.data.cache;

import com.zpedroo.slypmining.SlypMining;
import com.zpedroo.slypmining.data.PlayerData;
import com.zpedroo.slypmining.mysql.DBConnection;
import com.zpedroo.slypmining.objects.Key;
import com.zpedroo.slypmining.objects.Reward;
import com.zpedroo.slypmining.utils.builder.ItemBuilder;
import de.tr7zw.nbtapi.NBTItem;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DataCache {

    private List<Key> keys;
    private HashMap<Player, PlayerData> data;

    public DataCache() {
        this.keys = new ArrayList<>(16);
        this.data = new HashMap<>(512);
        this.cache();
    }

    public List<Key> getKeys() {
        return keys;
    }

    public Key getKey(String key) {
        for (Key keys : getKeys()) {
            if (!StringUtils.equals(keys.getName(), key)) continue;

            return keys;
        }

        return null;
    }

    public HashMap<Player, PlayerData> getData() {
        return data;
    }

    public PlayerData getData(Player player) {
        if (!getData().containsKey(player)) {
            PlayerData data = DBConnection.getInstance().getDBManager().getData(player);
            getData().put(player, data);

            return data;
        }

        return getData().get(player);
    }

    private void cache() {
        File where = new File(SlypMining.get().getDataFolder(), "/keys");
        File[] files = where.listFiles((d, name) -> name.endsWith(".yml"));

        if (files == null) return;

        for (File file : files) {
            if (file == null) continue;

            YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(file);

            String name = file.getName().replace(".yml", "");
            ItemStack item = ItemBuilder.build(yamlConfig, "Key-Item");
            ItemStack display = ItemBuilder.build(yamlConfig, "Key-Display");

            List<Reward> rewards = new ArrayList<>(16);

            for (String reward : yamlConfig.getConfigurationSection("Key-Settings.rewards").getKeys(false)) {
                Double chance = yamlConfig.getDouble("Key-Settings.rewards." + reward + ".chance");
                ItemStack rewardDisplay = ItemBuilder.build(yamlConfig, "Key-Settings.rewards." + reward);
                List<String> commands = yamlConfig.getStringList("Key-Settings.rewards." + reward + ".commands");
                List<String> messages = yamlConfig.getStringList("Key-Settings.rewards." + reward + ".messages");

                rewards.add(new Reward(rewardDisplay, reward, chance, commands, messages));
            }

            NBTItem nbt = new NBTItem(item);

            nbt.setString("SlypMiningKey", name);

            final Key key = new Key(name, nbt.getItem(), display, rewards);
            new HashSet<>(rewards).forEach(reward -> reward.setKey(key));

            getKeys().add(key);
        }
    }
}