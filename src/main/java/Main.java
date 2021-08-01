import java.io.*;
import java.util.HashMap;
import java.nio.file.Files;
import java.util.Map;
import com.google.gson.Gson;
//XSSF

public class Main {
    public static void main(String[] args) throws IOException {

        String fileName = "Саянск";
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
        int[] test_ans = a.dummySearch();

        long endTime = System.nanoTime();

        long duration = (endTime - startTime) / 1000000000;
        System.out.println("\n" + duration + " секунд");
        a.saveTable();
        a.createCSV();
    }
}
