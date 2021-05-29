CREATE TABLE T1
(
    ID INT
);
CREATE TABLE T2
(
    ID INT
);
CREATE TABLE T3
(
    ID INT
);

CREATE TABLE #T
(
    ID INT
);
DECLARE @T TABLE
           (
               ID INT
           );


DELETE   T1
OUTPUT deleted.ID
INTO T3  (ID)
FROM T1
         INNER JOIN T2 ON T1.ID = T2.ID;