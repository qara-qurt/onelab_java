package org.onelab.user_service.service;

import org.onelab.user_service.entity.User;

public interface UserService {
    String save(User user);
    void withDrawBalance(String orderId,String userId,double totalPrice);
    void fillBalance(String userId,double amount);
}
