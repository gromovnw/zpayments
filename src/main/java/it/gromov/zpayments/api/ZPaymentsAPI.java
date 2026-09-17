package it.gromov.zpayments.api;

import it.gromov.zpayments.service.CartService;
import org.jetbrains.annotations.Nullable;

public final class ZPaymentsAPI {

    private static volatile CartService cartService;

    private ZPaymentsAPI() {
    }

    public static void init(CartService cartService) {
        ZPaymentsAPI.cartService = cartService;
    }

    public static void shutdown() {
        cartService = null;
    }

    public static boolean isReady() {
        return cartService != null;
    }

    public static @Nullable CartService getCartService() {
        return cartService;
    }
}
