DROP SCHEMA IF EXISTS testdb1;

CREATE SCHEMA IF NOT EXISTS testdb1;

#GRANT ALL PRIVILEGES ON testdb1.* TO 'root'@'%' IDENTIFIED BY 'root';
#GRANT ALL PRIVILEGES ON testdb1.* TO 'root'@'localhost' IDENTIFIED BY 'root';

use testdb1;

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


insert into Department values(1001, 'Research');
insert into Department values(1002, 'Sales');
insert into Department values(1003, 'Technology');
insert into Department values(1004, 'Security');


insert into Employee values(100001, 'David', 'Duffy', 'CEO', '1985-12-17', NULL, NULL);

insert into Employee values(100002, 'Kevin', 'Withers', 'Head of Research', '1989-01-05', 100001, 1001);
insert into Employee values(100003, 'Tracey', 'Connor', 'VP-Sales', '1995-02-10', 100001, 1002);
insert into Employee values(100004, 'Gary', 'White', 'CTO', '1999-03-15', 100001, 1003);
insert into Employee values(100005, 'Matthew', 'Frost', 'Security Manager', '1998-04-20', 100001, 1004);

insert into Employee values(100006, 'Petra', 'Moody', 'Senior Research Analyst', '2009-07-15', 100002, 1001);
insert into Employee values(100007, 'Nigel', 'Pentland', 'Research Analyst', '2014-01-25', 100002, 1001);

insert into Employee values(100008, 'Patricia', 'Murray', 'Senior Sales Asscoiate', '2011-09-18', 100003, 1002);
insert into Employee values(100009, 'Rachael', 'Stevenson', 'Sales Asscoiate', '2012-10-22', 100003, 1002);
insert into Employee values(100010, 'John', 'Smith', 'Sales Asscoiate', '2013-11-26', 100003, 1002);

insert into Employee values(100011, 'Joe', 'Bloggs', 'Business Analyst', '2011-02-23', 100004, 1003);
insert into Employee values(100012, 'Gerard', 'Brawley', 'Software Developer', '2013-03-29', 100004, 1003);

insert into Employee values(100013, 'Ricky', 'McLaren', 'Security Analyst', '2009-06-03', 100005, 1004);
insert into Employee values(100014, 'Andy', 'McNee', 'Security Analyst', '2010-05-09', 100005, 1004);

commit;