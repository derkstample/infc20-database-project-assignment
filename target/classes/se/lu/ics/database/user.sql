-- Create the SQL login for java_app_user with a secure password
CREATE LOGIN java_app_user
WITH PASSWORD = '<Your Strong Password>';

-- Switch to the target database
USE CompanyDB;

-- Create a user in the database mapped to the login
CREATE USER java_app_user FOR LOGIN java_app_user;

-- Grant EXECUTE permission on specific procedures
GRANT EXECUTE ON uspGetAllStudentsWithCourses 
TO java_app_user;

GRANT EXECUTE ON uspGetAllStudents
TO java_app_user;

GRANT EXECUTE ON uspGetStudentByPersonalNo
TO java_app_user;

GRANT EXECUTE ON uspInsertStudent
TO java_app_user;

GRANT EXECUTE ON uspUpdateStudent
TO java_app_user;

GRANT EXECUTE ON uspDeleteStudent
TO java_app_user;