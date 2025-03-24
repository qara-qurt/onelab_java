package org.onelab.gateway_cli_service.client;

import org.onelab.gateway_cli_service.config.ClientConfig;
import org.onelab.common_lib.dto.UserDto;
import org.onelab.common_lib.dto.UserLoginDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "user-service", configuration = ClientConfig.class)
public interface UserClient {

    @PostMapping("/api/users/register")
    UserDto registerUser(@RequestBody UserDto userRegistrationDto);

    @PostMapping("/api/users/login")
    Map<String, String> loginUser(@RequestBody UserLoginDto userLoginDto);

    @GetMapping("/api/users/{id}")
    UserDto getUserById(@PathVariable Long id);

    @GetMapping("/api/users")
    List<UserDto> getAllUsers(@RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int size);

    @DeleteMapping("/api/users/{id}")
    void deleteUser(@PathVariable Long id);
}
