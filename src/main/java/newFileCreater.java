import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class newFileCreater {
    private Workbook wb;
    private XSSFSheet sheet;
    private int currentRow;
    private Finder finder;
    newFileCreater(Finder finder) {
        wb = new XSSFWorkbook();
        sheet = (XSSFSheet) wb.createSheet();
        currentRow = 0;
        this.finder = finder;
    }

    public void addRecords(XSSFRow rowInfo, int colLimit, List<Map<String, String>> responses) {
        XSSFRow currentNewRow;
        currentNewRow = sheet.createRow(currentRow);
        for (int col = 0; col < colLimit; col++) {
            XSSFCell cell = currentNewRow.createCell(col);
            try {
                cell.setCellValue(rowInfo.getCell(col).getStringCellValue());
            } catch (IllegalStateException e) {
                cell.setCellValue(String.valueOf((int) rowInfo.getCell(col).getNumericCellValue()));
            } catch (NullPointerException e) {
                continue;
            }
        }
        for (Map<String, String> response : responses) {
            currentRow += 1;
            sheet.createRow(currentRow);
            setCadastr(currentRow, response, sheet);
        }
        currentRow += 1;
    }

    public void saveFile(String finalFileName) throws IOException {
        File file = new File("src/inputFiles/" + finalFileName);

        FileOutputStream outFile = new FileOutputStream(file);
        wb.write(outFile);
    }

    private void setCadastr(int row, Map<String, String> responseMap, XSSFSheet sheet) {
        sheet.getRow(row).createCell(finder.getCadastrCol(), CellType.STRING).setCellValue(responseMap.get("cadastral_number"));
        sheet.getRow(row).createCell(finder.getAreaCol(), CellType.STRING).setCellValue(responseMap.get("area"));
        sheet.getRow(row).createCell(finder.getNameCol(), CellType.STRING).setCellValue(responseMap.get("name"));
    }


}
