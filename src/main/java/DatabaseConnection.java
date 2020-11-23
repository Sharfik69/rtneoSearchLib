import java.sql.*;

public class DatabaseConnection {
    static String DB_URL = "jdbc:postgresql://";
    static String USER = "username";
    static String PASS = "password";
    private Connection connection;

    DatabaseConnection (String name, String login, String password, String addr, String port) throws SQLException {
        USER = login;
        PASS = password;
        DB_URL = DB_URL + addr + ":" + port + "/" + name;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
            return;
        }

        connection = null;

        try {
            connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);

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

    public void sendQuery(String street, String house, String apartment, String complementaryInfo) {
        String query = "select * from reimport_rosreestr_search where street like '" + street + "%' and house like '" + house +
                "|%' and apartment like '" + apartment + "' and address_notes like '%" + complementaryInfo + "%' and object_type in ('flat', 'building')";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            String k = "123";
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void sendQuery(String street, String house, String complementaryInfo) {
        String query = "select * from reimport_rosreestr_search where street like '" + street + "%' and house like '" + house +
                "|%' and apartment is null and address_notes like '%" + complementaryInfo + "%' and object_type in ('flat', 'building')";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            String k = "123";
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
