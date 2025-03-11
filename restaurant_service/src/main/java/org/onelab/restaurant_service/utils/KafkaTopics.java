package org.onelab.restaurant_service.utils;

public final class KafkaTopics {
    private KafkaTopics() {
        throw new UnsupportedOperationException("Utility class should not be instantiated.");
    }

    public static final String GROUP_ID = "restaurant-group";
    public static final String DISH_ADD = "dish.add";
    public static final String DISH_REMOVE = "dish.remove";
    public static final String MENU_ADD = "menu.add";
    public static final String MENU_REMOVE = "menu.remove";
    public static final String ADD_DISH_TO_MENU = "menu.add_dish";
    public static final String REMOVE_DISH_FROM_MENU = "menu.remove_dish";
    public static final String WITHDRAW_ORDER = "order.withdraw";
    public static final String CREATE_ORDER = "order.create";
}
