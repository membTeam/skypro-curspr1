/*
 create Emploee
 used in APIsqlite.InitialData
 */

CREATE TABLE Emploees (
                          id INTEGER NOT NULL,
                          fullName TEXT(200) NOT NULL,
                          departmentsId INTEGER DEFAULT 1 NOT NULL,
                          salary INTEGER DEFAULT 0,
                          CONSTRAINT Emploees_PK PRIMARY KEY (id),
                          CONSTRAINT Emploees_FK FOREIGN KEY (departmentsId) REFERENCES Departments(Id)
);
