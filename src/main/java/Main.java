import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
//XSSF

public class Main {
    public static void main(String[] args) throws IOException {
        getFromFolder("src/inputFiles/from");
    }

    public static void getFromFolder(String fromFolder) {
        final File folder = new File(fromFolder);
        List<String> files = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            files.add(fileEntry.getName().replace(".xlsx", ""));
        }

        for (String f : files) {
            try {
                System.out.println();
                for (int ii = 0; ii < 20; ii++) System.out.print("-");
                System.out.println("\nРегион: " + f);
                classic(f);
                for (int ii = 0; ii < 20; ii++) System.out.print("-");
                System.out.println();
            } catch (IOException | SQLException e) {
                System.out.println("opa gavno");
                e.printStackTrace();
            }
        }


    }

    public static void classic(String regName) throws IOException, SQLException {
        String fileName = regName;
        ClassicFinder a = new ClassicFinder(fileName + ".xlsx",
                fileName + " синхронизированные.xlsx",
                17,
                18,
                19,
                false,
                "reimport2",
                "cuba",
                "cuba",
                "localhost",
                "5432",
                false
        );
        long startTime = System.nanoTime();

        a.setSearchSettings(true,
                11,
                12,
                13,
                10,
                20,
                0);

        a.createTableForFast();

        int[] test_ans = a.dummySearch();

        a.deleteTableForFast();

        long endTime = System.nanoTime();

        long duration = (endTime - startTime) / 1000000000;
        System.out.println("\n" + duration + " секунд");
        a.saveTable();
        a.createCSV();
        a.closeConnection();
    }
}
