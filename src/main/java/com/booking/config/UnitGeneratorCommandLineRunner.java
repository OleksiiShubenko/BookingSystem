package com.booking.config;

import com.booking.dataModel.UnitType;
import com.booking.dataModel.dto.UnitDto;
import com.booking.service.UnitService;
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

    @Autowired
    public UnitGeneratorCommandLineRunner(UnitService unitService) {
        this.unitService = unitService;
    }

    @Override
    public void run(String... args) throws Exception {
        var random = new Random();
        List<UnitDto> generatedUnits = IntStream.range(1, 91)
                .mapToObj(i -> new UnitDto(
                        random.nextInt(5) + 1,
                        UnitType.values()[random.nextInt(3)],
                        random.nextInt(10) + 1,
                        random.nextInt(1500) + 50,
                        "Generated unit " + i
                )).toList();
        generatedUnits.forEach(unitService::createUnit);
    }

}
