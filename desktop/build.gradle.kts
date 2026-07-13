// Desktop launcher. It starts the application in a normal window using LibGDX's
// LWJGL3 backend, and pulls in the desktop native libraries for graphics.

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
