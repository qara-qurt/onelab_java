package org.onelab.restaurant_service.service;

import org.onelab.restaurant_service.entity.Menu;

import java.util.List;

public interface MenuService {
    String addMenu(Menu menu);
    void removeMenu(String name);
    void addDishesToMenu(String menuID, List<String> dishIDs);
    void removeDishesFromMenu(String menuID, List<String> dishIDs);
}
