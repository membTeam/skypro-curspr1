package APIsqlite;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import static devlAPI.APIprintService.println;
import static java.lang.Integer.parseInt;

public class InitialData {
    public record recEmploee(int departId, int salary, String fullName){
        @Override
        public String toString(){
            return "departId:" + this.departId() +
                    " salary:" + this.salary() +
                    " fullName:" + this.fullName();
        }
    };
    public record recDepartment(int id, String info){
        @Override
        public String toString(){
            return "departId:" + this.id() +
                    " fullName:" + this.info();
        }
    }

    public static List<recDepartment> readDepartment() throws FileNotFoundException {
        List<recDepartment> listDepart = new ArrayList<>();

        var strPath = "anyData/initialData/departments.txt";
        var strPattern = "(\\d{1,}) (.*)";
        var pattern = Pattern.compile(strPattern);

        try (var scaner = new Scanner(new File(strPath))) {
            while (scaner.hasNextLine()) {
                var sRead = scaner.nextLine();
                if (sRead.length() == 0) break;

                var matcher = pattern.matcher(sRead);
                if (matcher.find()) {
                    var bufRecDepartment = new recDepartment(
                            parseInt(matcher.group(1)),
                            matcher.group(2));

                    listDepart.add(bufRecDepartment);
                } else {
                    println("NO MATCH" + sRead);
                }
            }

            return listDepart;
        }
    }

    public static List<recEmploee> readEmploeeFromFile() throws FileNotFoundException {
        List<recEmploee> listEmpl = new ArrayList<>();

        var strPath = "anyData/initialData/emploees.txt";
        var strPattern = "(\\d{1,}) (\\d+) (.*)";
        var pattern = Pattern.compile(strPattern);

        try(var scaner = new Scanner(new File(strPath))){
            while (scaner.hasNextLine()){
                var sRead = scaner.nextLine();
                if (sRead.length() == 0)
                    break;

                var matcher = pattern.matcher(sRead);
                if (matcher.find()) {
                    var bufRecEmploee = new recEmploee(
                            parseInt(matcher.group(1)),
                            parseInt(matcher.group(2)),
                            matcher.group(3));

                    listEmpl.add(bufRecEmploee);
                } else {
                    println("NO MATCH" + sRead);
                }
            }
        }

        return listEmpl;
    }

    public static String scriptSQLEmploee(){
        var sbSQLEmploee = new StringBuffer();
        try{
            var lsEmploee = readEmploeeFromFile();

            var keyId = 1;
            boolean firstStr = true;

            sbSQLEmploee.append("INSERT INTO Emploees (id, fullName, departmentsId, salary) VALUES");
            for (var item: lsEmploee) {
                if (firstStr){
                    firstStr = !firstStr;
                } else {
                    sbSQLEmploee.append(", ");
                }

                sbSQLEmploee.append(String.format("(%d, '%s', %d, %d)",
                        keyId++, item.fullName(), item.departId(), item.salary()));
            }
            sbSQLEmploee.append(";");



        }
        catch (FileNotFoundException ex){
            println("err read data from file:\n" + ex.getMessage());
        }

        return sbSQLEmploee.toString();
    }

    public static void initialEmploee(){
        //var resProc = 0;
        var sql = "select COUNT(*) exs from Emploees e;";

        try (Connection conn = APIsqlite.Connect.getConnect()){
            var statement = conn.createStatement();
            var resQuery = statement.executeQuery(sql);
            while (resQuery.next()){
                var resExists = resQuery.getInt("exs");

                if (resExists > 0){
                    println("База данных содержит данные\n" +
                            "Заполнение отменено");
                    return;
                }
            }

            var strSQL = scriptSQLEmploee();
            var stateMent = conn.createStatement();
            stateMent.executeUpdate(strSQL);

            println("Выполнена начальная загрузка справочника сотрудников");

        } catch (Exception ex){
            println("err:\n" + ex.getMessage());
        }

    }

}
