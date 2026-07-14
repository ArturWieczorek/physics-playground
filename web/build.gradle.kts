// Web launcher. It compiles the whole application to JavaScript and WebGL using
// TeaVM, through the gdx-teavm backend, so the same Java runs in a browser. The
// output is copied into the top-level docs/ folder, which GitHub Pages serves.

val gdxVersion = "1.13.1"
val teavmBackend = "1.2.0"

repositories {
  // The TeaVM backend pulls one small helper (jMultiplatform) that is published on JitPack.
  maven { url = uri("https://jitpack.io") }
}

dependencies {
  implementation(project(":app"))
  implementation(project(":engine"))
  implementation("com.github.xpenatan.gdx-teavm:backend-teavm:$teavmBackend")
  implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
}

// "./gradlew web:buildWeb" compiles to JS/WebGL and copies the result into docs/.
tasks.register<JavaExec>("buildWeb") {
  group = "web"
  description = "Compiles the app to WebGL/JS with TeaVM and copies it into docs/."
  mainClass.set("org.physics.web.TeaVMBuilder")
  classpath = sourceSets["main"].runtimeClasspath

  doLast {
    val docs = rootProject.layout.projectDirectory.dir("docs").asFile
    docs.deleteRecursively() // clear any previous build
    copy {
      from(layout.buildDirectory.dir("dist/webapp")) // put index.html at the docs/ root
      into(docs)
    }
  }
}
