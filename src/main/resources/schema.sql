-- This is where your DML goes
create table employee(
	id BIGINT not null auto_increment primary key,
	name varchar(200)
);

create table task(
	id BIGINT not null auto_increment primary key,
	description varchar(500),
	due_date varchar(20),
	assignee BIGINT--can be used as FK
);