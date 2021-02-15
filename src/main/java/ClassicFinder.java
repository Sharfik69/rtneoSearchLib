import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

public class ClassicFinder extends Finder {

    String databaseName, login, password;
    private DatabaseConnection connection;
    private boolean printInfo, settingsWasEdited = false;
    private int streetCol, houseCol, apartmentCol, infoCol, checker, infoAreaCol;
    private newFileCreater forFewRecords;
    private int[] status;
    private newFileCreater forAreaRecords;

    private ArrayList <String> foundAddresses;

    private Set <String> dontUsed;
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
                  boolean header, String databaseName, String login, String password, String addr, String port, boolean createTable) throws IOException {
        super(fileName, outputFileName, cadastrCol, areaCol, nameCol, header);
        this.databaseName = databaseName;
        this.login = login;
        this.password = password;
        this.connection = new DatabaseConnection(databaseName, login, password, addr, port);

        this.forFewRecords = new newFileCreater(this);
        this.forAreaRecords = new newFileCreater(this);
        if (createTable) {
            boolean ans = this.connection.createSuperTable();
            System.out.println(ans ? "Супер таблица была создана" : "Таблица скорее всего уже существует");
        }

        foundAddresses = new ArrayList<>();

        //вынужденная временная мера

        InputStream is = new FileInputStream("src/inputFiles/removed.json");
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));

        String line = buf.readLine();
        StringBuilder sb = new StringBuilder();
        while(line != null){
            sb.append(line).append("\n");
            line = buf.readLine();
        }

        String fileAsString = sb.toString();
        Map<String, String> map = new Gson().fromJson(fileAsString, Map.class);
        this.dontUsed = map.keySet();

    }

    /**
     * @param printInfo    печатать ли в терминал информацию о том, как проходит поиск
     * @param streetCol    столбец с улицей
     * @param houseCol     столбец с номером дома
     * @param apartmentCol столбец с номером квартиры, даже если пустой
     * @param infoCol      столб с дополнительной информацией, сюда лучше вставлять номер столбца с регионом
     * @param checker      номер столбца, где пустое поле будет означать конец файла. Подойдет столб с непрерывной инфой
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
     *
     * @return возвращает массив, 0 элемент количество найденных, 1 элемент количество не найденных, 2 элемент количество нескольких найденных записей
     */
    public int[] dummySearch() throws IOException {
        System.out.println("Run");
        int[] ans = new int[3];
        if (!settingsWasEdited) {
            return new int[]{-1, -1, -1};
        }
        XSSFSheet sheet = getSheet();
        int from = (super.header ? 1 : 0);
        int rowSize = 0;
        String ss;
        for (int i = from; ; i++, rowSize++) {
            try {
                ss = sheet.getRow(i).getCell(checker).getStringCellValue();
            } catch (NullPointerException e) {
                break;
            }
        }
        int startFrom = 0;

        for (int i = from, cnt = startFrom; cnt < rowSize; i++, cnt++) {
            String street = getXCell(i, streetCol, sheet).toUpperCase();
            String house = getXCell(i, houseCol, sheet);
            String apartment = getXCell(i, apartmentCol, sheet);
            String complementaryInfo = getXCell(i, infoCol, sheet);

            String cadastralCheck = getXCell(i, getCadastrCol(), sheet);

            if (!cadastralCheck.equals("")) {
                continue;
            }


            List<Map<String, String>> responses;
            if (apartment.equals("-") || apartment.equals("")) {
                responses = connection.sendQuery(street, house, complementaryInfo);
            } else {
                responses = connection.sendQuery(street, house, apartment, complementaryInfo);
            }
            List <Map <String, String> > response2 = new ArrayList<>();
            for (Map response : responses) {
                if (!dontUsed.contains(response.get("cadastral_number"))) {
                    response2.add(response);
                }
            }
            responses = response2;
            if (responses.size() == 1) {
                Map<String, String> responseMap = responses.get(0);
                setCadastr(i, responseMap, sheet);
                ans[0]++;
            } else if (responses.size() > 0) {
                ArrayList<Map<String, String>> potentialAddress = new ArrayList<Map<String, String>>();
                ArrayList<Map<String, String>> zeroDifference = new ArrayList<Map<String, String>>();
                for (Map<String, String> response : responses) {
                    try {
                        BigDecimal responseArea = new BigDecimal(response.get("area")),
                                addressArea = new BigDecimal(getXCell(i, infoAreaCol, sheet)),
                                difference;

                        difference = responseArea.subtract(addressArea).abs();
                        if (difference.compareTo(new BigDecimal("0.0001")) <= 0) {
                            zeroDifference.add(response);
                        } else if (difference.compareTo(new BigDecimal("0.75")) < 0) {
                            potentialAddress.add(response);
                        }
                    } catch (Exception ignor) {
                    }
                }

                if (zeroDifference.size() == 1) {
                    potentialAddress = zeroDifference;
                } else if (zeroDifference.size() > 1) {
                    potentialAddress.addAll(zeroDifference);
                }

                if (potentialAddress.size() == 1) {
                    setCadastr(i, potentialAddress.get(0), sheet);
                    addAreaAddresses(i, responses, sheet); //Те, которые отобрали по площади
                    ans[0]++;
                } else {
//                    System.out.println(responses.size());
                    addFewAddresses(i, responses, sheet);
                    ans[2]++;
                }
            } else {
                ans[1]++;
            }
            System.out.print(String.format("\r%d%% (g - %d, b - %d, f - %d) id - %d | Proc: %d%%",
                    cnt * 100 / rowSize,
                    ans[0],
                    ans[1],
                    ans[2],
                    i,
                    ans[0] * 100 / i));
        }
        forFewRecords.saveFile(getFileName() + " Несколько записей.xlsx");
        forAreaRecords.saveFile(getFileName() + " Несколько записей, но нашли верную по площади.xlsx");
        status = ans;
        FileWriter writer = new FileWriter("src/inputFiles/" + getFileName() + ".txt", true);
        BufferedWriter bufferWriter = new BufferedWriter(writer);
        bufferWriter.write(Arrays.toString(ans) + " " + getFileName());
        bufferWriter.close();
        return ans;
    }

    private void addFewAddresses(int row, List<Map<String, String>> responses, XSSFSheet sheet) {
        forFewRecords.addRecords(sheet.getRow(row), 27, responses);
    }

    private void addAreaAddresses(int row, List<Map<String, String>> responses, XSSFSheet sheet) {
        forAreaRecords.addRecords(sheet.getRow(row), 27, responses);
    }

    private void setCadastr(int row, Map<String, String> responseMap, XSSFSheet sheet) {
        sheet.getRow(row).createCell(getCadastrCol(), CellType.STRING).setCellValue(responseMap.get("cadastral_number"));
        sheet.getRow(row).createCell(getAreaCol(), CellType.STRING).setCellValue(responseMap.get("area"));
        sheet.getRow(row).createCell(getNameCol(), CellType.STRING).setCellValue(responseMap.get("name"));

        foundAddresses.add(responseMap.get("cadastral_number"));

    }

    public void createCSV() {
        try (PrintWriter writer = new PrintWriter(new File("src/inputFiles/" + getFileName() + " IPK.csv"))) {

            StringBuilder sb = new StringBuilder();

            for (String i : foundAddresses) {
                sb.append(i).append("\n");
            }

            writer.write(sb.toString());

            System.out.println("CSV создан");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private String getXCell(int row, int col, XSSFSheet sheet) {
        try {
            return sheet.getRow(row).getCell(col).getStringCellValue();
        } catch (NullPointerException e) {
            return "";
        }
    }


}
