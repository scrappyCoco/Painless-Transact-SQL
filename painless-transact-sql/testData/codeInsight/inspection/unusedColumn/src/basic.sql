DECLARE
    @Products TABLE
              (
                  Id          INT IDENTITY PRIMARY KEY,
                  Name        VARCHAR(100) NOT NULL,
                  Color       VARCHAR(100) DEFAULT 'White',
                  Price       DECIMAL(20, 2),
                  Description AS Name + ' ' + CONVERT(VARCHAR(50), Price, 111)
              );

CREATE TABLE #MyTable (
                          Id INT PRIMARY KEY,
                          Description VARCHAR(MAX)
)