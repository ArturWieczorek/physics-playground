# Chapter 1: The skeleton

Before we write any physics, we set up the workshop: a build system that compiles and runs
the project, a formatter that keeps the code tidy, and a testing framework we will use in
every chapter from here on. By the end you will have an empty window that opens, and a
first passing test.

If you have never used any of these tools, do not worry. You only need to understand what
each one is for, not every detail.

## The pieces

- **Java 21**. The language we write in. Check you have it with `java -version`.
- **Gradle**. The build tool. It compiles the code, downloads the libraries we depend on,
  runs the tests, and starts the app. You do not install it by hand: the project carries a
  small "wrapper" (`gradlew`) that fetches the correct version for you the first time you
  run it.
- **LibGDX**. The library that opens a window, draws shapes, and reads the mouse and
  keyboard. It also lets the same code run in a web browser later.
- **JUnit 5**. The testing framework. We describe what the code should do, and JUnit checks
  it, quickly and automatically.
- **Spotless** with Google Java Format. One consistent code style, applied by a command so
  we never argue about spacing.

## How the project is split into modules

Gradle lets us divide the project into modules that depend on one another. We use four:

```
engine    The physics. Plain Java, no graphics. Depends on nothing.
app       The application: draws scenes, handles input. Depends on engine + LibGDX.
desktop   Opens the app in a native window. Depends on app.
web       (added in ch14) Runs the app in a browser.
```

The dependency arrows only ever point one way: `desktop -> app -> engine`. The engine never
depends on the app or on LibGDX. That is what keeps the physics pure and testable.

The layout on disk:

```
settings.gradle.kts     Lists the modules.
build.gradle.kts        Shared setup: Java version, formatting, tests.
gradle.properties       A few build options.
gradlew, gradle/        The Gradle wrapper (run the build without installing Gradle).
engine/                 The physics library and its tests.
app/                    The LibGDX application.
desktop/                The desktop launcher.
```

## What we wrote

The engine has its first real code: a tiny class of number helpers,
`org.physics.engine.math.Scalars`, with two methods:

- `clamp(value, min, max)` keeps a number inside a range. We will use it later to stop
  particles drifting off screen.
- `approximatelyEqual(a, b, tolerance)` asks whether two numbers are the same to within a
  small wiggle. Because simulation is always slightly approximate, we compare numbers this
  way instead of with `==`. This helper turns up in almost every test we write.

Both come with tests in `ScalarsTest`, including the cases that should fail loudly, such as
a backwards range or a negative tolerance. Writing those tests first, then the code, is the
habit we keep for the whole course.

The app side is deliberately empty: `PlaygroundApp` opens a window and paints it a dark
colour, and `DesktopLauncher` starts it. We will give it something to show starting in ch05.

## Running it

From the project folder:

```
./gradlew test           Compile everything and run all tests.
./gradlew spotlessApply   Reformat the code to the shared style.
./gradlew build          Format check, compile, and test in one go.
./gradlew desktop:run    Open the (empty, dark) app window.
```

The first run downloads Gradle and the libraries, so it takes a minute. After that it is
fast. You should see all tests pass, and `desktop:run` should open a 1024x720 window titled
"Physics Playground".

## What to take away from this chapter

- Gradle builds, tests, and runs the project; the wrapper means nobody has to install it.
- The project is split so the physics `engine` depends on nothing and stays testable.
- We write the test first, then the code that satisfies it.
- The formatter keeps style out of our way.

Next, ch02, we build the first real physics tool: a vector, the object that represents both
a position and a velocity, and the arithmetic that goes with it.
