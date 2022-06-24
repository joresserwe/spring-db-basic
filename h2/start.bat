@echo off
for /f "delims=" %%F in ('dir /b /s "bin\*.jar" 2^>nul') do javaw -jar %%F
