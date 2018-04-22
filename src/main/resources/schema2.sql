DROP SCHEMA IF EXISTS testdb2;

CREATE SCHEMA IF NOT EXISTS testdb2;

#GRANT ALL PRIVILEGES ON testdb2.* TO 'root'@'%' IDENTIFIED BY 'root';
#GRANT ALL PRIVILEGES ON testdb2.* TO 'root'@'localhost' IDENTIFIED BY 'root';

use testdb2;

DROP TABLE IF EXISTS Employee;

DROP TABLE IF EXISTS Department;

CREATE TABLE Department 
( ID 			int(10) 		NOT NULL,
  NAME 			varchar(30) 	NOT NULL,
  CONSTRAINT    department_pk 	PRIMARY KEY (ID)
);


CREATE TABLE Employee 
( ID 			int(10) 	 	NOT NULL,
  FIRST_NAME 	varchar(30) 	NOT NULL,
  LAST_NAME 	varchar(30) 	NOT NULL,
  JOB_TITLE 	varchar(30) 	NOT NULL,
  HIRE_DATE		varchar(30)		NOT NULL,
  MANAGER_ID	int(10),
  DEPARTMENT_ID	int(10),  
  CONSTRAINT    employee_pk 	PRIMARY KEY (ID),
  CONSTRAINT   fk_departments
    FOREIGN KEY (DEPARTMENT_ID)
    REFERENCES Department(ID)
);

commit;