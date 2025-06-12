@echo off
echo Compiling Java files...
"c:\Program Files\Java\jdk-21\bin\javac.exe" -d build -cp "lib\mysql-connector-j-8.0.33.jar;." src\model\*.java src\dao\*.java src\utils\*.java src\logic\*.java src\ui\*.java

echo Running application...
"c:\Program Files\Java\jdk-21\bin\java.exe" -cp "build;lib\mysql-connector-j-8.0.33.jar" ui.MainApp

pause 