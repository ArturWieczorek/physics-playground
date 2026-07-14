// Desktop launcher. It starts the application in a normal window using LibGDX's
// LWJGL3 backend, and pulls in the desktop native libraries for graphics. The
// Shadow plugin bundles everything into a single runnable jar.

plugins {
  id("com.gradleup.shadow") version "8.3.5"
}

val gdxVersion = "1.13.1"

dependencies {
  implementation(project(":app"))
  implementation(project(":engine"))
  implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion")
  implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
}

// "./gradlew desktop:run" opens the app window.
tasks.register<JavaExec>("run") {
  group = "application"
  description = "Runs the Physics Playground desktop app."
  mainClass.set("org.physics.desktop.DesktopLauncher")
  classpath = sourceSets["main"].runtimeClasspath
}

// The jar manifest names the entry point; the Shadow plugin's fat jar inherits it.
tasks.jar {
  manifest {
    attributes["Main-Class"] = "org.physics.desktop.DesktopLauncher"
  }
}

// "./gradlew desktop:shadowJar" produces a single runnable jar:
//   java -jar desktop/build/libs/physics-playground.jar
tasks.shadowJar {
  archiveBaseName.set("physics-playground")
  archiveClassifier.set("")
  archiveVersion.set("")
}
