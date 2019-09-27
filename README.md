# Kotlin AfterWorkshop Exercise

This project contains a small skeleton code that can be used in order to play in the "game" presented
at the end of the presentation.

One could also use any other tools, but where is the fun in that. :)

## Prerequisite
To run, one need to have installed `java` on ones environment.

## Building
Building can be done with the following:
```shell script
./gradlew clean assemble
```
Where gradle will be downloaded.

## Run (build is also included)
```shell script
./gradlew run
```

Will run the `main()` method in the `SpammyKt.kt` file.
This is configured in the gradle file as:
```kotlin
application {
    mainClassName = "se.r2m.kotlin.aw.SpammyKt"
}
```

## Other tasks?
Gradle can display other tasks using:
```shell script
./gradle tasks
```