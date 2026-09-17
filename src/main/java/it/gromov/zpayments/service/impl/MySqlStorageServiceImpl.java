package it.gromov.zpayments.service.impl;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.db.MariaDbDatabaseType;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;
import it.gromov.zpayments.config.section.MySqlSection;
import it.gromov.zpayments.service.ConfigService;
import it.gromov.zpayments.service.StorageService;
import it.gromov.zpayments.storage.entity.CartEntry;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public final class MySqlStorageServiceImpl implements StorageService {

    private final Plugin plugin;
    private final ConfigService configService;

    private ConnectionSource connectionSource;
    private Dao<CartEntry, Integer> cartDao;
    private boolean ready;

    public MySqlStorageServiceImpl(Plugin plugin, ConfigService configService) {
        this.plugin = plugin;
        this.configService = configService;
    }

    @Override
    public void enable() {
        ready = false;
        MySqlSection section = configService.getConfig().getStorage().getMysql();

        try {
            String url = "jdbc:mariadb://" + section.getHost() + ":" + section.getPort() + "/" + section.getDatabase()
                    + "?useSSL=" + section.isUseSsl()
                    + "&autoReconnect=true"
                    + "&characterEncoding=utf8"
                    + "&useUnicode=true";
            connectionSource = new JdbcConnectionSource(url, section.getUsername(), section.getPassword(), new MariaDbDatabaseType());

            DatabaseTableConfig<CartEntry> tableConfig = DatabaseTableConfig.fromClass(connectionSource.getDatabaseType(), CartEntry.class);
            tableConfig.setTableName(section.getTablePrefix() + tableConfig.getTableName());
            cartDao = DaoManager.createDao(connectionSource, tableConfig);
            TableUtils.createTableIfNotExists(connectionSource, tableConfig);
            // JdbcConnectionSource сам по себе соединение не проверяет (оно
            // ленивое, до первого реального запроса) — без этого countOf()
            // ошибка URL/пароля/хоста осталась бы незамеченной до первой
            // покупки. Так падаем сразу при старте, с понятным логом.
            cartDao.countOf();
            ready = true;
            plugin.getLogger().log(Level.INFO, "Подключение к MySQL установлено: " + section.getHost() + ":" + section.getPort() + "/" + section.getDatabase());
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.SEVERE, "Не удалось подключиться к MySQL для хранения корзины", exception);
        }
    }

    @Override
    public void reload() {
        disable();
        enable();
    }

    @Override
    public void disable() {
        if (connectionSource != null) {
            try {
                connectionSource.close();
            } catch (Exception exception) {
                plugin.getLogger().log(Level.WARNING, "Ошибка при закрытии соединения с MySQL", exception);
            }
        }
        connectionSource = null;
        cartDao = null;
        ready = false;
    }

    @Override
    public boolean isReady() {
        return ready && cartDao != null;
    }

    @Override
    public void addEntry(CartEntry entry) {
        if (!isReady()) {
            return;
        }
        try {
            cartDao.create(entry);
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.WARNING, "Не удалось сохранить покупку в корзину", exception);
        }
    }

    @Override
    public boolean hasEntry(String orderId, String taskKey) {
        if (!isReady()) {
            return false;
        }
        try {
            return cartDao.queryBuilder()
                    .where()
                    .eq("orderId", orderId)
                    .and()
                    .eq("taskKey", taskKey)
                    .countOf() > 0;
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.WARNING, "Не удалось проверить наличие записи в корзине", exception);
            return false;
        }
    }

    @Override
    public List<CartEntry> findByNickname(String nickname) {
        if (!isReady()) {
            return Collections.emptyList();
        }
        try {
            return cartDao.queryForEq("nickname", nickname);
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.WARNING, "Не удалось загрузить корзину игрока " + nickname, exception);
            return Collections.emptyList();
        }
    }

    @Override
    public void removeEntry(CartEntry entry) {
        if (!isReady()) {
            return;
        }
        try {
            cartDao.delete(entry);
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.WARNING, "Не удалось удалить запись из корзины", exception);
        }
    }

    @Override
    public void removeEntries(List<CartEntry> entries) {
        if (!isReady() || entries == null || entries.isEmpty()) {
            return;
        }
        try {
            cartDao.delete(entries);
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.WARNING, "Не удалось удалить записи из корзины", exception);
        }
    }
}
