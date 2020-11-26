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
     *
     * @param name название базы
     * @param login логин по которому подключаемся
     * @param password пароль
     * @param addr адрес по которому можно подключиться к бд
     * @param port порт бд, по умолчанию для остгреса 5432
     */
    DatabaseConnection (String name, String login, String password, String addr, String port) {
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
     * @param complementaryInfo информация, лучше всего сюда передавать регион
     */
    public List <Map <String, String> >  sendQuery(String street, String house, String apartment, String complementaryInfo) {

        String query = "select * from " + databaseName
                + " where street like '" + streetChecker(street)
                + "' and house like '" + houseChecker(house)
                + "' and apartment like '" + apartment
                + "' and address_notes like '%" + complementaryInfo + "%'";

        return queryHandler(query);
    }

    /**
     * Обычный метод поиска для обычных домов, используем пока для разработки
     * @param complementaryInfo информация, лучше всего сюда передавать регион
     * @return Вернет Map со всеми значениями
     */
    public List<Map<String, String>> sendQuery(String street, String house, String complementaryInfo) {
        String query = "select cadastral_number, assignation_code, area, name from " + databaseName
                + " where street like '" + streetChecker(street)
                + "' and house like '" + houseChecker(house)
                + "' and apartment is null and address_notes like '%" + complementaryInfo + "%'";

        return queryHandler(query);
    }

    /**
     *
     * @param query - Запрос, который нужно обработать
     * @return Возвращает лист с ответами
     */
    private List <Map <String, String> > queryHandler(String query) {
        String [] neededFields = new String[]{"cadastral_number", "assignation_code", "area", "name"};
        try {
            ResultSet rs = stmt.executeQuery(query);
            List <Map <String, String> > records = new ArrayList<Map<String, String>>();
            while (rs.next()) {
                Map <String, String> record = new HashMap<String, String>();
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



    //TODO: Проверка дома на наличие разделителей
    //TODO: Проверка улицы на наличие цифр

    private String streetChecker(String street) {
        return street + "%";
    }

    private String houseChecker(String house) {
        return house + "|%";
    }

    /**
     * Метод который создает заджойненную таблицу, с которой удобнее всего работать
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
