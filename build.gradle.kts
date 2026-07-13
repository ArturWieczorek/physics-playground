import com.diffplug.gradle.spotless.SpotlessExtension

// Root build script. It sets up the things every module shares: where to fetch
// libraries from, which Java version to use, how to format the code, and how to
// run tests. Each module then adds only what is special about itself.

plugins {
  // Spotless keeps the code formatted in one consistent style. Declared here,
  // applied in every module below.
  id("com.diffplug.spotless") version "6.25.0" apply false
}

allprojects {
  group = "org.physics"
  version = "0.1.0"

  repositories {
    mavenCentral()
  }
}

subprojects {
  apply(plugin = "java")
  apply(plugin = "com.diffplug.spotless")

  // Build against Java 21. The toolchain lets Gradle find or download the right
  // JDK, so everyone builds with the same version.
  configure<JavaPluginExtension> {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
  }

  // One formatting style for all Java code: Google's, applied on demand with
  // "./gradlew spotlessApply" and checked in the build with "spotlessCheck".
  configure<SpotlessExtension> {
    java {
      target("src/**/*.java")
      googleJavaFormat("1.22.0")
      removeUnusedImports()
      trimTrailingWhitespace()
      endWithNewline()
    }
  }

  dependencies {
    "testImplementation"(platform("org.junit:junit-bom:5.11.0"))
    "testImplementation"("org.junit.jupiter:junit-jupiter")
    "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
  }

  tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging { events("passed", "skipped", "failed") }
  }
}
