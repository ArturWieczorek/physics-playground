# Chapter 14: Shipping it

We have a playground full of physics. This last chapter is about getting it into other people's
hands: a packaged desktop app they can double-click, and, the headline feature, a version that
runs in a web browser with nothing to install. The same Java we have written all along, running
on a web page.

## One codebase, two launchers

Recall the shape of the project. All the physics is in `engine`, the app itself is in `app`, and
neither of them knows or cares how it is started. That separation pays off now. To ship to a new
place, we only add a thin launcher; nothing about the physics or the scenes changes.

We already had `desktop`, which starts the app in a native window. Now we add `web`, which starts
the exact same app in a browser.

## The desktop package

For the desktop we bundle everything, our code, LibGDX, and its native libraries, into a single
runnable jar using the Shadow plugin:

```
./gradlew desktop:shadowJar
java -jar desktop/build/libs/physics-playground.jar
```

That one file is the whole app. Anyone with Java can run it with no build tools and no setup.

## The browser build

This is the part that feels like magic: our Java runs in a web browser, drawing through WebGL,
with no plugin and no download for the visitor. The tool that makes it possible is TeaVM, which
reads compiled Java and rewrites it as JavaScript. The `gdx-teavm` backend teaches TeaVM how to
turn LibGDX's drawing and input into the browser's canvas and events.

The `web` module has two small pieces, mirroring the desktop:

- `TeaVMLauncher` is the browser entry point. It is the twin of `DesktopLauncher`: it points the
  app at an HTML canvas and hands over the very same `PlaygroundApp`.
- `TeaVMBuilder` is a small build-time program that tells TeaVM what to compile, where to put the
  result, and bundles LibGDX's built-in font so the on-screen text works.

One Gradle task runs the whole thing:

```
./gradlew web:buildWeb
```

It compiles every class the app touches to JavaScript, copies in the assets and the WebGL glue,
and writes a complete little website into the top-level `docs/` folder: an `index.html`, the
compiled `app.js`, and its supporting files. You can try it locally by serving that folder:

```
npx http-server docs
```

and opening the address it prints. You should see the playground exactly as on the desktop, in
the browser, with the same number keys and mouse controls.

## Publishing to GitHub Pages

Because the build lands in `docs/`, publishing is almost nothing. GitHub Pages can serve a
`docs/` folder straight from the repository. In the repository settings, under Pages, choose
"Deploy from a branch", select the `main` branch and the `/docs` folder, and save. A minute later
the playground is live on the web for anyone to open. To update it, re-run `./gradlew
web:buildWeb` and commit the changed `docs/`.

## A note on the journey

It is worth pausing on what just happened. The same `Vector2` we built test-first in ch02, the
same integrators from ch03, the same forces and constraints from the chapters since, all of it
now runs on a desktop and in a browser, unchanged. That is the reward for keeping the physics
pure and separate from the drawing. The engine never knew about windows or web pages, so moving
it to a new home cost us only a launcher.

## Running it

```
./gradlew test                # every physics test, one last time
./gradlew desktop:run         # the native app
./gradlew desktop:shadowJar   # the packaged jar
./gradlew web:buildWeb        # the browser build into docs/
```

## What to take away from this chapter

- Because the app is separate from its launcher, shipping to a new platform is just a new
  launcher.
- The Shadow plugin bundles the desktop app into one runnable jar.
- TeaVM compiles the Java to JavaScript and WebGL, so the app runs in a browser with no install.
- The build writes a static site into `docs/`, which GitHub Pages serves directly.

That is the end of the course. You started with a single falling dot and finished with a
tearable cloth, a splashing fluid, and a physics playground anyone can open in a browser, every
piece of it plain, tested Java you wrote and understand.
