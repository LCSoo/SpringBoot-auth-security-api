CREATE DATABASE IF NOT EXISTS auth_security_system;
CREATE DATABASE IF NOT EXISTS auth_security_system_test;

GRANT ALL PRIVILEGES ON auth_security_system.* TO 'admin'@'%';
GRANT ALL PRIVILEGES ON auth_security_system_test.* TO 'admin'@'%';
FLUSH PRIVILEGES;