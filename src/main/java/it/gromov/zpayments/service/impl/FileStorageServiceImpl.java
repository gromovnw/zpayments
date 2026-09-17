package it.gromov.zpayments.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.gromov.zpayments.service.ConfigService;
import it.gromov.zpayments.service.StorageService;
import it.gromov.zpayments.storage.entity.CartEntry;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class FileStorageServiceImpl implements StorageService {

    private static final Type LIST_TYPE = new TypeToken<ArrayList<CartEntry>>() {
    }.getType();

    private final Plugin plugin;
    private final ConfigService configService;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final List<CartEntry> entries = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger idSequence = new AtomicInteger(1);
    private File file;
    private boolean ready;

    public FileStorageServiceImpl(Plugin plugin, ConfigService configService) {
        this.plugin = plugin;
        this.configService = configService;
    }

    @Override
    public void enable() {
        file = new File(plugin.getDataFolder(), configService.getConfig().getStorage().getFileName());
        entries.clear();
        idSequence.set(1);

        if (file.exists()) {
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                List<CartEntry> loaded = gson.fromJson(reader, LIST_TYPE);
                if (loaded != null) {
                    entries.addAll(loaded);
                    for (CartEntry entry : loaded) {
                        if (entry.getId() >= idSequence.get()) {
                            idSequence.set(entry.getId() + 1);
                        }
                    }
                }
            } catch (Exception exception) {
                plugin.getLogger().log(Level.SEVERE, "Не удалось прочитать файл корзины " + file.getName(), exception);
            }
        }
        ready = true;
        plugin.getLogger().log(Level.INFO, "Хранилище корзины: файл " + file.getName() + " (" + entries.size() + " записей)");
    }

    @Override
    public void reload() {
        disable();
        enable();
    }

    @Override
    public void disable() {
        ready = false;
    }

    @Override
    public boolean isReady() {
        return ready;
    }

    @Override
    public void addEntry(CartEntry entry) {
        if (!ready) {
            return;
        }
        entry.setId(idSequence.getAndIncrement());
        entries.add(entry);
        persist();
    }

    @Override
    public boolean hasEntry(String orderId, String taskKey) {
        synchronized (entries) {
            return entries.stream().anyMatch(e -> e.getOrderId().equals(orderId) && e.getTaskKey().equals(taskKey));
        }
    }

    @Override
    public List<CartEntry> findByNickname(String nickname) {
        synchronized (entries) {
            return entries.stream().filter(e -> e.getNickname().equals(nickname)).collect(Collectors.toList());
        }
    }

    @Override
    public void removeEntry(CartEntry entry) {
        if (!ready) {
            return;
        }
        entries.removeIf(e -> e.getId() == entry.getId());
        persist();
    }

    @Override
    public void removeEntries(List<CartEntry> toRemove) {
        if (!ready || toRemove == null || toRemove.isEmpty()) {
            return;
        }
        List<Integer> ids = toRemove.stream().map(CartEntry::getId).collect(Collectors.toList());
        entries.removeIf(e -> ids.contains(e.getId()));
        persist();
    }

    private synchronized void persist() {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            synchronized (entries) {
                gson.toJson(entries, writer);
            }
        } catch (Exception exception) {
            plugin.getLogger().log(Level.SEVERE, "Не удалось сохранить файл корзины " + file.getName(), exception);
        }
    }
}
