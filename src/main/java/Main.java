import java.io.IOException;
import java.util.Arrays;

//XSSF

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");

        ClassicFinder a = new ClassicFinder("1111.xlsx",
                "1111 новый.xlsx",
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

        a.setSearchSettings(true, 4, 5, 7, 2, 12, 1);
        int[] test_ans = a.dummySearch();
        System.out.println(Arrays.toString(test_ans));
        a.saveTable();
    }
}
