@echo off
echo Compilando projeto...
javac -cp "lib/mysql-connector-j-9.4.0.jar" -d bin src/model/*.java
javac -cp "lib/mysql-connector-j-9.4.0.jar;bin" -d bin src/dao/*.java
javac -cp "bin" -d bin src/util/*.java
javac -cp "lib/mysql-connector-j-9.4.0.jar;bin" -d bin src/view/*.java
echo Compilacao concluida!
echo.
echo Execute com: java -cp "bin;lib/mysql-connector-j-9.4.0.jar" view.LoginFrame
pause