package ru.pet.service.mappers;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Iterator;

@Component
@Slf4j
public class SignalsMapHandler {
    private String path = "path_to_file_with_signals_map";
    private final int pathPosition = 0; // номер столбца с путём в файле
    private final int descriptionPosition = 0; // номер столбца с описанием на русском

    public String findTranslatedEvent(String protectionPath, boolean value) {
        String result = null;
        try {
            FileInputStream excelFile = new FileInputStream(new File(path));
            Workbook workbook = new XSSFWorkbook(excelFile);

            // первый лист для обработки
            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.iterator();

            int counter = 0;
            while (rowIterator.hasNext()){
                Cell cell = rowIterator.next().getCell(pathPosition);
                if(cell.getRichStringCellValue().getString().equals(protectionPath)){
                    result = sheet.getRow(counter).getCell(descriptionPosition).getRichStringCellValue().getString();
                    if(!value){
                        result = "Снятие сигнала " + result.substring(0,1).toLowerCase() + result.substring(1);
                    }
                    return result;
                }
                counter++;
            }
            log.error("Path to vied " + protectionPath + " don't found");

            excelFile.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
