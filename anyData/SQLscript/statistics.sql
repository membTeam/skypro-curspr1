-- Статистика начислений
DROP table if exists buf;
CREATE temp TABLE buf (
    id INTEGER,
    yymm INTEGER,
    avg NUMERIC,
    maxSalr INTEGER,
    minSalr INTEGER,
    emplMax TEXT(50),
    emplMin TEXT(50),
    sumSal INTEGER
);

INSERT INTO buf(id, yymm) values(1,@yymm);
UPDATE buf
    set avg  = (select avg(salary) from Salaries WHERE yymm = @yymm),
        maxSalr = (select max(salary) from Salaries WHERE yymm = @yymm),
        minSalr = (select min(salary) from Salaries WHERE yymm = @yymm),
        emplMax  = (select avg(salary) from Salaries WHERE yymm = @yymm)
    where id > 0;

UPDATE buf
    set
        sumSal = (select sum(salary) from Salaries WHERE yymm = @yymm),
        emplMax = (select (e.fullName || ' ' || salary || ' руб') res
            from Salaries s, Emploees e
            WHERE s.emploeesId = e.id
                and yymm = @yymm
                and salary = (select max(salary) from Salaries s2 WHERE yymm = @yymm)),

        emplMin = (select (e.fullName || ' ' || salary || ' руб') res
        from Salaries s, Emploees e
        WHERE s.emploeesId = e.id
            and yymm = @yymm
            and salary = (select min(salary) from Salaries s2 WHERE yymm = @yymm))
where id > 0;