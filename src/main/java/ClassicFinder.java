import java.io.*;
import java.sql.SQLException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;

public class ClassicFinder extends Finder{

    String databaseName, login, password;
    private DatabaseConnection connection;
    private boolean printInfo, settingsWasEdited = false;
    private int streetCol, houseCol, apartmentCol, infoCol, checker;

    /**
     * @param fileName       Имя файла в папке inputFiles
     * @param outputFileName С каким именем сохранить файл в папке outputFiles
     * @param cadastrCol     в какой столбец записывать кадастр
     * @param areaCol        в какой столбец записывать площадь
     * @param nameCol        в какой столбец записывать описание адреса
     * @param header         есть ли шапка в таблице, если есть, то в этом случае обрабатываем все со второй строки
     * @param databaseName   название базы данные откуда брать информацию
     * @param login          Логин от базы
     * @param password       Пароль от базы
     */
    ClassicFinder(String fileName, String outputFileName, int cadastrCol, int areaCol, int nameCol,
                  boolean header, String databaseName, String login, String password, String addr, String port) {
        super(fileName, outputFileName, cadastrCol, areaCol, nameCol, header);
        this.databaseName = databaseName;
        this.login = login;
        this.password = password;
        try {
            this.connection = new DatabaseConnection(databaseName, login, password, addr, port);
        } catch (SQLException e) {
            System.out.println("Не удалось подключиться к базе данных");
            return;
        }
    }

    /**
     *
     * @param printInfo печатать ли в терминал информацию о том, как проходит поиск
     * @param streetCol столбец с улицей
     * @param houseCol столбец с номером дома
     * @param apartmentCol столбец с номером квартиры, даже если пустой
     * @param infoCol столб с дополнительной информацией, сюда лучше вставлять номер столбца с регионом
     * @param checker номер столбца, где пустое поле будет означать конец файла. Подойдет столб с непрерывной инфой
     */
    public void setSearchSettings(boolean printInfo, int streetCol, int houseCol, int apartmentCol, int infoCol, int checker) {
        this.printInfo = printInfo;
        this.streetCol = streetCol;
        this.houseCol = houseCol;
        this.apartmentCol = apartmentCol;
        this.infoCol = infoCol;
        this.checker = checker;

        this.settingsWasEdited = true;
    }

    /**
     * Самый обычный поиск, который никак не изменяет данные
     * @return возвращает массив, 0 элемент количество найденных, 1 элемент количество не найденных, 2 элемент количество нескольких найденных записей
     */
    public int[] dummySearch() {
        if (!settingsWasEdited) {
            return new int[]{-1, -1, -1, -1};
        }
        XSSFSheet sheet = getSheet();
        int from = (super.header ? 1 : 0);
        int rowSize = 0;
        String ss;
        for (int i = from; ; i++, rowSize++) {
            try {
                ss = sheet.getRow(i).getCell(checker).getStringCellValue();
            }
            catch (NullPointerException e) {
                break;
            }
        }
        for (int i = from, cnt = 0; cnt <= rowSize; i++, cnt++) {
            String street = getXCell(i, streetCol, sheet).toUpperCase();
            String house = getXCell(i, houseCol, sheet);
            String apartment = getXCell(i, apartmentCol, sheet);
            String complementaryInfo = getXCell(i, infoCol, sheet);

            if (apartment.equals("-") || apartment.equals("")) {
                connection.sendQuery(street, house, complementaryInfo);
            }
            else {
                connection.sendQuery(street, house, apartment, complementaryInfo);
            }
        }

        return new int[]{-1, -1, -1, -1};
    }

    private String getXCell(int row, int col, XSSFSheet sheet) {
        try {
            return sheet.getRow(row).getCell(col).getStringCellValue();
        }
        catch (NullPointerException e) {
            return "";
        }
    }


}
