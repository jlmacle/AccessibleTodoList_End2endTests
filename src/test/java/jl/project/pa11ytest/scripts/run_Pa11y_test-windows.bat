if exist ../AccessibleTodoList_FrontEnd/node_modules/pa11y/bin (cd ../AccessibleTodoList_FrontEnd/node_modules/.bin && pa11y http://localhost:4200) else (pa11y http://localhost:4200)
cd src/test/java/jl/project/pa11ytest/scripts/
stop_potentially_existing_Angular_Spring_server_processes-windows.bat