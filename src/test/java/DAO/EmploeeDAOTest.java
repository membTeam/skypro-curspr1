package DAO;

import devlAPI.APIdevl;
import devlAPI.APIerror;
import devlRecord.RecordResProc;
import models.DAOcomnAPI;
import models.Emploee;
import models.EmploeeDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static devlAPI.APIprintService.println;

public class EmploeeDAOTest {

    @Test
    public void findEntityById_withExistsItem_test(){
        println("testing EmploeeDAO.findEntityById exist item");

        var emploeeDAO = new  EmploeeDAO();
        var id = 2;
        var emploee = emploeeDAO.findEntityById(id);

        // assert
        Assertions.assertFalse(APIerror.getErr());
        Assertions.assertNotNull(emploee);

    }

    @Test
    public void findEntityById_withNotExistsItem_test(){
        println("testing EmploeeDAO.findEntityById not exists item");

        var emploeeDAO = new  EmploeeDAO();
        var id = 200;
        var emploee = emploeeDAO.findEntityById(id);

        // assert
        Assertions.assertFalse(APIerror.getErr());
        Assertions.assertNull(emploee);
    }

    @Test
    public void verfExistsEmploee_withExistsItem_test(){
        println("testing EmploeeDAO.verfExistsEmploee exist item");

        var id = 2;
        var emploeeDAO = new EmploeeDAO();
        var res = emploeeDAO.verfExistsEmploee(id);
        var bRes = APIdevl.getBooleanFromStr(res.strData());

        // assert
        Assertions.assertFalse(APIerror.getErr());
        Assertions.assertTrue(res.res()
                && !APIerror.getErr()
                && bRes);
    }

    @Test
    public void verfExistsEmploee_withNotExistsItem_test(){
        println("testing EmploeeDAO.verfExistsEmploee not item");

        APIerror.resetErr();

        var id = 200;
        var emploeeDAO = new EmploeeDAO();
        var res = emploeeDAO.verfExistsEmploee(id);
        var bRes = APIdevl.getBooleanFromStr(res.strData());

        // assert
        Assertions.assertTrue(res.res());
        Assertions.assertFalse(APIerror.getErr());
        Assertions.assertFalse(bRes);
    }

    @Test
    public void printItemEmploee_withExistsItem_test() {
        println("testing EmploeeDAO.printItemEmploee");
        EmploeeDAO.printItemEmploee(2);

        // assert
        Assertions.assertFalse(APIerror.getErr());
    }

    @Test
    public void printItemEmploee_withNotItem_test() {
        println("testing EmploeeDAO.printItemEmploee");
        EmploeeDAO.printItemEmploee(200);

        // assert
        Assertions.assertFalse(APIerror.getErr());
    }

    @Test
    public void getAllEmploee_test() {
        println("testing EmploeeDAO.getAllEmploee");

        var res = EmploeeDAO.getAllEmploee();

        // assert
        Assertions.assertTrue(res.res());
    }

    @Test
    public void getMaxId_test() {
        println("testing EmploeeDAO.getMaxId");

        // assert
        Assertions.assertTrue(EmploeeDAO.getMaxId() > 0);
    }

    @Test
    public void printAllEmploee_test() {
        println("testing EmploeeDAO.printAllEmploee");

        EmploeeDAO.printAllEmploee();

        // assert
        Assertions.assertFalse(APIerror.getErr());
    }

    @Test
    public void updata_withExists_test(){
        var sql = """
                   select ifnull(min(id),-1) from Emploees 
                        where idUse > 0 and fullName != 'Test Data';
                  """;

        var resSql = DAOcomnAPI.getDataFromSQLscript(sql);
        var id = Integer.parseInt(resSql.strData());

        if (id < 0){
            println("Нет данных для теста update");
            return;
        }

        var emploeeDAO = new EmploeeDAO();
        var fullName = "Test Data";
        var depatment = 3;
        var position = 3;

        var emploee = new Emploee(id, fullName,depatment, position);
        var resUpdate = emploeeDAO.update(emploee, "positionId\nfullName");

        // assert
        Assertions.assertNotNull(resUpdate);
        Assertions.assertEquals(position, resUpdate.getPositionId());
        Assertions.assertEquals(fullName, resUpdate.getFullName());
    }

    @Test
    public void updata_updateOnefield_test(){
        var sql = """
                   select ifnull(min(id),-1) from Emploees 
                        where idUse > 0 and fullName != 'Test Data';
                  """;

        var resSql = DAOcomnAPI.getDataFromSQLscript(sql);
        var id = Integer.parseInt(resSql.strData());

        if (id < 0){
            println("Нет данных для теста update");
            return;
        }

        var emploeeDAO = new EmploeeDAO();
        var fullName = "Test Data";
        var depatment = 3;
        var position = 5;

        var emploee = new Emploee(id, fullName,depatment, position);
        var resUpdate = emploeeDAO.update(emploee, "positionId");

        // assert
        Assertions.assertNotNull(resUpdate);
        Assertions.assertEquals(position, resUpdate.getPositionId());
    }

    @Test
    public void updata_withErrFields_test(){
        var sql = """
                   select ifnull(min(id),-1) from Emploees 
                        where idUse > 0 and fullName != 'Test Data';
                  """;

        var resSql = DAOcomnAPI.getDataFromSQLscript(sql);
        var id = Integer.parseInt(resSql.strData());

        if (id < 0){
            println("Нет данных для теста update");
            return;
        }

        var emploeeDAO = new EmploeeDAO();
        var fullName = "Test Data";
        var depatment = 3;
        var position = 3;

        var emploee = new Emploee(id, fullName,depatment, position);
        // Создание ошибки в параметре arrFields
        var resUpdate = emploeeDAO.update(emploee, "positionId\nfullName_");

        // assert
        Assertions.assertTrue(APIerror.getErr());
        Assertions.assertNull(resUpdate);
    }

    @Test
    public void updata_withNotEmploee_test(){

        var id = 100;
        var emploeeDAO = new EmploeeDAO();
        var fullName = "Test Data";
        var depatment = 3;
        var position = 3;

        var emploee = new Emploee(id, fullName,depatment, position);
        // Создание ошибки в параметре arrFields
        var resUpdate = emploeeDAO.update(emploee, "positionId\nfullName");

        // assert
        Assertions.assertTrue(APIerror.getErr());
        Assertions.assertNull(resUpdate);
    }

    @Test
    public void delete_withIdUseIs_0_test(){
        var sql = """
                   select ifnull(min(id),-1) from Emploees 
                        where idUse = 0;
                  """;
        var resSql = DAOcomnAPI.getDataFromSQLscript(sql);
        var id = Integer.parseInt(resSql.strData());

        if (id < 0){
            println("Нет данных для теста update");
            return;
        }

        var emploeeDAO = new EmploeeDAO();
        var resDel = emploeeDAO.delete(id);

        // assert
        Assertions.assertFalse(resDel.res());
    }

    @Test
    public void delete_withIdUseIs_1_test(){
        var sql = """
                   select ifnull(min(id),-1) from Emploees \
                        where idUse = 1;
                  """;
        var resSql = DAOcomnAPI.getDataFromSQLscript(sql);
        var id = Integer.parseInt(resSql.strData());

        if (id < 0){
            println("Нет данных для теста update");
            return;
        }

        var emploeeDAO = new EmploeeDAO();
        var resDel = emploeeDAO.delete(id);

        // assert
        Assertions.assertTrue(resDel.res());
    }

    @Test
    public void create_test(){
        var id = EmploeeDAO.getMaxId() + 1;
        var fullName = "New Emploee";
        var positionId = 3;
        var department = 4;

        var emploeeDAO = new EmploeeDAO();
        var emploee = new Emploee(id,fullName,department,positionId);

        var resCreate = emploeeDAO.create(emploee);

        // assert
        Assertions.assertInstanceOf(RecordResProc.class, resCreate);
        Assertions.assertTrue(resCreate.res());
    }

}
