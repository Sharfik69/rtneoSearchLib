import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {

        String fileName = "г Усолье-Сибирское";
        ClassicFinder a = new ClassicFinder(fileName + ".xlsx",
                fileName + " синхронизированные.xlsx",
                10,
                11,
                12,
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
                5,
                4,
                100,
                7,
                100,
                0);
        int[] test_ans = a.dummySearch();

        long endTime = System.nanoTime();

        long duration = (endTime - startTime) / 1000000000;
        System.out.println("\n" + duration + " секунд");
        a.saveTable();
    }
}
