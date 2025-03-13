package org.onelab.gateway_cli_service.service;

import java.util.List;

public interface MenuService {
    String getMenu(Long id);
    String createMenu(String name, List<Long>  dishIDs);
    String removeMenu(Long id);
    String getMenus(int page, int size);
    String addDishToMenu(Long menuID, List<Long> dishID);
    String removeDishFromMenu(Long menuID, List<Long> dishIDs);
}
