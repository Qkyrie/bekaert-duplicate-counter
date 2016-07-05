package com.deswaef.bekaert;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class BekaertApplication implements CommandLineRunner {

    public static final String INPUT_FILE = "input.txt";
    public static final String OUTPUT_FILE = "output.xls";

    @Override
    public void run(String... args) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader(new File(INPUT_FILE)));

        Map<String, Long> occurrences = new HashMap<>();

        br.lines()
                .forEach(x -> {
                    if (occurrences.containsKey(x)) {
                        occurrences.put(x, (occurrences.get(x) + 1));
                    } else {
                        occurrences.put(x, 1L);
                    }
                });

        Map<Long, List<String>> finalMap = new HashMap<>();

        occurrences
                .entrySet()
                .stream()
                .forEach(entry -> {
                    if (finalMap.containsKey(entry.getValue())) {
                        List<String> strings = finalMap.get(entry.getValue());
                        strings.add(entry.getKey());
                        finalMap.put(entry.getValue(), strings);
                    } else {
                        ArrayList<String> strings = new ArrayList<>();
                        strings.add(entry.getKey());
                        finalMap.put(entry.getValue(), strings);
                    }
                });

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Love Sheet");


        for (Long occurrence : finalMap.keySet()) {
            List<String> strings = finalMap.get(occurrence);
            for (String theString : strings) {
                int theIndex = strings.indexOf(theString);
                if (sheet.getRow(theIndex) == null) {
                    HSSFRow row = sheet.createRow(theIndex);
                    HSSFCell cell = row.createCell(occurrence.intValue());
                    cell.setCellValue(theString);
                } else {
                    HSSFRow row = sheet.getRow(theIndex);
                    HSSFCell cell = row.createCell(occurrence.intValue());
                    cell.setCellValue(theString);
                }
            }
        }
        try {
            FileOutputStream out =
                    new FileOutputStream(new File(OUTPUT_FILE));
            workbook.write(out);
            out.close();
            System.out.println("Excel written successfully..");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(BekaertApplication.class, args);
    }
}
