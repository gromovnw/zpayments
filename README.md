# zPayments

Spigot/Bukkit-плагин доставки покупок с витрины [zDonate](https://zdonate.me): опрашивает API магазина и выполняет команды выдачи — без RCON.

## Установка

```
mvn clean package
```

Джарник — `target/zPayments.jar`, все зависимости уже внутри. Кинуть в `plugins/`, перезапустить сервер.

```
/zpayments setup <shopId> <serverId> <pluginKey>
/zpayments testconnection
```

Реквизиты — в личном кабинете zDonate, магазин → сервер.

## Команды

`/zpayments setup|status|testconnection|reload`, `/cart` (алиасы `/shop`, `/donate`, `/buy`).

## Требования

Java 8, Spigot/Paper 1.8+.

## Разработка

Архитектура и договорённости — в [CLAUDE.md](CLAUDE.md).
