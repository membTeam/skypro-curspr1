package org.curspr;

import DevlInterface.IRunComd;
import devlAPI.ConsRunComand;
import models.DAObaseConsComand;

import java.util.Scanner;

import static devlAPI.APIprintService.print;
import static devlAPI.APIprintService.println;

public class App {
    private final static Scanner scanner;
    // --------------------------------------------

    static {
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        println("Курсовой проект 1");

        println("Список допустимых команд: print comands");
        println("Выход из консольного режима quit");

        try (scanner) {
            while (true) {

                print("введите команду: ");
                var strInput = scanner.nextLine();

                if (strInput.isEmpty()) {
                    println("Не корректный ввод команды");
                    continue;
                }

                if (strInput.equals("quit")) {
                    break;
                }

                var consComd = new ConsRunComand(strInput);

                var resConsComd = consComd.runConsoleComd();
                if (!resConsComd.res()) {
                    println(resConsComd.mes());
                    println("");
                    continue;
                }

                if (resConsComd.data() != null) {
                    if (resConsComd.data() instanceof IRunComd) {
                        ((IRunComd) resConsComd.data()).apply();

                    } else if (resConsComd.data() instanceof DAObaseConsComand) {

                        var resDaoConsComd = (DAObaseConsComand) resConsComd.data();

                        strInput = "";
                        String prMes;
                        while (!(prMes = resDaoConsComd.nextStr(strInput)).isEmpty()) {
                            print(prMes);
                            strInput = scanner.nextLine();
                        }

                        // Вывод результата
                        var resSave = resDaoConsComd.saveModel();
                        if (resSave.res()) {
                            if (resSave.data() != null) {
                                println(resSave.data().toString());
                            } else {
                                println(resSave.mes());
                            }

                        } else {
                            println(resSave.mes());
                        }
                    }
                }

                println("");
            }

        } catch (Exception e) {
            println(e.getMessage());
        }

    }
}
