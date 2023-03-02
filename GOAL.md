## Goals
Create a web application that allows a manager to assign tasks to employees.
All the objects involved in this taskDto should be very simple. There is no 
need to spend a lot of time thinking of complex scenarios.
The objects you really need are the employees and the tasks. 
The functionality that needs to be implemented is the assignment, unassignment and reassignment of
tasks to the employees. Also, the user should have the possibility to change the due date of the taskDto.

As a senior developer you are required to build the full stack of the 
application, so we will be looking to find:

#### Back end
* the creation of the db tables, with some tasks and some users in them
* the JPA part
* services
* controllers
* `unit tests`

#### Front end
* Will have it's own project.

Good Luck!

### Requirements:
In order for this project to build and start you need:
+ `Maven v3.8.1` or higher 
+ `Node js v16.14.0` or higher
+ `npm v8.3.1` or higher
+ `Java v17` (maybe even 15)


### Code
 You're going to work with: 
> **Employees**:
> * id
> * name
>  	
> **Tasks**:
> *id
> * description
> * due date
> * assignee (an employee)

To allow the users of the application to:
> * Assign, unassign and reassing tasks to employees
> * Change the due date of a taskDto


