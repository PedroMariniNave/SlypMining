package com.zpedroo.slypmining.mysql;

import com.zpedroo.slypmining.data.PlayerData;
import com.zpedroo.slypmining.objects.PlayerKey;
import com.zpedroo.slypmining.managers.DataManager;
import com.zpedroo.slypmining.objects.Key;
import com.zpedroo.slypmining.objects.Reward;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBManager {

    private DataManager dataManager;

    public DBManager() {
        this.dataManager = new DataManager();
    }

    public void saveData(PlayerData data) {
        if (contains(data.getUUID().toString(), "uuid")) {
            String query = "UPDATE `" + DBConnection.TABLE + "` SET" +
                    "`uuid`='" + data.getUUID().toString() + "', " +
                    "`keys`='" + serializeKeys(data.getKeys()) + "', " +
                    "`rewards`='" + serializeRewards(data.getRewards()) + "' " +
                    "WHERE `uuid`='" + data.getUUID().toString() + "';";
            executeUpdate(query);
            return;
        }

        String query = "INSERT INTO `" + DBConnection.TABLE + "` (`uuid`, `keys`, `rewards`) VALUES " +
                "('" + data.getUUID().toString() + "', " +
                "'" + serializeKeys(data.getKeys()) + "', " +
                "'" + serializeRewards(data.getRewards()) + "');";
        executeUpdate(query);
    }

    public PlayerData getData(Player player) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        String query = "SELECT * FROM `" + DBConnection.TABLE + "` WHERE `uuid` = '" + player.getUniqueId() + "';";

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            result = preparedStatement.executeQuery();

            if (result.next()) {
                List<PlayerKey> keys = deserializeKeys(result.getString(2));
                List<Reward> rewards = deserializeRewards(result.getString(3));

                return new PlayerData(player.getUniqueId(), keys, rewards);
            }

            return new PlayerData(player.getUniqueId(), new ArrayList<>(16), new ArrayList<>(64));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeConnection(connection, result, preparedStatement, null);
        }

        return new PlayerData(player.getUniqueId(), new ArrayList<>(16), new ArrayList<>(64));
    }

    private Boolean contains(String value, String column) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        String query = "SELECT `" + column + "` FROM `" + DBConnection.TABLE + "` WHERE `" + column + "`='" + value + "';";
        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            result = preparedStatement.executeQuery();
            return result.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeConnection(connection, result, preparedStatement, null);
        }

        return false;
    }

    private void executeUpdate(String query) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeConnection(connection, null, null, statement);
        }
    }

    private void closeConnection(Connection connection, ResultSet resultSet, PreparedStatement preparedStatement, Statement statement) {
        try {
            if (connection != null) connection.close();
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (statement != null) statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    protected void setupTable() {
        String query = "CREATE TABLE IF NOT EXISTS `" + DBConnection.TABLE + "` (`uuid` VARCHAR(255) NOT NULL, `keys` LONGTEXT NOT NULL, `rewards` LONGTEXT NOT NULL, PRIMARY KEY(`uuid`));";
        executeUpdate(query);
    }

    private Connection getConnection() throws SQLException {
        return DBConnection.getInstance().getConnection();
    }

    /*
     * Serialize player keys
     *
     * Serialized: key#amount,key#amount
     */
    public String serializeKeys(List<PlayerKey> keys) {
        StringBuilder ret = new StringBuilder(32);

        for (PlayerKey key : keys) {
            if (key == null) continue;

            ret.append(key.getKey().getName()).append("#").append(key.getAmount().toString()).append(",");
        }

        return ret.toString();
    }

    /*
     * Deserialize player keys
     *
     */
    private List<PlayerKey> deserializeKeys(String keys) {
        List<PlayerKey> ret = new ArrayList<>(16);
        String[] split = keys.split(",");

        for (String str : split) {
            String[] strSplit = str.split("#");

            Key key = DataManager.getInstance().getCache().getKey(strSplit[0]);
            if (key == null) continue;

            Integer amount = Integer.parseInt(strSplit[1]);

            ret.add(new PlayerKey(key, amount));
        }

        return ret;
    }

    /*
     * Serialize player rewards
     *
     * Serialized: key#reward,key#reward
     */
    public String serializeRewards(List<Reward> rewards) {
        StringBuilder ret = new StringBuilder(32);

        for (Reward reward : rewards) {
            if (reward == null) continue;

            Key key = reward.getKey();
            if (key == null) continue;

            ret.append(key.getName()).append("#").append(reward.getName()).append(",");
        }

        return ret.toString();
    }

    /*
     * Deserialize player rewards
     *
     */
    private List<Reward> deserializeRewards(String rewards) {
        List<Reward> ret = new ArrayList<>(64);
        String[] split = rewards.split(",");

        for (String str : split) {
            String[] strSplit = str.split("#");

            Key key = DataManager.getInstance().getCache().getKey(strSplit[0]);
            if (key == null) continue;

            Reward reward = key.getReward(strSplit[1]);
            if (reward == null) continue;

            ret.add(reward);
        }

        return ret;
    }

    private DataManager getDataManager() {
        return dataManager;
    }
}