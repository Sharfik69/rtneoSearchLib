import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Класс, в котором будем открывать файл
 *
 * @author Dmitrii Zaguzin
 * @version 0.1
 */
abstract class Finder {
    protected boolean header;
    private String fileName;
    private FileInputStream inputStream;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private String finalFileName;
    private int cadastrCol, areaCol, nameCol;

    /**
     * @param fileName       Имя файла в папке inputFiles
     * @param outputFileName С каким именем сохранить файл в папке outputFiles
     * @param cadastrCol     в какой столбец записывать кадастр
     * @param areaCol        в какой столбец записывать площадь
     * @param nameCol        в какой столбец записывать описание адреса
     * @param header         есть ли шапка в таблице, если есть, то в этом случае обрабатываем все со второй строки
     */
    Finder(String fileName, String outputFileName, int cadastrCol, int areaCol, int nameCol, boolean header) {
        try {
            this.fileName = fileName;

            File theDir = new File(String.format("src/inputFiles/response/%s", fileName.replace(".xlsx", "")));
            if (!theDir.exists()) {
                theDir.mkdirs();
            }

            inputStream = new FileInputStream(new File("src/inputFiles/from/" + fileName));
            workbook = new XSSFWorkbook(inputStream);
            sheet = workbook.getSheetAt(0);
            finalFileName = outputFileName;
            this.header = header;
            setOutputInfo(cadastrCol, areaCol, nameCol);

            System.out.println("Файл открыт");
        } catch (IOException e) {
            System.out.println("Произошла ошибка");
            e.printStackTrace();
        }
    }

    public void saveTable() throws IOException {
        File file = new File("src/inputFiles/response/" + fileName.replace(".xlsx", "") + "/" + finalFileName);

        FileOutputStream outFile = new FileOutputStream(file);
        workbook.write(outFile);
    }

    /**
     * @param cadastrCol в какой столбец ставить кадастр
     * @param areaCol    в какой столбец ставить площадь
     * @param nameCol    в какой столбец выводить описание
     */
    public void setOutputInfo(int cadastrCol, int areaCol, int nameCol) {
        this.cadastrCol = cadastrCol;
        this.areaCol = areaCol;
        this.nameCol = nameCol;
    }

    public XSSFSheet getSheet() {
        return sheet;
    }

    public int getCadastrCol() {
        return cadastrCol;
    }

    public int getAreaCol() {
        return areaCol;
    }

    public int getNameCol() {
        return nameCol;
    }

    public String getFileName() {
        return fileName.replace(".xlsx", "");
    }
}
