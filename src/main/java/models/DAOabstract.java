package models;

import devlRecord.RecordResProc;


// TODO: убрать R extends Number
public abstract class DAOabstract <T>  {

    abstract T findEntityById(int id);

    abstract RecordResProc delete(int id);

    abstract RecordResProc create(T entity);

    abstract T update(T entity, String arrFields);

}
