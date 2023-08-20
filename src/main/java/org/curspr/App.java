package org.curspr;

import java.util.Scanner;

import devlAPI.APIerror;
import devlAPI.ConsParser;
import models.DAObaseConsComd;

import static devlAPI.APIprintService.*;

public class App {
    private final static Scanner scanner;
    // --------------------------------------------

    static {
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        println("Курсовой проект 1");

        println("Список допустимых команд: list comd");
        println("Выход из консольного режима quit");

        try (scanner) {
            while (true) {

                print("введите команду: ");
                var strInput = scanner.nextLine();

                if (strInput.isEmpty()){
                    println("Не корректный ввод команды");
                    break;
                }

                if (strInput.equals("quit")) {
                    break;
                }

                // TODO: встраивание общего общего обработчика
                var resConsComd = ConsParser.menedjConsoleComd(strInput);

                if (!resConsComd.res()) {
                    println(resConsComd.mes());
                    println("");
                    continue;
                }

                if (resConsComd.data() != null
                            && resConsComd.data() instanceof DAObaseConsComd) {

                    var daoConsComd = (DAObaseConsComd)resConsComd.data();

                    strInput = "";
                    String prMes;
                    while ( !(prMes = daoConsComd.nextStr(strInput)).isEmpty() ){
                        print(prMes);
                        strInput = scanner.nextLine();
                    }

                    // Вывод результата
                    if (!APIerror.getErr()){
                        var resSave = daoConsComd.saveModel();
                        if (resSave.res()){
                            if (resSave.data() != null){
                                println(resSave.data().toString());
                            } else {
                                println(resSave.mes());
                            }
                        } else {
                            println(resSave.mes());
                        }
                    } else {
                        println(APIerror.getMes());
                    }
                }

                println("");
            }

        } catch (Exception e) {
            println(e.getMessage());
        }

    }
}
