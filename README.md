# Traffic Simulation System index_zero_trafficsystem

Traffic management system group project by Team Index Zero
Created by KCL students as a part of group project 2016

## Building and running

**Prerequisites** you need to have JDK > 1.7 prior to running and building, and `JAVA_HOME` environment variable set.

 In order to build the software, just execute for Mac/linux:

```
 ./gradlew clean assemble
```

 For windows machines:

```
 gradlew.bat clean assemble 
```

 It will compile, go through the tests (if any fail, will fail the build) and prepare distributions in relevant `build`
 folders of submodules. Alternatively, you can install gradle system-wide and use it.

 ## Developing with IntelliJ IDEA

 In order to import project, from the IntelliJ IDEA start screen, select Import -> From existing model -> Choose Gradle
 and then choose our build.gradle top-level file.

 ## Project structure

 1. simulator - actual simulator with all the logic embedded in there
 2. gui - graphical user interface capable of drawing the animated simulation, getting input either from the simulator
 directly via pipe, or from a file.
