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
