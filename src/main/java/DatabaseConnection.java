import org.postgresql.util.PSQLException;

import java.sql.*;
import java.util.*;

public class DatabaseConnection {
    static String DB_URL = "jdbc:postgresql://";
    static String USER = "username";
    static String PASS = "password";
    private Connection connection;
    private Statement stmt;
    private String databaseName = "reimport_rtneo_refactor";

    /**
     * @param name     название базы
     * @param login    логин по которому подключаемся
     * @param password пароль
     * @param addr     адрес по которому можно подключиться к бд
     * @param port     порт бд, по умолчанию для остгреса 5432
     */
    DatabaseConnection(String name, String login, String password, String addr, String port) {
        USER = login;
        PASS = password;
        DB_URL = DB_URL + addr + ":" + port + "/" + name;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Произошла ошибка");
            e.printStackTrace();
            return;
        }

        connection = null;

        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = connection.createStatement();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка");
            e.printStackTrace();
        }

        if (connection != null) {
            System.out.println("Подключение прошло успешно");
        } else {
            System.out.println("Ошибка подключения");
        }

    }

    /**
     * Обычный метод поиска для квартир, используем пока для разработки
     *
     * @param complementaryInfo информация, лучше всего сюда передавать регион
     */
    public List<Map<String, String>> sendQuery(String street, String house, String apartment, String complementaryInfo) {

        String queryF = queryFormer(street, house, apartment, complementaryInfo);
        return queryHandler(queryF);
    }

    /**
     * Обычный метод поиска для обычных домов, используем пока для разработки
     *
     * @param complementaryInfo информация, лучше всего сюда передавать регион
     * @return Вернет Map со всеми значениями
     */
    public List<Map<String, String>> sendQuery(String street, String house, String complementaryInfo) {

        String queryF = queryFormer(street, house, "", complementaryInfo);
        return queryHandler(queryF);

    }

    /**
     * @param query - Запрос, который нужно обработать
     * @return Возвращает лист с ответами
     */
    private List<Map<String, String>> queryHandler(String query) {
        String[] neededFields = new String[]{"cadastral_number", "assignation_code", "area", "name"};
        try {
            ResultSet rs = stmt.executeQuery(query);
            List<Map<String, String>> records = new ArrayList<Map<String, String>>();
            while (rs.next()) {
                Map<String, String> record = new HashMap<String, String>();
                for (String neededField : neededFields) {
                    record.put(neededField, rs.getString(neededField));
                }
                records.add(record);
            }
            return records;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Collections.EMPTY_LIST;
    }

    private String queryFormer(String street, String house, String apartment, String complementaryInfo) {
        String columnQ = "cadastral_number, assignation_code, area, name";
        String streetQ = String.format("(street like '%s' or street like '%s|%%')", street, street);
        String houseQ = String.format("(house like '%s' or house like '%s|%%')", house, house);
        String apartmentQ = String.format("apartment like '%s'", apartment);
        if (apartment.equals("")) {
            apartmentQ = "1 > 0";
        }
        String addressNotesQ = String.format("address_notes like '%%%s%%'", complementaryInfo);
        String[] literalResponse = letterInHouse(house);
        if (!literalResponse[0].equals("EMPTY")) {
            houseQ = String.format("(house like '%s%s%%' or house like '%s_%s%%' or house like '%s%s%%' or house like '%s_%s%%')",
                    literalResponse[0], literalResponse[1], literalResponse[0], literalResponse[1],
                    literalResponse[0], literalResponse[1].toUpperCase(), literalResponse[0], literalResponse[1].toUpperCase());
        }

        String condition = String.format("%s and %s and %s and %s", streetQ, houseQ, apartmentQ, addressNotesQ);

        return String.format("select %s from %s where %s", columnQ, databaseName, condition);
    }

    /**
     * Функция проверяет есть ли в названии дома литера и цифра
     *
     * @param house дом
     * @return {EMPTY, EMPTY} если дом только из цифр, иначе литеру дома и номер
     */
    private String[] letterInHouse(String house) {
        String[] ans = new String[]{"", ""};
        for (int i = 0; i < house.length(); i++) {
            if (Character.isLetter(house.charAt(i))) {
                ans[1] += house.charAt(i);
            } else if (Character.isDigit(house.charAt(i))) {
                ans[0] += house.charAt(i);
            }
        }
        if (ans[1].equals("")) {
            return new String[]{"EMPTY", "EMPTY"};
        } else {
            return ans;
        }
    }


    /**
     * Метод который создает заджойненную таблицу, с которой удобнее всего работать
     *
     * @return true - если таблица была создана, иначе false
     */
    public boolean createSuperTable() {
        String query = "create table reimport_rtneo_refactor as select a.subject_id, a.region_id, a.settlement_id, a.street, a.house, a.apartment, a.cadastral_number, a.address_notes, b.assignation_code, b.area, b.name, b.response_json\n" +
                "from reimport_rosreestr_search as a left join reimport_rosreestr_object_info_full as b on a.cadastral_number = b.cadastral_number";
        try {
            System.out.println("Создание таблицы");
            boolean rs = stmt.execute(query);
            System.out.println(rs ? "Таблица создана" : "Таблица скорее всего есть");
        } catch (PSQLException e) {
            System.out.println("Скорее всего таблица уже была создана или произошла какая-то ошибка");
            return false;
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

}
