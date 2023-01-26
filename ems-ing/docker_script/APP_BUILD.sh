. ./SET_ENV
cd ..
gradle clean
gradle compileQuerydsl
gradle build --exclude-task test