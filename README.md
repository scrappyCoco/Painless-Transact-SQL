# Painless-Transaact-SQL
## Inspections
### Code Style
#### Using CASE instead of CHOOSE and vice-versa
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

#### Column alias definition
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

#### Semicolon at the end of statement
This inspection allow to append semicolon at the end of each statement implicitly.

Before:
```sql
SELECT 1
```

After:
```sql
SELECT 1;
```

### DDL
#### READONLY keyword check
This inspection warn when keyword READONLY was missing.
![MsReadonlyParameterInspection](https://raw.githubusercontent.com/scrappyCoco/Painless-Transact-SQL/master/screenshots/MsReadonlyParameterInspection.png)

### DML
#### READONLY modification check
This inspection check attempt to insert into readonly table variable.
![MsReadonlyModificationInspection](https://raw.githubusercontent.com/scrappyCoco/Painless-Transact-SQL/master/screenshots/MsReadonlyModificationInspection.png)

#### Check for semicolon before CTE (Common Table Expression)
This inspection check for existence semicolon before CTE.
![MsSemicolonCteInspection](https://raw.githubusercontent.com/scrappyCoco/Painless-Transact-SQL/master/screenshots/MsSemicolonCteInspection.png)

#### Redundant DISTINCT keyword in set operators
DISTINCT is redundant in set operators: UNION, INTERSECT, EXCEPT.
![MsRedundantDistinctInSetOperatorsInspection](https://raw.githubusercontent.com/scrappyCoco/Painless-Transact-SQL/master/screenshots/MsRedundantDistinctInSetOperatorsInspection.png)

### String functions
#### SUBSTRING function check
This inspection offer to replace SUBSTRING to LEFT.
Before:
```sql
SELECT SUBSTRING('ABCDEF', 1, 2);
```
After:
```sql
SELECT LEFT('ABCDEF', 2);
```

#### TRIM function check
This inspection offer to replace the sequence LTRIM/RTRIM to TRIM.
Before:
```sql
SELECT LTRIM(RTRIM(' ABCDEF '));
```
After:
```sql
SELECT TRIM(' ABCDEF ');
```

## Intentions
### Replace LEFT to SUBSTRING
Before:
```sql
SELECT LEFT('ABCDEF', 2);
```
After:
```sql
SELECT SUBSTRING('ABCDEF', 1, 2);
```

### Replace CAST to CONVERT
Before:
```sql
SELECT CAST('123' AS INT);
```
After:
```sql
SELECT CONVERT(INT, '123');
```

### Replace CONVERT to CAST
Before:
```sql
SELECT CONVERT(INT, '123');
```
After:
```sql
SELECT CAST('123' AS INT);
```

### Reverse IIF expression and arguments
Before:
```sql
SELECT IIF(@a > @b, 'A', 'B');
```
After:
```sql
SELECT IIF(@b < @a, 'B', 'A');
```

### Swap operands
Before:
```sql
IF @a > @b PRINT 'A';
```
After:
```sql
IF @b < @a PRINT 'A';
```

### Replace VALUES to SELECT
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
