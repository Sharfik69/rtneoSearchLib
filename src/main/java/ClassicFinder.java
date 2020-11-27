import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassicFinder extends Finder{

    String databaseName, login, password;
    private DatabaseConnection connection;
    private boolean printInfo, settingsWasEdited = false;
    private int streetCol, houseCol, apartmentCol, infoCol, checker, infoAreaCol;

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
     * @param createTable    Нужно ли создавать таблицу, работа с которой будет намного быстрее
     */
    ClassicFinder(String fileName, String outputFileName, int cadastrCol, int areaCol, int nameCol,
                  boolean header, String databaseName, String login, String password, String addr, String port, boolean createTable) {
        super(fileName, outputFileName, cadastrCol, areaCol, nameCol, header);
        this.databaseName = databaseName;
        this.login = login;
        this.password = password;
        this.connection = new DatabaseConnection(databaseName, login, password, addr, port);
        if (createTable) {
            boolean ans = this.connection.createSuperTable();
            System.out.println(ans ? "Супер таблица была создана" : "Таблица скорее всего уже существует");
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
    public void setSearchSettings(boolean printInfo, int streetCol, int houseCol, int apartmentCol, int infoCol, int infoAreaCol, int checker) {
        this.printInfo = printInfo;
        this.streetCol = streetCol;
        this.houseCol = houseCol;
        this.apartmentCol = apartmentCol;
        this.infoCol = infoCol;
        this.infoAreaCol = infoAreaCol;
        this.checker = checker;
        this.settingsWasEdited = true;
    }

    /**
     * Самый обычный поиск, который никак не изменяет данные
     * @return возвращает массив, 0 элемент количество найденных, 1 элемент количество не найденных, 2 элемент количество нескольких найденных записей
     */
    public int[] dummySearch() {
        System.out.println("Run");
        int [] ans = new int[3];
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
                List<Map<String, String> > responses = connection.sendQuery(street, house, complementaryInfo);
                if (responses.size() == 1) {
                    Map <String, String> responseMap = responses.get(0);
                    setCadastr(i, responseMap, sheet);
//                    sheet.getRow(i).createCell(getCadastrCol(), CellType.STRING).setCellValue(responseMap.get("cadastral_number"));
//                    sheet.getRow(i).createCell(getAreaCol(), CellType.STRING).setCellValue(responseMap.get("area"));
//                    sheet.getRow(i).createCell(getNameCol(), CellType.STRING).setCellValue(responseMap.get("name"));
                    ans[0]++;
                }
                else if (responses.size() > 0) {
                    ArrayList<Map <String, String> > potentialAddress = new ArrayList<Map<String, String>>();
                    for (Map <String, String> response : responses) {
                        try {
                            BigDecimal responseArea = new BigDecimal(response.get("area")),
                                    addressArea = new BigDecimal(getXCell(i, infoAreaCol, sheet)),
                                    difference;

                            difference = responseArea.subtract(addressArea).abs();

                            if (difference.compareTo(new BigDecimal("0.75")) < 0) {
                                potentialAddress.add(response);
                            }
                        }
                        catch (Exception ignor) {}
                    }
                    if (potentialAddress.size() == 1) {
                        setCadastr(i, potentialAddress.get(0), sheet);
                        ans[0]++;
                    }
                    else {
                        ans[2]++;
                    }
                    //TODO: Сравнение по площади

                }
                else {
                    ans[1]++;
                }
            }
            else {
               // connection.sendQuery(street, house, apartment, complementaryInfo);
            }
            System.out.println(ans[0] + " " + ans[1] + " " + ans[2]);
        }

        return new int[]{-1, -1, -1, -1};
    }

    private void setCadastr(int row, Map <String, String> responseMap, XSSFSheet sheet) {
        sheet.getRow(row).createCell(getCadastrCol(), CellType.STRING).setCellValue(responseMap.get("cadastral_number"));
        sheet.getRow(row).createCell(getAreaCol(), CellType.STRING).setCellValue(responseMap.get("area"));
        sheet.getRow(row).createCell(getNameCol(), CellType.STRING).setCellValue(responseMap.get("name"));
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
