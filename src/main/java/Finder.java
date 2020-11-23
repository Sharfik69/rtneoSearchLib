import java.io.*;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;

/** Класс, в котором будем открывать файл
 * @author Dmitrii Zaguzin
 * @version 0.1
 */
public class Finder {
    /**
     * inputStream - поток, в который запишем файл
     * workbook - переменная ексельки
     * sheet - переменная листа, на котором прохожит вся работа
     */
    private FileInputStream inputStream;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private String finalFileName;

    private int cadastrCol, areaCol, nameCol;

    /**
     *
     * @param fileName Имя файла в папке inputFiles
     * @param outputFileName С каким именем сохранить файл в папке outputFiles
     */
    Finder (String fileName, String outputFileName, int cadastrCol, int areaCol, int nameCol) {
        try {
            inputStream = new FileInputStream(new File("src/inputFiles/" + fileName));
            workbook = new XSSFWorkbook(inputStream);
            sheet = workbook.getSheetAt(0);
            finalFileName = outputFileName;

            setOutputInfo(cadastrCol, areaCol, nameCol);

            System.out.println("Файл открыт");
        } catch (IOException e) {
            System.out.println("Произошла ошибка");
            e.printStackTrace();
        }
    }
    public void setOutputInfo(int cadastrCol, int areaCol, int nameCol){
        this.cadastrCol = cadastrCol;
        this.areaCol = areaCol;
        this.nameCol = nameCol;
    }

}
