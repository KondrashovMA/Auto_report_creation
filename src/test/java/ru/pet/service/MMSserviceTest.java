package ru.pet.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.pet.ReportCreation.ReportsCreator;
import ru.pet.model.MMS.BRCB;
import ru.pet.repository.secondTherminal.SecondTherminalRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
public class MMSserviceTest {
    @Autowired
    ReportsOutput reportsOutput;
    @Autowired
    ReportsCreator reportsCreator;

    @Autowired
    SecondTherminalRepository secondTherminalRepository;

    private final int period = 14;
    @Test
    @SneakyThrows
    public void TestConfigSecondTherminal(){
        List<BRCB> brcbs = secondTherminalRepository.findAll()
                .stream()
                .filter(brcb ->
                    period >= Math.abs(ChronoUnit.DAYS.between(LocalDate.now(), brcb.getDate().toLocalDateTime())))
                .collect(Collectors.toList());
        System.out.println(brcbs.size());
    }
}
