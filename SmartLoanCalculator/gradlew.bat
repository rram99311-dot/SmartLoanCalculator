@echo off
setlocal EnableExtensions
set "GRADLE_VERSION=8.9"
set "GRADLE_ROOT=%USERPROFILE%\.gradle\wrapper\dists"
set "GRADLE_HOME=%GRADLE_ROOT%\gradle-%GRADLE_VERSION%"
if not exist "%GRADLE_HOME%\bin\gradle.bat" (
  if not exist "%GRADLE_ROOT%" mkdir "%GRADLE_ROOT%"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest 'https://services.gradle.org/distributions/gradle-8.9-bin.zip' -OutFile '%GRADLE_ROOT%\gradle-8.9-bin.zip'; Expand-Archive -Force '%GRADLE_ROOT%\gradle-8.9-bin.zip' '%GRADLE_ROOT%'"
)
call "%GRADLE_HOME%\bin\gradle.bat" %*
