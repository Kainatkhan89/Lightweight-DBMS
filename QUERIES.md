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
