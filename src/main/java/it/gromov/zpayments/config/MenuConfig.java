package it.gromov.zpayments.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public final class MenuConfig extends OkaeriConfig {

    private String title = "&8Корзина покупок";
    private int rows = 3;

    private boolean fillerEnabled = true;
    private String fillerMaterial = "GRAY_STAINED_GLASS_PANE";
    private String fillerName = " ";

    private String itemMaterial = "CHEST";
    private String itemNameFormat = "&a{title}";
    private List<String> itemLoreFormat = new ArrayList<>(java.util.Arrays.asList(
            "&7Количество: &f{count}",
            "&7Заказ: &f{order-id}",
            "",
            "&eНажмите, чтобы получить"
    ));

    private String emptyItemMaterial = "BARRIER";
    private String emptyItemName = "&cКорзина пуста";

    private boolean claimAllEnabled = true;
    private int claimAllSlot = 22;
    private String claimAllMaterial = "EMERALD_BLOCK";
    private String claimAllName = "&aЗабрать все покупки";

    public int getSafeRows() {
        return Math.min(6, Math.max(1, rows));
    }

    public int getSafeSize() {
        return getSafeRows() * 9;
    }
}
