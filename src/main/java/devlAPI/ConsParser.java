package devlAPI;

import devlRecord.RecordResProc;

public class ConsParser {

    public static RecordResProc menedjConsoleComd(String strInput) {
        var resRunComd = new ConsRunComand(strInput);

        return resRunComd.runConsoleComd();
    }

}
