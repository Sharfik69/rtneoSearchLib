import java.io.IOException;

//XSSF

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");

        ClassicFinder a = new ClassicFinder("1111.xlsx",
                "1111 новый.xlsx",
                14,
                15,
                16,
                true,
                "reimport2",
                "cuba",
                "cuba",
                "localhost",
                "5432",
                false
                );

        a.setSearchSettings(true, 4, 5, 7, 2, 1);
        int [] test_ans = a.dummySearch();
        System.out.println(test_ans);
        a.saveTable();
//        HSSFWorkbook workbook = new HSSFWorkbook();
//        HSSFSheet sheet = workbook.createSheet("Employees sheet");

//        XSSFWorkbook workbook = new XSSFWorkbook();
//        XSSFSheet sheet = workbook.createSheet("TEST_SHEET");

//        FileInputStream inputStream = new FileInputStream(new File("test.xlsx"));
//
//        // Get the workbook instance for XLS file
//        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
////        XSSFSheet sheet = workbook.createSheet("TEST_SHEET");
//        XSSFSheet sheet = workbook.getSheetAt(0);
//
//        int rownum = 0;
//        Cell cell;
//        Row row;
//        //
//
//        XSSFCell cell1 = sheet.getRow(0).getCell(0);
//        System.out.println(cell1.getStringCellValue());
//        row = sheet.createRow(rownum);
////
////        cell = row.createCell(0, CellType.STRING);
////        cell.setCellValue("ZHOPA");
//        // EmpName
//        cell = row.createCell(1, CellType.STRING);
//        cell.setCellValue("EmpNo");
//        // Salary
//        cell = row.createCell(2, CellType.STRING);
//        cell.setCellValue("Salary");
//        // Grade
//        cell = row.createCell(3, CellType.STRING);
//        cell.setCellValue("Grade");
//        // Bonus
//        cell = row.createCell(4, CellType.STRING);
//        cell.setCellValue("Bonus");
//
//        File file = new File("test.xlsx");
//
//        FileOutputStream outFile = new FileOutputStream(file);
//        workbook.write(outFile);
//        System.out.println("Created file: " + file.getAbsolutePath());
    }
}
