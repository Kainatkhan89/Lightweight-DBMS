
## For Authentication module

1. Create user

- [x] Prompt user_name, password and question / answer
- [x] Generate user_id
- [x] Save user_id, user_name, password (encrypted) in "AUTH_FILE_PATH"
- [x] Save user_id question, answer in "AUTH_QA_FILE_PATH"

2. Login user

- [x] Prompt user_name and password
- [x] Read from "AUTH_FILE_PATH" and iterate over each line
- [x] Find user_name and match password (encrypt password)
- [x] If user_name and password match, return ask question and then match the answer as well
- [x] Save the login user info in a data structure (**User** class)


# For DBMS system

> DBMS file system structure should look like below

```
data/
├── auth
│   ├── user_pass.txt
│   └── user_qa.txt
└── db
    ├── student_data.txt
    ├── student_meta.txt
    ├── teacher_data.txt
    └── teacher_meta.txt
```

> Here, `student` and `teacher` are the table names

## Main module

- [x] Check if current_user (**User** class variable) is empty, prompt user to login or signup
- [x] If user wants to login, then do follow (Login user) steps
- [x] If user wants to signup, then do follow (Create user) steps, and exit the program (user should login again)

- [x] If user is logged-in (**User** class variable is not null), then prompt user to enter the query
- [x] Parse the query and extract the command (CREATE, SELECT, INSERT, UPDATE, DELETE, TRANSACTION etc.)
- [x] Based on the command, call the respective module (by passing query_string and current_user objects and `is_transaction`)

> Note: If `is_transaction` is true, then you should not print the results (only print the results when the transaction is committed)

> Note: If `is_transaction` is false, then you should print the results

> Note: If `is_transaction` is true, then use the temporary files (DB_DIR_PATH + <table-name> + DB_TEMP_META_SUFFIX/DB_TEMP_DATA_SUFFIX) instead of the original files (DB_DIR_PATH + <table-name> + DB_META_SUFFIX/DB_DATA_SUFFIX)

## CREATE module
> This modules creates a table in the database

- [x] Parse the query and extract the table name, column names and column types
- [x] If table name already exists (you already have a file i.e: DB_DIR_PATH + <table-name> + DB_META_SUFFIX already exists), then throw error
- [x] If table doesn't exist before, then create two files
    - [x] DB_DIR_PATH + <table-name> + DB_META_SUFFIX (this file will contain the meta data of the table)
    - [x] DB_DIR_PATH + <table-name> + DB_DATA_SUFFIX
    (this file will contain the actual data of the table)
- [x] Write the meta data in the meta file, meta data should be able read and converted to a hash-map/or any data structure [`meta-data structure`] (for validation purpose). 
- [x] Meta data should contain the following information
    - [x] table_name
    - [x] column_names
    - [x] column_types
    - [x] column_sizes
    - [x] column_is_nullable
    - [x] column_is_unique
    - [x] column_is_primary_key
    - [x] column_is_foreign_key
    - [x] column_foreign_key_table_name
    - [x] column_foreign_key_column_name
    - [x] column_foreign_key_on_delete
    - [x] column_foreign_key_on_update

## SELECT module

- [x] Parse the query and extract the table name, column names and where clause
- [x] If table name doesn't exist (you don't have a file i.e: DB_DIR_PATH + <table-name> + DB_META_SUFFIX), then throw error
- [x] If table exists, then read the meta data from the meta file and convert to `meta-data structure`. Check if the column names are valid or not, if not, then throw error.
- [x] If column names are valid, then read the data from the data file and convert to `data-structure`. Check if the where clause is valid or not, if not, then throw error.
- [x] If where clause is valid, then filter the data based on the where clause and print the results (column names and data)

## INSERT module

- [x] Parse the query and extract the table name, column names and values
- [x] If table name doesn't exist (you don't have a file i.e: DB_DIR_PATH + <table-name> + DB_META_SUFFIX), then throw error
- [x] If table exists, then read the meta data from the meta file and convert to `meta-data structure`. Check if the column names are valid or not, if not, then throw error.
- [x] If column names are valid, then check if the values are valid or not, if not, then throw error.
- [x] If values are valid, then check if the values are unique or not (or other constraints based on the meta-data), if not, then throw error.
- [x] If all the constraints are valid, then insert the data in the data file and print the results (column names and data)

> Note: also add <row-id> as well (this will come handy when you want to update/delete a row)

## UPDATE module

- [x] Parse the query and extract the table name, column names, values and where clause
- [x] If table name doesn't exist (you don't have a file i.e: DB_DIR_PATH + <table-name> + DB_META_SUFFIX), then throw error
- [x] If table exists, then read the meta data from the meta file and convert to `meta-data structure`. Check if the column names are valid or not, if not, then throw error.
- [x] If column names are valid, then read + filter the data based on `where` clause.
- [x] Check if the updated_value are unique or not (or other constraints based on the `meta-data`), if not, then throw error.
- [x] If all the constraints are valid, then update the data in the data file and print the results (column names and data)

> Note: only update those line(s) which matches the where clause (you can use <row-id> for this purpose)

## DELETE module

- [x] Similar to update except that you will delete the line(s) which matches the where clause

> Note: only delete those line(s) which matches the where clause (you can use <row-id> for this purpose)

> If the where clause is empty, then delete all the lines

> Note: keep in mind the constraints (unique, primary key, foreign key etc.) while deleting the line(s). For example, if you delete a line which is referenced by a foreign key, then you should throw error.

## TRANSACTION module

- [x] Parse the query string and extract the all commands and table name(s).
- [x] Copy all table names to a temporary directory (DB_DIR_PATH + <table-name> + DB_TEMP_META_SUFFIX/DB_TEMP_DATA_SUFFIX).
- [x] Iterate over each command, call the respective module (by passing query_string and current_user object and `is_transaction` = true)
- [x] Make all the changes in the temporary files (DB_DIR_PATH + <table-name> + DB_TEMP_META_SUFFIX/DB_TEMP_DATA_SUFFIX).
- [x] If `commit`, then replace the temp files with original files.
- [x] If `rollback`, then delete the temp files.

## ERD module

- [x] Parse the query string and extract the all commands and table name(s).
- [x] Extract all the meta-data structure for each table.
- [x] Extract all the foreign key constraints and construct the ERD string like below.

For example:

~~~~~~~~~~~~~~~~~~~~~~~~~~
Student -- 1:N -- Course 
~~~~~~~~~~~~~~~~~~~~~~~~~~
Student
- id (PK)
- name
- age
- course_id (FK)
~~~~~~~~~~~~~~~~~~~~~~~~~~
Course
- id (PK)
- name
~~~~~~~~~~~~~~~~~~~~~~~~~~
