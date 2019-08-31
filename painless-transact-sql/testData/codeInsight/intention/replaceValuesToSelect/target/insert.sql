DECLARE @T TABLE
           (
               A INT,
               B BIT,
               C CHAR
           );

INSERT INTO @T (A, B, C)
SELECT A = 1,
       B = 1,
       C = 'C'
UNION ALL
SELECT A = 2,
       B = 0,
       C = 'A';