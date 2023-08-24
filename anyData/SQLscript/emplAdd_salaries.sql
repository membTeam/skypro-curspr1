-- Увеличение зарплаты
DROP table if exists buf;
CREATE temp table buf(
    Id integer,
    jobTitle text(50),
    salBase int,
    salEnd int
);

INSERT INTO buf(id, jobTitle, salBase)
select id, jobTitle, salary
	from Positions ;

UPDATE buf set salEnd = salBase * @pr WHERE id > 0;

UPDATE Positions
    set salary = (select cast(salEnd as integer) from buf WHERE id = Positions.id)
WHERE id > 0;