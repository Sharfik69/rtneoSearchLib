import java.io.IOException;

//XSSF

public class Main {
    public static void main(String[] args) throws IOException {
        ClassicFinder a = new ClassicFinder("Усть-Удинский.xlsx",
                "Усть-Удинский тестовый.xlsx",
                14,
                15,
                16,
                false,
                "reimport2",
                "cuba",
                "cuba",
                "localhost",
                "5432",
                false
        );
        long startTime = System.nanoTime();

        a.setSearchSettings(true, 4, 5, 7, 2, 10, 1);
        int[] test_ans = a.dummySearch();

        long endTime = System.nanoTime();

        long duration = (endTime - startTime) / 1000000000;
        System.out.println("\n" + duration + " секунд");
        a.saveTable();
    }
}
