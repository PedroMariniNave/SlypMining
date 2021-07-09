package com.zpedroo.slypmining.data;

import com.zpedroo.slypmining.objects.Key;
import com.zpedroo.slypmining.objects.PlayerKey;
import com.zpedroo.slypmining.objects.Reward;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.UUID;

public class PlayerData {

    private UUID uuid;
    private List<PlayerKey> keys;
    private List<Reward> rewards;

    public PlayerData(UUID uuid, List<PlayerKey> keys, List<Reward> rewards) {
        this.uuid = uuid;
        this.keys = keys;
        this.rewards = rewards;
    }

    public UUID getUUID() {
        return uuid;
    }

    public List<PlayerKey> getKeys() {
        return keys;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public void giveReward(Reward reward) {
        getRewards().add(reward);
    }

    public void removeReward(Reward reward) {
        getRewards().remove(reward);
    }

    public void giveKey(Key key, Integer amount) {
        for (PlayerKey keys : getKeys()) {
            if (!StringUtils.equals(keys.getKey().getName(), key.getName())) continue;

            keys.addAmount(amount);
            return;
        }

        getKeys().add(new PlayerKey(key, amount));
    }

    public void removeKey(Key key, Integer amount) {
        for (PlayerKey keys : getKeys()) {
            if (!StringUtils.equals(keys.getKey().getName(), key.getName())) continue;

            keys.removeAmount(amount);

            if (keys.getAmount() <= 0) {
                getKeys().remove(keys);
            }
            break;
        }
    }
}