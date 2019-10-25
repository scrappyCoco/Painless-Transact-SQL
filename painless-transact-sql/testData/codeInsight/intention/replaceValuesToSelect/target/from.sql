SELECT *
FROM (
         SELECT A    = 1,
                [BY] = 2,
                C    = 3
         UNION ALL
         SELECT A    = 4,
                [BY] = 5,
                C    = 6
     ) AS Data(A, B, C)