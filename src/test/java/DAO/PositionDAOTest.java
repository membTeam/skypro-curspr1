package DAO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import devlAPI.APIerror;
import models.PositionsDAO;

import static devlAPI.APIprintService.println;

public class PositionDAOTest {
    @Test
    public void isExistsPositionInDepartment_depatment_2_test(){
        println("isExistsPositionInDepartment_depatment_2_test");

        var positionId = 3;     // начОтдела только одна должность
        var departmentId = 2;   // Отдел Администраторы
        var resExists = PositionsDAO.isExistsPositionInDepartment(departmentId, positionId);

        Assertions.assertFalse(APIerror.getErr());
        Assertions.assertInstanceOf(Boolean.class, resExists);
        Assertions.assertTrue(resExists);
    }

    @Test
    public void isExistsPositionInDepartment_depatment_4_test(){
        println("isExistsPositionInDepartment_depatment_4_test");

        var positionId = 7;     // Гл. бухгалтер только одна должность
        var departmentId = 4;   // Бухгалтерия
        var resExists = PositionsDAO.isExistsPositionInDepartment(departmentId, positionId);

        Assertions.assertFalse(APIerror.getErr());
        Assertions.assertInstanceOf(Boolean.class, resExists);
        Assertions.assertTrue(resExists);
    }
}
