package it.gromov.zpayments;

import it.gromov.zpayments.api.ZPaymentsAPI;
import it.gromov.zpayments.http.ShopApiClient;
import it.gromov.zpayments.listener.MenuClickListener;
import it.gromov.zpayments.listener.PlayerJoinListener;
import it.gromov.zpayments.service.CartService;
import it.gromov.zpayments.service.CommandService;
import it.gromov.zpayments.service.ConfigService;
import it.gromov.zpayments.service.MenuService;
import it.gromov.zpayments.service.MessageService;
import it.gromov.zpayments.service.ShopApiService;
import it.gromov.zpayments.service.StorageService;
import it.gromov.zpayments.config.section.StorageType;
import it.gromov.zpayments.service.impl.CartServiceImpl;
import it.gromov.zpayments.service.impl.CommandServiceImpl;
import it.gromov.zpayments.service.impl.ConfigServiceImpl;
import it.gromov.zpayments.service.impl.FileStorageServiceImpl;
import it.gromov.zpayments.service.impl.MenuServiceImpl;
import it.gromov.zpayments.service.impl.MessageServiceImpl;
import it.gromov.zpayments.service.impl.MySqlStorageServiceImpl;
import it.gromov.zpayments.service.impl.ShopApiServiceImpl;
import org.bukkit.plugin.java.JavaPlugin;

public final class ZPaymentsPlugin extends JavaPlugin {

    private ConfigService configService;
    private MessageService messageService;
    private StorageService storageService;
    private CartService cartService;
    private MenuService menuService;
    private ShopApiService shopApiService;
    private CommandService commandService;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            getLogger().severe("Не удалось создать папку плагина, отключение.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        configService = new ConfigServiceImpl(this);
        configService.enable();

        messageService = new MessageServiceImpl(configService);
        messageService.enable();

        storageService = configService.getConfig().getStorage().getType() == StorageType.MYSQL
                ? new MySqlStorageServiceImpl(this, configService)
                : new FileStorageServiceImpl(this, configService);
        storageService.enable();

        ShopApiClient shopApiClient = new ShopApiClient(configService);

        cartService = new CartServiceImpl(this, configService, storageService, messageService, shopApiClient);
        cartService.enable();

        menuService = new MenuServiceImpl(configService, cartService);
        menuService.enable();

        shopApiService = new ShopApiServiceImpl(this, configService, cartService, shopApiClient);
        shopApiService.enable();

        commandService = new CommandServiceImpl(this, configService, messageService, cartService, menuService, shopApiService, this::reload);
        commandService.enable();

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this, cartService), this);
        getServer().getPluginManager().registerEvents(new MenuClickListener(configService, messageService, cartService), this);

        ZPaymentsAPI.init(cartService);
    }

    public void reload() {
        configService.reload();
        messageService.reload();
        storageService.reload();
        cartService.reload();
        menuService.reload();
        shopApiService.reload();
        commandService.reload();
    }

    @Override
    public void onDisable() {
        ZPaymentsAPI.shutdown();

        if (commandService != null) commandService.disable();
        if (shopApiService != null) shopApiService.disable();
        if (menuService != null) menuService.disable();
        if (cartService != null) cartService.disable();
        if (storageService != null) storageService.disable();
        if (messageService != null) messageService.disable();
        if (configService != null) configService.disable();
    }
}
