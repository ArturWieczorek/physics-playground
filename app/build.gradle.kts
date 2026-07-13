// The visible application. It depends on the physics engine and on LibGDX for
// drawing and input. It knows nothing about how it is launched: the desktop and
// web modules each provide their own entry point that starts this app.

val gdxVersion = "1.13.1"

dependencies {
  implementation(project(":engine"))
  implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
}
