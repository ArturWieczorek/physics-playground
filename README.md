# Physics Playground

An interactive physics sandbox written in Java, built up one concept at a time as a
course for complete beginners. It runs as a native desktop app and in the browser
(WebGL), and every piece of the physics is plain, tested Java you can read.

You do not need a physics degree or years of programming behind you. If you can run one
command in a terminal, you can follow along. Each chapter adds one idea, explains the
maths in ordinary language, writes the test first, then writes the code that makes the
test pass.

## What you will build

A single app with a menu of scenes, each one a small experiment:

- Bouncing particles that obey Newton's laws
- A mass on a spring (simple harmonic motion)
- Elastic collisions that conserve momentum and energy
- A box of gas whose temperature is just the average energy of its particles
- Planets and moons pulled together by gravity, tracing Kepler orbits
- Electric charges with their field lines and equipotentials
- A charged particle spiralling through a magnetic field
- Atoms attracting and repelling through the Lennard-Jones potential, freezing and melting
- A piece of cloth you can grab, swing, and tear
- A pool of fluid you can splash and stir
- A race down three ramps where the cleverest curve, not the straight one, wins
- A double pendulum swinging in never-repeating chaos

## How it is organised

The code is split so that the physics is easy to test and easy to read:

```
engine/    Pure Java physics. No graphics, no framework. Just maths and tests.
app/       The LibGDX layer: draws each scene and handles mouse and keyboard.
desktop/   Launches the app as a native window.
web/       Compiles the app to WebGL so it runs in a browser.
docs/      The published web build (GitHub Pages).
chapters/  The course itself, one folder per lesson.
```

The important idea: all the real physics lives in `engine`, which depends on nothing.
That is what lets us test it thoroughly and explain it clearly, before a single pixel is
drawn.

## The course

Start at [chapters/00-orientation](chapters/00-orientation/README.md) and work forward.
Each chapter is one commit in the history and one git tag (`ch00`, `ch01`, ...), so you
can jump to any lesson with:

```
git checkout ch05
```

and see exactly the code as it stood at that point.

| Chapter | Topic |
| ------- | ----- |
| ch00 | Orientation: what we build and how simulation works |
| ch01 | Project skeleton: build, format, and test tooling |
| ch02 | Vectors: the maths of direction and magnitude |
| ch03 | Motion: turning forces into movement over time |
| ch04 | Forces and Newton's second law |
| ch05 | Springs and simple harmonic motion |
| ch06 | Collisions, momentum, and energy |
| ch07 | Kinetic theory: a box of gas |
| ch08 | Gravitation and orbits |
| ch09 | Electric charges and fields |
| ch10 | Magnetism and the Lorentz force |
| ch11 | Molecular dynamics: atoms that stick and melt |
| ch12 | Cloth you can tear |
| ch13 | Fluid you can splash |
| ch14 | Shipping it: desktop and web builds |
| ch15 | The brachistochrone: the fastest descent |
| ch16 | The double pendulum and chaos |

## Running it

Requirements: Java 21 or newer. Everything else is fetched by the build.

```
./gradlew desktop:run        # open the native app in a window
./gradlew test               # run all the physics tests
./gradlew spotlessApply       # format the code
```

Switch scenes with the number keys (or N and B to cycle), reset with R, pause with P, and slow
down or speed up time with the `[` and `]` keys. Click or drag to interact, and use the arrow
keys to adjust each scene's parameters. The heads-up display names the current scene, lists its
controls, and shows its live numbers; each scene's controls are also described in its chapter.

### A packaged desktop app

```
./gradlew desktop:shadowJar
java -jar desktop/build/libs/physics-playground.jar
```

### The browser build (WebGL)

The whole app compiles to JavaScript and WebGL with TeaVM, so it runs in a browser with no
install:

```
./gradlew web:buildWeb        # compiles to JS/WebGL and writes the result into docs/
```

To try it locally, serve the folder and open it:

```
npx http-server docs          # then open the printed http://localhost:... address
```

### Publishing to GitHub Pages

The `docs/` folder is a complete static site. In the repository settings, under Pages, choose
"Deploy from a branch", pick the `main` branch and the `/docs` folder, and save. The playground
will be live at your GitHub Pages URL a minute later. Re-run `./gradlew web:buildWeb` and commit
`docs/` whenever you want to update it.

The details of the browser build and packaging are covered in ch14.

## Licence

MIT. See [LICENSE](LICENSE).
