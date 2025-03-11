package org.onelab.gateway_cli_service.service;

import java.util.List;

public interface MenuService {
    String getMenu(String id);
    String createMenu(String name, List<String>  dishIDs);
    String removeMenu(String id);
    String getMenus(int page, int size);
    String addDishToMenu(String menuID, List<String> dishID);
    String removeDishFromMenu(String menuID, List<String> dishIDs);
}
