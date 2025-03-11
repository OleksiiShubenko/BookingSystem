package com.booking.controller;

import com.booking.dataModel.dto.UserDto;
import com.booking.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void createUnit_ShouldReturnCreatedUserDto() {
        UserDto userDto = new UserDto("testUser", "securePassword");
        UserDto expectedUserDto = new UserDto("testUser", "securePassword");
        when(userService.createUser(userDto)).thenReturn(expectedUserDto);

        UserDto actualUserDto = userController.createUser(userDto);

        assertNotNull(actualUserDto);
        assertEquals(expectedUserDto, actualUserDto);
        verify(userService, times(1)).createUser(userDto);
    }
}
