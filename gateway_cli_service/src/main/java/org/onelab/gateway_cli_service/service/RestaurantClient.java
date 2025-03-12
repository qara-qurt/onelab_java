package org.onelab.gateway_cli_service.service;

import org.onelab.gateway_cli_service.dto.MenuDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "restaurant-service")
public interface RestaurantClient {

    @GetMapping("/api/menus")
    List<MenuDto> getMenus(@RequestParam int page, @RequestParam int size);
}

