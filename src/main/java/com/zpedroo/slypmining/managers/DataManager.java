package com.zpedroo.slypmining.managers;

import com.zpedroo.slypmining.data.PlayerData;
import com.zpedroo.slypmining.mysql.DBConnection;
import com.zpedroo.slypmining.data.cache.DataCache;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class DataManager {

    private static DataManager instance;
    public static DataManager getInstance() { return instance; }

    private DataCache dataCache;

    public DataManager() {
        instance = this;
        this.dataCache = new DataCache();
    }

    public PlayerData load(Player player) {
        if (player == null) return null;

        return getCache().getData(player);
    }

    public void save(PlayerData data) {
        if (data == null) return;

        DBConnection.getInstance().getDBManager().saveData(data);
    }

    public void saveAll() {
        new HashSet<>(getCache().getData().values()).forEach(this::save);
    }

    public DataCache getCache() {
        return dataCache;
    }
}