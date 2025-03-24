package org.onelab.restaurant_service.service;

import org.onelab.common_lib.dto.MenuDto;
import org.onelab.common_lib.dto.MenuRequestDto;

import java.util.List;

public interface MenuService {
    String createMenu(MenuRequestDto menu);
    void removeMenu(Long id);
    void addDishesToMenu(Long menuId, List<Long> dishIds);
    void removeDishesFromMenu(Long menuId, List<Long> dishIds);
    MenuDto getMenu(Long id);
    List<MenuDto> getMenus(int page, int size);
}
