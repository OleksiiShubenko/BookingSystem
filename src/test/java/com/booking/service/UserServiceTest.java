package com.booking.service;

import com.booking.dataModel.User;
import com.booking.dataModel.dto.UserDto;
import com.booking.dataModel.exceptions.EntityNotFoundException;
import com.booking.dataModel.exceptions.EntityExistException;
import com.booking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private final String USERNAME = "testUser";
    private final UserDto validUserDto = new UserDto(USERNAME, "password123");
    private final User existingUser = new User(USERNAME, "password123", List.of());

    @Test
    void getUser_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(USERNAME)).thenReturn(Optional.of(existingUser));

        User result = userService.getUser(USERNAME);

        assertEquals(existingUser, result);

        verify(userRepository, times(1)).findById(USERNAME);
    }

    @Test
    void getUser_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.findById(USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.getUser(USERNAME));

        assertEquals("User with username: testUser does not exist", exception.getMessage());

        verify(userRepository, times(1)).findById(USERNAME);
    }

    @Test
    void createUser_ShouldReturnUserDto_WhenUserIsCreated() {
        when(userRepository.existsById(USERNAME)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserDto result = userService.createUser(validUserDto);

        assertEquals(USERNAME, result.username());
        assertEquals("*****", result.password());

        verify(userRepository, times(1)).existsById(USERNAME);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenUserAlreadyExists() {
        when(userRepository.existsById(USERNAME)).thenReturn(true);

        EntityExistException exception = assertThrows(EntityExistException.class, () -> userService.createUser(validUserDto));

        assertEquals("User with username: testUser already exist", exception.getMessage());

        verify(userRepository, times(1)).existsById(USERNAME);
        verify(userRepository, never()).save(any(User.class));
    }
}
