package it.gromov.zpayments.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public final class LuckPermsGroups {

    private static final String GROUP_PREFIX = "group.";
    private static final long LOAD_TIMEOUT_SECONDS = 10;

    private final Plugin plugin;
    private Object api;
    private boolean v5;
    private boolean resolved;

    public LuckPermsGroups(Plugin plugin) {
        this.plugin = plugin;
    }

    public synchronized boolean isAvailable() {
        return resolveApi() != null;
    }

    public List<String> groupsOf(UUID uuid) throws Exception {
        Object currentApi = resolveApi();
        if (currentApi == null) {
            return Collections.emptyList();
        }

        Object user;
        if (v5) {
            Object userManager = invoke(currentApi, "getUserManager");
            Object future = invoke(userManager, "loadUser", uuid);
            user = ((Future<?>) future).get(LOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } else {
            user = invoke(currentApi, "getUser", uuid);
        }
        if (user == null) {
            return Collections.emptyList();
        }

        List<String> groups = new ArrayList<>();
        Collection<?> nodes = (Collection<?>) invoke(user, "getNodes");
        for (Object node : nodes) {
            String key = String.valueOf(firstNonNull(invokeOptional(node, "getKey"), invokeOptional(node, "getPermission")));
            Object value = invokeOptional(node, "getValue");
            if (!Boolean.TRUE.equals(value) || !key.startsWith(GROUP_PREFIX)) {
                continue;
            }
            if (Boolean.TRUE.equals(invokeOptional(node, "hasExpired"))) {
                continue;
            }
            groups.add(key.substring(GROUP_PREFIX.length()));
        }
        return groups;
    }

    public UUID uuidOf(String name) throws Exception {
        Object currentApi = resolveApi();
        if (currentApi == null || !v5) {
            return null;
        }
        Object userManager = invoke(currentApi, "getUserManager");
        Object future = invoke(userManager, "lookupUniqueId", name);
        Object found = ((Future<?>) future).get(LOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        if (!(found instanceof Optional) || !((Optional<?>) found).isPresent()) {
            return null;
        }
        return (UUID) ((Optional<?>) found).get();
    }

    private synchronized Object resolveApi() {
        if (resolved) {
            return api;
        }
        resolved = true;
        api = lookup("net.luckperms.api.LuckPerms", true);
        if (api == null) {
            api = lookup("me.lucko.luckperms.api.LuckPermsApi", false);
        }
        if (api == null) {
            plugin.getLogger().info("LuckPerms не найден — группы игроков для доплаты не отправляются.");
        }
        return api;
    }

    private Object lookup(String className, boolean isV5) {
        try {
            Class<?> type = Class.forName(className);
            Object registration = Bukkit.getServicesManager().getRegistration(type);
            if (registration == null) {
                return null;
            }
            Object provider = invoke(registration, "getProvider");
            v5 = isV5;
            return provider;
        } catch (ClassNotFoundException notInstalled) {
            return null;
        } catch (Exception exception) {
            plugin.getLogger().warning("Не удалось подключиться к LuckPerms: " + exception.getMessage());
            return null;
        }
    }

    private static Object invoke(Object target, String name, Object... args) throws Exception {
        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i] instanceof UUID ? UUID.class : args[i].getClass();
        }
        Method method = findMethod(target.getClass(), name, types);
        method.setAccessible(true);
        return method.invoke(target, args);
    }

    private static Object invokeOptional(Object target, String name) {
        try {
            return invoke(target, name);
        } catch (Exception missing) {
            return null;
        }
    }

    private static Method findMethod(Class<?> type, String name, Class<?>[] types) throws NoSuchMethodException {
        return type.getMethod(name, types);
    }

    private static Object firstNonNull(Object first, Object second) {
        return first != null ? first : second;
    }
}
