package it.gromov.zpayments.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public final class MessagesConfig extends OkaeriConfig {

    private String prefix = "&8[&bzPayments&8]&7 ";
    private String noPermission = "{prefix}&cУ вас нет прав на выполнение этой команды.";
    private String playerOnly = "{prefix}&cЭту команду можно использовать только находясь в игре.";
    private String unknownSubCommand = "{prefix}&cНеизвестная подкоманда. Используйте &f/zpayments help&c.";
    private String reloadSuccess = "{prefix}&aКонфигурация успешно перезагружена.";
    private String reloadFailed = "{prefix}&cНе удалось перезагрузить конфигурацию: &f{error}";
    private String statusLine = "{prefix}&7ID магазина: &f{shop-id} &7| Сервер: &f{server-id} &7| Последний опрос: &f{last-poll-ago} сек. назад &7({last-poll-status})";
    private String testConnectionRunning = "{prefix}&7Проверяем соединение с zDonate...";
    private String testConnectionSuccess = "{prefix}&aСоединение с zDonate установлено успешно.";
    private String testConnectionFailed = "{prefix}&cНе удалось подключиться к zDonate: &f{error}";
    private String cartEmpty = "{prefix}&7Ваша корзина пуста.";
    private String cartOpened = "{prefix}&7Открываем корзину покупок...";
    private String cartOnCooldown = "{prefix}&cПодождите ещё &f{seconds} сек.&c перед повторным открытием корзины.";
    private String cartItemClaimed = "{prefix}&aВыдано: &f{title} &7x{count}";
    private String cartAllClaimed = "{prefix}&aВыдано покупок: &f{count}";
    private String cartClaimError = "{prefix}&cНе удалось выдать один из товаров, попробуйте позже.";
    private String joinCartClaimed = "{prefix}&aПока вас не было, вам начислили покупок: &f{count}&a. Проверьте /cart.";
    private String apiErrorGeneric = "{prefix}&cОшибка обращения к zDonate: &f{error}";
    private String helpHeader = "&8&m--------&r &bzPayments&r &8&m--------";
    private String helpEntryFormat = "&b/zpayments {name}";
    private String setupUsage = "{prefix}&cИспользование: &f/zpayments setup <shop-id> <server-id> <plugin-key>";
    private String setupSuccess = "{prefix}&aНастройки сохранены. shop-id: &f{shop-id}&a, server-id: &f{server-id}";
    private String setupFailed = "{prefix}&cНе удалось сохранить настройки: &f{error}";
}
