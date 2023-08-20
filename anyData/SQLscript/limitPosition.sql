DROP table if exists buf;

CREATE  temp table buf(
  id interger,
  posUsed integer,
  posLimit integer
);

INSERT into buf(id) values(%d);

UPDATE buf set posLimit = (SELECT numLimit
		from Positions p2 WHERE id = (select id from buf))
	WHERE id > 0;


UPDATE buf set posUsed = (select COUNT(*) num
		from Emploees e WHERE positionId = (select id from buf) )
	where id > 0;

SELECT id, posUsed, posLimit from buf;
