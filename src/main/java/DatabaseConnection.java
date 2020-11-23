import org.postgresql.util.PSQLException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseConnection {
    static String DB_URL = "jdbc:postgresql://";
    static String USER = "username";
    static String PASS = "password";
    private Connection connection;
    private Statement stmt;
    private String databaseName = "reimport_rtneo_refactor";
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
            connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);
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
    public void sendQuery(String street, String house, String apartment, String complementaryInfo) {
        String query = "select * from " + databaseName + " where street like '" + street + "%' and house like '" + house +
                "|%' and apartment like '" + apartment + "' and address_notes like '%" + complementaryInfo + "%'";
        String q;
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Обычный метод поиска для обычных домов, используем пока для разработки
     * @param complementaryInfo информация, лучше всего сюда передавать регион
     */
    public HashMap<String, String> sendQuery(String street, String house, String complementaryInfo) {
        System.out.println(1);
        String query = "select cadastral_number, assignation_code, area, name from " + databaseName + " where street like '" + street + "%' and house like '" + house +
                "|%' and apartment is null and address_notes like '%" + complementaryInfo + "%'";
        ResultSet rs;
        String [] neededFields = new String[]{"cadastral_number", "assignation_code", "area", "name"};
        System.out.println(2);
        try {
            rs = stmt.executeQuery(query);
            ArrayList <HashMap <String, String> > records = new ArrayList<HashMap<String, String>>();
            System.out.println(3);
            while (rs.next()) {
                HashMap <String, String> record = new HashMap<String, String>();
                for (String neededField : neededFields) {
                    record.put(neededField, rs.getString(neededField));
                }
                records.add(record);
            }
            System.out.println(4);
            if (records.size() == 1) {
                return records.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return null;

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
//            e.printStackTrace();
            return false;
        } catch (SQLException e) {
//            e.printStackTrace();
            return false;
        }
        return true;
    }

}
