package ru.pet.ReportCreation;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ru.pet.model.MMS.BRCB;
import ru.pet.repository.firstTherminal.FirstTherminalRepository;
import ru.pet.repository.fourthTherminal.FourthTherminalRepository;
import ru.pet.repository.secondTherminal.SecondTherminalRepository;
import ru.pet.repository.thirdTherminal.ThirdTherminalRepository;
import ru.pet.service.ReportsOutput;

import javax.annotation.PostConstruct;
import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**!
 * Сервис, в котором происходит получение данных из БД и заполнение документа с событиями
 */
@Getter
@Slf4j
@Service
public class ReportsCreator {
    @Autowired
    FirstTherminalRepository firstTherminalRepository;
    @Autowired
    SecondTherminalRepository secondTherminalRepository;
    @Autowired
    ThirdTherminalRepository thirdTherminalRepository;
    @Autowired
    FourthTherminalRepository fourthTherminalRepository;

    @Autowired
    ReportsOutput reportsOutput;

    List<JpaRepository<BRCB, Long>> repositories = new ArrayList<>();

    @Value("${functions}")
    private List<String> vIEDS;

    java.sql.Date dateFinish = new Date(System.currentTimeMillis());
    java.sql.Date dateStart = Date.valueOf(LocalDate.now().minus(Period.ofDays(30)));

    @PostConstruct
    public void Run(){
        repositories.add(firstTherminalRepository);
        repositories.add(secondTherminalRepository);
        repositories.add(thirdTherminalRepository);
        repositories.add(fourthTherminalRepository);

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for(JpaRepository<BRCB, Long> repository : repositories){
            executorService.execute(() -> createReport(repository));
        }
        executorService.shutdown();
    }

    public void createReport(JpaRepository<BRCB, Long> repository){
        Map<String, List<BRCB>> map_vied = new HashMap<>();
        List<String> vieds = this.getVIEDS();
        if(vieds.isEmpty()){
            log.error("Error while getting data from data Base ");
        }else {
            for (String vied : vieds) {
                List<BRCB> temp_brcbs = repository.findAll()
                        .stream()
                        .filter(brcb -> (brcb.getVied().equals(vied) && (brcb.getDate().getTime() > dateStart.getTime()) && (brcb.getDate().getTime() < dateFinish.getTime())))
                        .collect(Collectors.toList());
                if (!temp_brcbs.isEmpty()) {
                    map_vied.put(vied, temp_brcbs);
                }else{
                    log.error("Error while getting vieds from vied: " + vied);
                }
            }
            if(!map_vied.isEmpty()){
                String fileName =  String.format("C:/demo/DataBaseEvents_therminal_%s_.xls", (repositories.indexOf(repository) + 1));
                reportsOutput.createDocument(map_vied, fileName);
                map_vied.clear();
                File f = new File(fileName);
                if(f.exists() && !f.isDirectory()) {
                    log.info("Document succsessfully created: " + fileName);
                }
            }
        }
    }
}
