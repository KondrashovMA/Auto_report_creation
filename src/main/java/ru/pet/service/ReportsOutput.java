package ru.pet.service;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.pet.model.MMS.BRCB;
import ru.pet.service.mappers.OperStateMapper;
import ru.pet.service.mappers.SignalsMapHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**!
 * Обработка полученных данных из БД и вывод их во внешний документ (формата excel)
 */
@Service
public class ReportsOutput {

    private Map<String, String> reportTypeMap = new HashMap<>();

    @Autowired
    private OperStateMapper operStateMapper;

    @Autowired
    private SignalsMapHandler signalsMapHandler;

    public ReportsOutput(){
        reportTypeMap.put("warning", "ПС");
        reportTypeMap.put("error", "АС");
        reportTypeMap.put("operState", "ОС");
    }

    // развернуть по дате и отсортировать, чтобы АС шли первыми
    private List<BRCB> handleBrcbList(List<BRCB> brcbs){
        Collections.reverse(brcbs);
        return brcbs.stream().sorted((o1, o2) -> {
            if(o1.getAlarm().equals(o2.getAlarm())){
                return 0;
            }else if(o1.getAlarm().equals("error") && (!o2.getAlarm().equals("error"))){
                return -1;
            }else if(o1.getAlarm().equals("warning") && o2.getAlarm().equals("operState")){
                return -1;
            }
            return 1;
        }).collect(Collectors.toList());
    }

    public synchronized void createDocument(Map<String, List<BRCB>> map_vied, String path){
        HSSFWorkbook workbook = new HSSFWorkbook();

        for(Map.Entry<String, List<BRCB>> item : map_vied.entrySet()){
            item.setValue(item.getValue().stream().sorted((o1, o2) -> {
                        if(o1.getDate().getTime() > o2.getDate().getTime()){
                            return -1;
                        }else if(o1.getDate().getTime() == o2.getDate().getTime()){
                            return 0;
                        }
                        return 1;
                    }
            ).collect(Collectors.toList()));

            // развернуть массив событий и отсортировать
            item.setValue(handleBrcbList(item.getValue()));

            HSSFSheet sheet = workbook.createSheet(item.getKey());
            Row row = sheet.createRow(0);

            // создание шапки для таблицы
            Cell numCell = row.createCell(0);
            numCell.setCellValue("№ п.п.");
            Cell vIEDnameCell = row.createCell(1);
            vIEDnameCell.setCellValue("vIED");
            Cell dateCell = row.createCell(2);
            dateCell.setCellValue("Дата время");
            Cell eventCell = row.createCell(3);
            eventCell.setCellValue("Наименование события");
            Cell typeEventCell = row.createCell(4);
            typeEventCell.setCellValue("Тип события (АС, ПС, ОС)");
            Cell valueCell = row.createCell(5);
            typeEventCell.setCellValue("Значение");

            // заполнение страницы
            int rowConter = 1;
            for(BRCB brcb : item.getValue()){
                row = sheet.createRow(rowConter);

                numCell = row.createCell(0);
                numCell.setCellValue(rowConter);

                vIEDnameCell = row.createCell(1);
                vIEDnameCell.setCellValue(brcb.getVied());

                dateCell = row.createCell(2);
                dateCell.setCellValue(String.valueOf(brcb.getDate()));

                eventCell = row.createCell(3);

                if(brcb.getAlarm().equals("operState")){
                    eventCell.setCellValue(operStateMapper.getEventDescription(brcb));
                }else{
                    String pathToFormat = brcb.getPath().split("\\.")[0] + "." + brcb.getPath().split("\\.")[1];
                    String description = signalsMapHandler.findTranslatedEvent(pathToFormat, Boolean.parseBoolean(brcb.getValue()));
                    if(description == null){
                        description = brcb.getPath() + " в значении " + brcb.getValue();
                    }
                    eventCell.setCellValue(description);
                }
                typeEventCell = row.createCell(4);
                typeEventCell.setCellValue(reportTypeMap.get(brcb.getAlarm()));

                valueCell = row.createCell(5);
                valueCell.setCellValue(brcb.getValue());

                rowConter++;
            }
        }

        File file = new File(path);
        file.getParentFile().mkdirs();

        FileOutputStream outFile;
        try {
            outFile = new FileOutputStream(file);
            workbook.write(outFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}