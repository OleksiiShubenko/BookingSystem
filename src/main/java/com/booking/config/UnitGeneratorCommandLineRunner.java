package com.booking.config;

import com.booking.dataModel.UnitType;
import com.booking.dataModel.dto.UnitDto;
import com.booking.dataModel.dto.UserDto;
import com.booking.service.UnitService;
import com.booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * UnitGeneratorCommandLineRunner is used to crate units after application started
 */
@Component
public class UnitGeneratorCommandLineRunner implements CommandLineRunner {

    private final UnitService unitService;
    private final UserService userService;

    @Autowired
    public UnitGeneratorCommandLineRunner(UnitService unitService, UserService userService) {
        this.unitService = unitService;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        if (!userService.isUserExist("genUser")) {
            var generatedUser = new UserDto("genUser", "genPas");
            userService.createUser(generatedUser);
        }

        var random = new Random();
        List<UnitDto> generatedUnits = IntStream.range(1, 91)
                .mapToObj(i -> new UnitDto(
                        "genUser",
                        random.nextInt(5) + 1,
                        UnitType.values()[random.nextInt(3)],
                        random.nextInt(10) + 1,
                        random.nextInt(1500) + 50,
                        "Generated unit " + i
                )).toList();

        generatedUnits.forEach(unitService::createUnit);
    }

}
