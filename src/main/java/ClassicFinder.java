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
        //"jdbc:postgresql://127.0.0.1:5432/vertex"
        try {
            DatabaseConnection connection = new DatabaseConnection(databaseName, login, password, addr, port);
        } catch (SQLException e) {
            System.out.println("Не удалось подключиться к базе данных");
        }
    }



}
