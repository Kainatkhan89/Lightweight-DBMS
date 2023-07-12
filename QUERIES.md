### Create table

```sql
CREATE TABLE Persons (
    personID int PRIMARY KEY,
    firstName varchar(255) NOT NULL UNIQUE,
    lastName varchar(255)  NOT NULL,
    address varchar(255) UNIQUE,
    city varchar(255),
    customer_id int REFERENCES Customers(id)
);
```
```sql
CREATE TABLE Customer (
    customerID int PRIMARY KEY,
    firstName varchar(255) NOT NULL UNIQUE,
    lastName varchar(255)  NOT NULL,
);
```

### Inserting data into the table

```sql
INSERT INTO Persons (personID, lastName, firstName, address, city, customer_id)
VALUES (1, 'Doe', 'John', '123 Main St', 'New York', 1001);
```

```sql
INSERT INTO Persons (personID, lastName, firstName, city, customer_id)
VALUES (2, 'Smith', 'Jane', 'Los Angeles', 1002);
```

```sql
INSERT INTO Persons (personID, lastName, firstName, address, city)
VALUES (3, 'Johnson', 'Mark', '456 Elm St', 'Chicago');
```

### Selecting data from the table

```sql
SELECT * FROM Persons;
```

```sql
SELECT * FROM Persons
WHERE personID = 1;
```

```sql
SELECT *
FROM Persons
WHERE firstName = 'Johnny';
```

```sql
SELECT personID, firstName, lastName, address, city, customer_id
FROM Persons;
```
```sql
SELECT personID, firstName, lastName
FROM Persons;
```

```sql
SELECT personID, firstName, lastName
FROM Persons
WHERE firstName = 'John';
```

### Updating data in the table

```sql
UPDATE Persons
SET address = '124 Main Street'
WHERE personID = 1;
```

```sql
UPDATE Persons
SET address = '123 Willet Street';
```

```sql
UPDATE Persons
SET firstName = 'Sarah', lastName = 'Johnson', address = '456 Elm Street'
WHERE personID = 2;
```

### Deleting data from the table

```sql
DELETE FROM Persons
WHERE personID = 1;
```

```sql
DELETE FROM Persons; 
```