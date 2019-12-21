DECLARE @Products TABLE
                  (
                      Id          INT IDENTITY PRIMARY KEY,
                      Name        VARCHAR(100) NOT NULL,
                      IsAvailable BIT          NOT NULL,
                      Color       VARCHAR(100) DEFAULT 'White',
                      Price       DECIMAL(20, 2),
                      Description AS Name + ' ' + CONVERT(VARCHAR(50), Price, 111)
                  );

MERGE @Products AS Target
USING (SELECT Price = 123, Id = 1) AS Source
ON Source.Id = Target.Id
WHEN NOT MATCHED BY TARGET THEN INSERT  (Price) VALUES (Price)
    OUTPUT inserted.Price INTO @Products (Price);