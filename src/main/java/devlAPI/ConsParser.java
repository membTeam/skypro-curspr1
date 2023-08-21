package devlAPI;

import devlRecord.RecordResProc;

public class ConsParser {

    public static RecordResProc menedjConsoleComd(String strInput) {
        var resRunComd = new ConsRunComand(strInput);

        var err = resRunComd.getErr();
        if (resRunComd.isErr()){
            return RecordResProc.getResultErr(resRunComd.getErr());
        }

        return resRunComd.runConsoleComd();
    }

}
