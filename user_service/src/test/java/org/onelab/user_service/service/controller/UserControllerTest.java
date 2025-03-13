package org.onelab.user_service.service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.user_service.dto.UserDto;
import org.onelab.user_service.dto.UserLoginDto;
import org.onelab.user_service.service.UserService;
import org.onelab.user_service.сontroller.UserController;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDto mockUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        mockUser = UserDto.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .balance(100.0)
                .build();

    }

    @Test
    void getUser_ShouldReturnUserById() throws Exception {
        when(userService.getUserByID(1L)).thenReturn(mockUser);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(mockUser.getUsername())))
                .andExpect(jsonPath("$.balance", is(mockUser.getBalance())));

        verify(userService, times(1)).getUserByID(1L);
    }

    @Test
    void createUser_ShouldReturnCreatedStatusAndUserId() throws Exception {
        when(userService.register(any(UserDto.class))).thenReturn(1L);

        String requestBody = """
    {
      "username": "newuser",
      "password": "password",
      "phone": "1234567890",
      "name": "John",
      "surname": "Doe",
      "roles": ["USER"]
    }
    """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(userService, times(1)).register(any(UserDto.class));
    }


    @Test
    void login_ShouldReturnToken() throws Exception {
        when(userService.login(any(UserLoginDto.class))).thenReturn("mockToken");

        String requestBody = """
        {
          "username": "testuser",
          "password": "password"
        }
        """;

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("mockToken")));

        verify(userService, times(1)).login(any(UserLoginDto.class));
    }

    @Test
    void searchUsers_ShouldReturnUserList() throws Exception {
        when(userService.searchUsers(anyString(), anyInt(), anyInt())).thenReturn(List.of(mockUser));

        mockMvc.perform(get("/api/users/search?text=test&page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].username", is(mockUser.getUsername())));

        verify(userService, times(1)).searchUsers(anyString(), anyInt(), anyInt());
    }

    @Test
    void getUsers_ShouldReturnUsersList() throws Exception {
        when(userService.getUsers(anyInt(), anyInt())).thenReturn(List.of(mockUser));

        mockMvc.perform(get("/api/users?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].username", is(mockUser.getUsername())));

        verify(userService, times(1)).getUsers(anyInt(), anyInt());
    }

    @Test
    void removeUser_ShouldReturnSuccessMessage() throws Exception {
        when(userService.removeUser(1L)).thenReturn("User successfully deleted"); // ✅ Correct way

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User successfully deleted"));

        verify(userService, times(1)).removeUser(1L);
    }


    @Test
    void fillBalance_ShouldReturnSuccessMessage() throws Exception {
        doNothing().when(userService).fillBalance(anyLong(), anyDouble());

        String requestBody = """
        {
          "amount": 50.0
        }
        """;

        mockMvc.perform(post("/api/users/1/fill-balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("User successfully filled")));

        verify(userService, times(1)).fillBalance(1L, 50.0);
    }

    @Test
    void fillBalance_ShouldReturnBadRequest_WhenAmountIsNegative() throws Exception {
        String requestBody = """
        {
          "amount": -10.0
        }
        """;

        mockMvc.perform(post("/api/users/1/fill-balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Amount must be greater than 0")));

        verify(userService, never()).fillBalance(anyLong(), anyDouble());
    }
}
