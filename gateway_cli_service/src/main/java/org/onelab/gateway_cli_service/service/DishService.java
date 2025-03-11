package org.onelab.gateway_cli_service.service;

public interface DishService {
    String getDishes(int page, int size);
    String addDish(String name,String description,double price);
    String removeDish(String id);
    String searchDishes(String name, int page, int size);
}
