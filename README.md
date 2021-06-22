# Inspections
## Code Style
### Using CASE instead of CHOOSE and vice-versa
This inspection allow to simplify CASE conditions by using CHOOSE function instead.

Before:
```sql
DECLARE @a INT = 1;
SELECT CASE @a
WHEN 1 THEN 'A'
WHEN 2 THEN 'B'
WHEN 3 THEN 'C'
END
```
After:
```sql
DECLARE @a INT = 1;
SELECT CHOOSE(@a, 'A', 'B', 'C')
```

### Column alias definition
This inspection allow to replace ANSI 'AS' to non-ANSI '='. This form of column alias definition is more pretty. It's something like interface and implemenetation in OOP.

Before:
```sql
SELECT
 ROW_NUMBER OVER (PARTITION BY A ORDER BY B) AS ABC,
 COUNT(*) OVER (PARTITION BY C) AS DEF
FROM MyTable;
```
After:
```sql
SELECT
 ABC = ROW_NUMBER OVER (PARTITION BY A ORDER BY B),
 DEF = COUNT(*) OVER (PARTITION BY C)
FROM MyTable;
```

### Semicolon at the end of statement
This inspection allow to append semicolon at the end of each statement implicitly.

Before:
```sql
SELECT 1
```

After:
```sql
SELECT 1;
```

### Redundant qualifier check
![MsRedundantQualifierInspection](https://raw.githubusercontent.com/scrappyCoco/Painless-Transact-SQL/master/screenshots/MsRedundantQualifierInspection.png)

## DDL
### READONLY keyword check
This inspection warn when keyword READONLY was missing.
![MsReadonlyParameterInspection](https://raw.githubusercontent.com/scrappyCoco/Painless-Transact-SQL/master/screenshots/MsReadonlyParameterInspection.png)

## DML
### READONLY modification check
This inspection check attempt to insert into readonly table variable.
![MsReadonlyModificationInspection](https://raw.githubusercontent.com/scrappyCoco/Painless-Transact-SQL/master/screenshots/MsReadonlyModificationInspection.png)

### Check for semicolon before CTE (Common Table Expression)
This inspection check for existence semicolon before CTE.
![MsSemicolonCteInspection](https://raw.githubusercontent.com/scrappyCoco/Painless-Transact-SQL/master/screenshots/MsSemicolonCteInspection.png)

### Redundant DISTINCT keyword in set operators
DISTINCT is redundant in set operators: UNION, INTERSECT, EXCEPT.
![MsRedundantDistinctInSetOperatorsInspection](https://raw.githubusercontent.com/scrappyCoco/Painless-Transact-SQL/master/screenshots/MsRedundantDistinctInSetOperatorsInspection.png)

### Check for the CURSOR definition
![MsCursorInspection](https://raw.githubusercontent.com/scrappyCoco/Painless-Transact-SQL/master/screenshots/MsCursorInsepction.png)

### Check for type compatibility
![MsTypeCompatibilityInspection](https://raw.githubusercontent.com/scrappyCoco/Painless-Transact-SQL/master/screenshots/TypeCompatibility.png)

### Unused columns
![MsUnusedColumnInspection](https://raw.githubusercontent.com/scrappyCoco/Painless-Transact-SQL/master/screenshots/UnusedColumnInTempTable.png)

### Missing columns
![MsMissingColumnInspection](https://raw.githubusercontent.com/scrappyCoco/Painless-Transact-SQL/master/screenshots/MissingColumn.png)

### Check for implicit column list in DML
In cases when columns list is not specified explicit some time ago can occurred problems when any columns will be added or deleted.
```sql
CREATE TABLE T (
    Id INT,
    Name VARCHAR(100)
);
GO

-- Before
INSERT INTO T
VALUES (1, 'Artem');

-- After
INSERT INTO T (Id, Name)
VALUES (1, 'Artem');
```

## String functions
### SUBSTRING function check
This inspection offer to replace SUBSTRING to LEFT.

Before:
```sql
SELECT SUBSTRING('ABCDEF', 1, 2);
```

After:
```sql
SELECT LEFT('ABCDEF', 2);
```

### TRIM function check
This inspection offer to replace the sequence LTRIM/RTRIM to TRIM.

Before:
```sql
SELECT LTRIM(RTRIM(' ABCDEF '));
```

After:
```sql
SELECT TRIM(' ABCDEF ');
```

### Prefer implicitly length in VARCHAR
If the length of VARCHAR is not specified in CONVERT/CAST it interpreted as VARCHAR(30). The best way is to specify implicitly the length of VARCHAR.
Before:
```sql
SELECT CONVERT(VARCHAR, NEWID());
```
After:
```sql
SELECT CONVERT(VARCHAR(30), NEWID());
```

### Replace string to REPLICATE
```sql
-- Before
SELECT '11111111'

-- After
SELECT REPLICATE('1', 8)
```

# Intentions
## Replace LEFT to SUBSTRING
Before:
```sql
SELECT LEFT('ABCDEF', 2);
```

After:
```sql
SELECT SUBSTRING('ABCDEF', 1, 2);
```

## Replace CAST to CONVERT
Before:
```sql
SELECT CAST('123' AS INT);
```

After:
```sql
SELECT CONVERT(INT, '123');
```

## Replace CONVERT to CAST
Before:
```sql
SELECT CONVERT(INT, '123');
```

After:
```sql
SELECT CAST('123' AS INT);
```

## Replace temp table to variable and vice-versa
```sql
-- Before
DECLARE @MyTable TABLE
(
    Id INT,
    Name VARCHAR (200)
);

INSERT INTO @MyTable (Id, Name)
VALUES (1, '2')

-- After
CREATE TABLE #MyTable
(
    Id   INT,
    Name VARCHAR(200)
);

INSERT INTO #MyTable (Id, Name)
VALUES (1, '2')
```

## Reverse IIF expression and arguments
Before:
```sql
SELECT IIF(@a > @b, 'A', 'B');
```

After:
```sql
SELECT IIF(@b < @a, 'B', 'A');
```

## Flip operands
Before:
```sql
IF @a > @b PRINT 'A';
```

After:
```sql
IF @b < @a PRINT 'A';
```

## Replace VALUES to SELECT
Before:
```sql
DECLARE @t TABLE (Id INT, Name VARCHAR(50));
INSERT INTO @t (Id, Name)
VALUES (1, 'Artem'), (2, 'Ivan');
```
After:
```sql
DECLARE @t TABLE (Id INT, Name VARCHAR(50));
INSERT INTO @t (Id, Name)
SELECT Id   = 1,
       Name = 'Artem'
UNION ALL
SELECT Id   = 2,
       Name = 'Ivan';
```

## Move TOP to ORDER BY
Before:
```sql
SELECT TOP 100 *
FROM #MyTable
```
After:
```sql
SELECT *
FROM #MyTable
ORDER BY 1 OFFSET 0 ROWS
FETCH NEXT 100 ROWS ONLY
```

### Add style for date
![MsAddDateStyleInConvertIntention](https://raw.githubusercontent.com/scrappyCoco/Painless-Transact-SQL/master/screenshots/MsAddDateStyleInConvertIntention.png)

### Convert to MERGE
MERGE instructions is wide and time-consuming. This intention can reduce the time to write it. To use it there must be presented SELECT statement with "Source" and "Target" table aliases.

```sql
CREATE TABLE dbo.MySource
(
    Id   INT          NOT NULL PRIMARY KEY,
    Name VARCHAR(100) NOT NULL
);

CREATE TABLE dbo.MyTarget
(
    Id      INT          NOT NULL PRIMARY KEY,
    Name    VARCHAR(100) NOT NULL,
    AddDate DATETIME
);
GO

-- Before
SELECT *
FROM dbo.MySource AS Source
INNER JOIN dbo.MyTarget AS Target ON Source.Id = Target.Id;

-- After
MERGE dbo.MyTarget AS Target
USING (
    SELECT Id      = Id,
           Name    = Name,
           AddDate = NULL
    FROM dbo.MySource
) AS Source
ON Source.Id = Target.Id
WHEN NOT MATCHED BY TARGET THEN
    INSERT (Id, Name, AddDate)
    VALUES (Id, Name, AddDate)
WHEN NOT MATCHED BY SOURCE THEN DELETE
WHEN MATCHED THEN
    UPDATE
    SET Name    = Source.Name,
        AddDate = Source.AddDate;
```

## Add Comment
```sql
CREATE TABLE dbo.MyTable
(
    Id   INT          NOT NULL PRIMARY KEY,
    Name VARCHAR(MAX) NOT NULL
);

-- After
EXEC sys.sp_addextendedproperty
  @name = N'MS_Description', @value = N'...',
  @level0type = N'SCHEMA', @level0name = N'dbo',
  @level1type = N'TABLE', @level1name = N'MyTable'

EXEC sys.sp_addextendedproperty
  @name = N'MS_Description', @value = N'...',
  @level0type = N'SCHEMA', @level0name = N'dbo',
  @level1type = N'TABLE', @level1name = N'MyTable',
  @level2type = N'COLUMN', @level2name = N'Id'

EXEC sys.sp_addextendedproperty
  @name = N'MS_Description', @value = N'...',
  @level0type = N'SCHEMA', @level0name = N'dbo',
  @level1type = N'TABLE', @level1name = N'MyTable',
  @level2type = N'COLUMN', @level2name = N'Name'
```

## Replace equal sign to EXISTS-INTERSECT
```sql
DECLARE @source TABLE (Code CHAR(10) NOT NULL, Category INT, PRIMARY KEY (Code, Category))
DECLARE @target TABLE (Code CHAR(10) NOT NULL, Category INT, PRIMARY KEY (Code, Category))

-- Before
SELECT *
FROM @source AS Source
INNER JOIN @target AS Target ON Source.Code = Target.Code
    AND Source.Category = Target.Category

-- After
SELECT *
FROM @source AS Source
INNER JOIN @target AS Target ON Source.Code = Target.Code
    AND EXISTS(SELECT Source.Category INTERSECT SELECT Target.Category)
```

## Replace ISNULL to CASE
```sql
DECLARE @i VARCHAR(100);

-- Before
SELECT ISNULL(@i, '123');

-- After
SELECT CASE WHEN @i IS NULL THEN '123' ELSE @i END;
```

## Replace NULLIF to CASE
```sql
DECLARE @i VARCHAR(100);

-- Before
SELECT NULLIF(@i, '123');

-- After
SELECT CASE WHEN @i = '123' THEN NULL ELSE @i END;
```

# Gutters
## DML
In some cases when we are scrolling code from up to down could be useful to see only instructions, that changes data in the tables (not variables and temp table)
![DML Gutter](https://raw.githubusercontent.com/scrappyCoco/Painless-Transact-SQL/master/screenshots/DmlGutter.png)


# Completions
## Column list interface in INSERT context
![Insert Template Completion](https://raw.githubusercontent.com/scrappyCoco/Painless-Transact-SQL/master/screenshots/InsertTemplateCompletion.gif)

# DB Tree
## Folding
In some cases can be useful to open in the DB tree all objects of single type in some scopes. For example: open all columns in some database.
![Folding](https://raw.githubusercontent.com/scrappyCoco/Painless-Transact-SQL/master/screenshots/Folding.png)

# Usages
## Find Path to Caller
This action allow to find the root of caller. To try it let press ```double SHIFT | "Find Path to Caller"```
![Find Path to Caller](https://raw.githubusercontent.com/scrappyCoco/Painless-Transact-SQL/master/screenshots/PathToCaller.png)

## Show Used References
For the complex scripts could be very useful an ability to see all used objects in the tree. To try it let press ```double SHIFT | "Show Used References"```
![Show Used References](https://raw.githubusercontent.com/scrappyCoco/Painless-Transact-SQL/master/screenshots/UsedReferences.png)

# Contains syntax highlighting
![Folding](https://raw.githubusercontent.com/scrappyCoco/Painless-Transact-SQL/master/screenshots/Contains.PNG)
