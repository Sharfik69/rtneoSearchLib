import java.io.*;
import java.util.HashMap;
import java.nio.file.Files;
import java.util.Map;
import com.google.gson.Gson;
//XSSF

public class Main {
    public static void main(String[] args) throws IOException {

        String fileName = "Краснокаменск";
        ClassicFinder a = new ClassicFinder(fileName + ".xlsx",
                fileName + " синхронизированные.xlsx",
                10,
                12,
                11,
                false,
                "chita",
                "cuba",
                "cuba",
                "localhost",
                "5432",
                false
        );
        long startTime = System.nanoTime();

        a.setSearchSettings(true,
                2,
                2,
                4,
                1,
                7,
                1);
        int[] test_ans = a.dummySearch();

        long endTime = System.nanoTime();

        long duration = (endTime - startTime) / 1000000000;
        System.out.println("\n" + duration + " секунд");
        a.saveTable();
    }
}
