package com.booking.service;

import com.booking.dataModel.User;
import com.booking.dataModel.dto.UserDto;
import com.booking.dataModel.exceptions.EntityNotFoundException;
import com.booking.dataModel.exceptions.EntityExistException;
import com.booking.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Boolean isUserExist(String username){
        return userRepository.existsById(username);
    }

    /**
     * Returns user by username
     */
    public User getUser(String username) {
        Optional<User> user = userRepository.findById(username);

        if (user.isEmpty()) {
            throw new EntityNotFoundException("User with username: " + username + " does not exist");
        }
        return user.get();
    }

    /**
     * Crete a new user by provided parameters
     */
    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsById(userDto.username())) {
            throw new EntityExistException("User with username: " + userDto.username() + " already exist");
        }

        var user = User.builder()
                .username(userDto.username())
                .password(userDto.password())
                .build();

        return mapUserToDto(userRepository.save(user));
    }

    private UserDto mapUserToDto(User user) {
        return new UserDto(user.getUsername(), "*****");
    }
}
