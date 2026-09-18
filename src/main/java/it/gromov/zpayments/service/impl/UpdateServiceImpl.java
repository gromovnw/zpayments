package it.gromov.zpayments.service.impl;

import it.gromov.zpayments.service.ConfigService;
import it.gromov.zpayments.service.UpdateService;
import it.gromov.zpayments.update.GithubUpdateClient;
import it.gromov.zpayments.update.dto.GithubReleaseDto;
import it.gromov.zpayments.util.SchedulerUtil;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.util.logging.Level;

public final class UpdateServiceImpl implements UpdateService {

    private final Plugin plugin;
    private final ConfigService configService;
    private final File pluginJarFile;
    private final GithubUpdateClient client = new GithubUpdateClient();

    public UpdateServiceImpl(Plugin plugin, ConfigService configService, File pluginJarFile) {
        this.plugin = plugin;
        this.configService = configService;
        this.pluginJarFile = pluginJarFile;
    }

    @Override
    public void enable() {
        if (!configService.getConfig().getUpdate().isEnabled()) {
            return;
        }
        SchedulerUtil.runAsync(plugin, this::checkAndDownload);
    }

    @Override
    public void reload() {
    }

    @Override
    public void disable() {
    }

    private void checkAndDownload() {
        try {
            GithubReleaseDto release = client.fetchLatestRelease();
            String remoteVersion = stripLeadingV(release.getTagName());
            String currentVersion = plugin.getDescription().getVersion();

            if (compareVersions(remoteVersion, currentVersion) <= 0) {
                return;
            }

            String downloadUrl = client.findJarDownloadUrl(release);
            byte[] jarBytes = client.downloadJar(downloadUrl);

            File updateDir = new File(pluginJarFile.getParentFile(), "update");
            if (!updateDir.exists() && !updateDir.mkdirs()) {
                plugin.getLogger().warning("Не удалось создать папку " + updateDir.getPath() + " для автообновления.");
                return;
            }
            File target = new File(updateDir, pluginJarFile.getName());
            try (FileOutputStream out = new FileOutputStream(target)) {
                out.write(jarBytes);
            }

            plugin.getLogger().info("Доступна новая версия zPayments " + remoteVersion + " (текущая: " + currentVersion
                    + "). Скачана и будет применена при следующем перезапуске сервера.");
        } catch (Exception exception) {
            plugin.getLogger().log(Level.WARNING, "Не удалось проверить обновления zPayments: " + exception.getMessage(), exception);
        }
    }

    private String stripLeadingV(String version) {
        return version.startsWith("v") || version.startsWith("V") ? version.substring(1) : version;
    }

    private int compareVersions(String a, String b) {
        String[] partsA = a.split("\\.");
        String[] partsB = b.split("\\.");
        int length = Math.max(partsA.length, partsB.length);
        for (int i = 0; i < length; i++) {
            int valueA = i < partsA.length ? parsePart(partsA[i]) : 0;
            int valueB = i < partsB.length ? parsePart(partsB[i]) : 0;
            if (valueA != valueB) {
                return Integer.compare(valueA, valueB);
            }
        }
        return 0;
    }

    private int parsePart(String part) {
        try {
            return Integer.parseInt(part.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException exception) {
            return 0;
        }
    }
}
