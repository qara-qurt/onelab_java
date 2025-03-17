package org.onelab.user_service.сontroller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.onelab.user_service.dto.UserDto;
import org.onelab.user_service.dto.UserLoginDto;
import org.onelab.user_service.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(UserController.BASE_URL)
public class UserController {

    // Base URL
    public static final String BASE_URL = "api/users";

    // Endpoints
    public static final String REGISTER = "/register";
    public static final String LOGIN = "/login";
    public static final String USER_BY_ID = "/{id}";
    public static final String SEARCH = "/search";
    public static final String FILL_BALANCE = "{id}/fill-balance";
    public static final String FILTER_BALANCE_ELASTIC = "/filter/balance/elastic";
    public static final String FILTER_BALANCE_STREAM = "/filter/balance/stream";
    public static final String FILTER_BIRTH_YEAR = "/filter/birth-year";
    public static final String COMPARE = "/compare-streams";



    private final UserService userService;

    @GetMapping(USER_BY_ID)
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserByID(id));
    }

    @PostMapping(REGISTER)
    public ResponseEntity<Map<String,Long>> createUser(@Valid @RequestBody UserDto user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", userService.register(user)));
    }

    @PostMapping(LOGIN)
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody UserLoginDto userLoginDto) {
        return ResponseEntity.ok(Map.of("token", userService.login(userLoginDto)));
    }

    @GetMapping(SEARCH)
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String text,
                                                     @RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.searchUsers(text, page, size));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.getUsers(page, size));
    }

    @DeleteMapping(USER_BY_ID)
    public ResponseEntity<Map<String, String>> removeUser(@PathVariable Long id) {
        userService.removeUser(id);
        return ResponseEntity.ok(Map.of("message", "User successfully deleted"));
    }

    @PostMapping(FILL_BALANCE)
    public ResponseEntity<Map<String,String>> fillBalance(@PathVariable Long id,
                                              @RequestBody Map<String, Double> request) {
        Double amount = request.get("amount");

        if (amount <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Amount must be greater than 0"));
        }
        userService.fillBalance(id, amount);
        return ResponseEntity.ok(Map.of("message", "User successfully filled"));
    }

    @GetMapping(FILTER_BALANCE_STREAM)
    public ResponseEntity<List<UserDto>> filterUsersByBalanceStream(@RequestParam(required = false) Double minBalance,
                                                              @RequestParam(required = false) Double maxBalance,
                                                              @RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.filterStreamUsers(minBalance,maxBalance, page, size));
    }

    @GetMapping(FILTER_BALANCE_ELASTIC)
    public ResponseEntity<List<UserDto>> filterUsersByBalanceElastic(@RequestParam(required = false) Double minBalance,
                                                              @RequestParam(required = false) Double maxBalance,
                                                              @RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.filterElasticUsers(minBalance,maxBalance, page, size));
    }

    @GetMapping(FILTER_BIRTH_YEAR)
    public ResponseEntity<List<UserDto>> filterByBirthDateElasticsearch(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "birthDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(userService.filterBirthDate(startDate, endDate, page, size, sortBy,sortOrder));
    }

    @GetMapping(COMPARE)
    public ResponseEntity<Map<String, Long>> compareStreamPerformance() {
        Map<String, Long> result = userService.compareStreamPerformance();
        return ResponseEntity.ok(result);
    }


}
