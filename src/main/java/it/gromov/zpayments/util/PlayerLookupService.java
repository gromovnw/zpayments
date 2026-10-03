package it.gromov.zpayments.util;

import it.gromov.zpayments.http.ShopApiClient;
import it.gromov.zpayments.http.dto.LookupDto;
import it.gromov.zpayments.http.dto.PendingLookupsDto;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public final class PlayerLookupService {

    private static final long PERIOD_TICKS = 40L;

    private final Plugin plugin;
    private final ShopApiClient client;
    private final LuckPermsGroups luckPerms;
    private BukkitTask task;

    public PlayerLookupService(Plugin plugin, ShopApiClient client) {
        this.plugin = plugin;
        this.client = client;
        this.luckPerms = new LuckPermsGroups(plugin);
    }

    public void start() {
        task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::pollOnce, PERIOD_TICKS, PERIOD_TICKS);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void pollOnce() {
        try {
            PendingLookupsDto response = client.fetchPendingLookups();
            if (response == null) {
                return;
            }
            for (LookupDto lookup : response.getLookups()) {
                answer(lookup);
            }
        } catch (Exception exception) {
            plugin.getLogger().log(Level.FINE, "Не удалось получить запросы групп игроков", exception);
        }
    }

    private void answer(LookupDto lookup) {
        try {
            UUID uuid = resolveUuid(lookup.getNickname());
            if (uuid == null) {
                reply(lookup.getId(), Collections.<String>emptyList(), "PLAYER_UNKNOWN");
                return;
            }
            if (!luckPerms.isAvailable()) {
                reply(lookup.getId(), Collections.<String>emptyList(), "LUCKPERMS_MISSING");
                return;
            }
            reply(lookup.getId(), luckPerms.groupsOf(uuid), null);
        } catch (Exception exception) {
            plugin.getLogger().log(Level.FINE, "Не удалось узнать группы игрока " + lookup.getNickname(), exception);
            reply(lookup.getId(), Collections.<String>emptyList(), "LOOKUP_FAILED");
        }
    }

    private void reply(String lookupId, List<String> groups, String error) {
        try {
            client.answerLookup(lookupId, groups, error);
        } catch (Exception exception) {
            plugin.getLogger().log(Level.FINE, "Не удалось отправить ответ на запрос групп", exception);
        }
    }

    private UUID resolveUuid(String nickname) throws Exception {
        Player online = Bukkit.getPlayerExact(nickname);
        if (online != null) {
            return online.getUniqueId();
        }
        UUID known = luckPerms.uuidOf(nickname);
        if (known != null) {
            return known;
        }
        OfflinePlayer offline = Bukkit.getOfflinePlayer(nickname);
        return offline.hasPlayedBefore() ? offline.getUniqueId() : null;
    }
}
