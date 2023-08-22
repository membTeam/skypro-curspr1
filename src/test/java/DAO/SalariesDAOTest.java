package DAO;

import devlAPI.APIerror;
import devlAPI.APIyymm;
import devlRecord.RecordResProc;
import models.SalariesDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static devlAPI.APIyymm.*;
import static devlAPI.APIprintService.println;


public class SalariesDAOTest {
    @Test
    public void verfExistsData_with_notData_test() {
        println("testing verfExistsData_with_notData_test");

        var yymm = getCurrentYYMM();
        var yymmNext = incYYMM(yymm);

        var resExists = SalariesDAO.verfExistsData(yymmNext);

        Assertions.assertFalse(APIerror.getErr());
        Assertions.assertFalse(resExists);
    }

    @Test
    public void verfExistsData_onlyVerfExists_test() {
        println("testing verfExistsData_onlyVerfExists_test");

        var existsData = SalariesDAO.verfExistsData();

        Assertions.assertFalse(APIerror.getErr());
        Assertions.assertInstanceOf(Boolean.class, existsData);
    }

    @Test
    public void getMaxId_test() {
        println("getMaxId_test");

        var resId = SalariesDAO.getMaxId();

        Assertions.assertFalse(APIerror.getErr());
        Assertions.assertInstanceOf(Integer.class, resId);
    }

    @Test
    public void addSalaries_test() {
        println("addSalaries_test");

        var existData = SalariesDAO.verfExistsData();
        int yymm;
        int month = LocalDate.now().getMonth().getValue();
        int yymmCurr = getCurrentYYMM();

        if (!existData) {
            var lDate = LocalDate.of(2023, month, 1)
                    .minusMonths(6);
            yymm = APIyymm.getYYMMfromDate(lDate);
        } else {
            yymm = APIyymm.getLastYYMM();
            yymm = APIyymm.incYYMM(yymm);
        }

        var addSalaries = SalariesDAO.addSalaries(yymm);

        // assert
        if (yymm <= yymmCurr){
            Assertions.assertTrue(addSalaries.res());
            Assertions.assertInstanceOf(RecordResProc.class, addSalaries);

            var existYYMM = SalariesDAO.verfExistsData(yymm);
            Assertions.assertTrue(existYYMM);
        } else {
            Assertions.assertFalse(addSalaries.res());
            Assertions.assertInstanceOf(RecordResProc.class, addSalaries);

        }

    }

    @Test
    public void addSalaries_add_existsData_test() {
        println("addSalaries_add_existsData_test");

        var existYYMM = SalariesDAO.verfExistsData();
        if (existYYMM) {
            var lastYYMM = getLastYYMM();
            var addSalaries = SalariesDAO.addSalaries(lastYYMM);

            // assert
            Assertions.assertFalse(addSalaries.res());
        } else {
            println("Нет данных для тестирования");
        }

    }

    @Test
    public void addSalaries_moreThen_currentDate_test() {
        println("addSalaries_moreThen_currentDate_test");

        var currYYMM = getCurrentYYMM();
        currYYMM = incYYMM(currYYMM);

        var addSalaries = SalariesDAO.addSalaries(currYYMM);

        // assert
        Assertions.assertFalse(addSalaries.res());

    }


}
