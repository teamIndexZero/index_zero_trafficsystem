# Traffic Simulation System index_zero_trafficsystem

Traffic management system group project by Team Index Zero
Created by KCL students as a part of group project 2016

## Building and running

**Prerequisites and Building** you need to have JDK >= 1.8 prior to running and building, and `JAVA_HOME` environment variable set.

In order to build the software, just execute from the project root (here):

 * for Mac/linux: `./gradlew clean assemble`
 * For windows machines: `gradlew.bat clean assemble`

It will compile, go through the tests (if any fail, will fail the build) and prepare distributions in relevant build
folders of submodules. Alternatively, you can install gradle system-wide and use it.

**Running**

In order to run the software with minimal hassle, just execute:
 * Mac/linux `./gradlew :gui:run`
 * Windows: `gradlew.bat :gui:run`

The GUI part of software will be first built and then ran for you by gradle builder. At the time being, it automatically
starts demonstration

Be sure to close the window to release gradle process.

## Developing with IntelliJ IDEA

 In order to import project, from the IntelliJ IDEA start screen, select Import -> From existing model -> Choose Gradle
 and then choose our build.gradle top-level file:
 
1. Start up IDEA
2. If any project is open, click File -> Close project
3. From IDEA New project screen, select Import project
4. Select Traffic System project folder
5. When asked what to do: Import project from external model -> Gradle
6. Next screen: settings: tick Use auto-import, tick create dirs for empty content roots. Use gradle wrapper.
7. Import all projects (tick all boxes) 
8. In IDEA (top-right): Unregistered VCS root detected -> Add root
9. Try building it - in Gradle sidebar, invoke: gradlew.bat build in top level terminal.
 

## Project structure

1. simulator - actual simulator with all the logic embedded in there
2. gui - graphical user interface capable of drawing the animated simulation, getting input either from the simulator
 directly via pipe, or from a file.
 
## Tests 
 
There are a couple of easy things in use for testing, namely: 
 
1. JUnit - 'default' test runner for java. Basically, you create a class in Test source root (`src/test/java`), make up 
 a method which is going to be the test and annotate it with `@Test` annotation. 
2. Mockito - mock framework. When we have no dependency ready, or when we want to test dependency in isolation, it comes handy. http://www.vogella.com/tutorials/Mockito/article.html nice tutorial
3. Fest-assert - fluent assertion library. https://github.com/alexruiz/fest-assert-2.x/wiki/One-minute-starting-guide one minute starting guide. It saves time on tests. 
 
Obviously we not need use 2) and 3), but they come handy as we get into the longer run.
