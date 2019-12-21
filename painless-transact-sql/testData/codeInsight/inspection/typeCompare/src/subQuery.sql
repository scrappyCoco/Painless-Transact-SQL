DECLARE @T TABLE
           (
               Id       INT,
               BirthDay DATE
           );

SELECT *
FROM (
         SELECT Id, BirthDay
         FROM @T
     ) AS T1
WHERE Id = BirthDay;