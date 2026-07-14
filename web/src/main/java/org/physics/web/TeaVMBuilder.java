package org.physics.web;

import com.github.xpenatan.gdx.backends.teavm.config.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder;
import java.io.File;
import java.io.IOException;
import org.teavm.tooling.TeaVMTool;

/**
 * A small program that runs at build time (not in the browser). It tells TeaVM what to compile,
 * where to put the result, and what the entry point is, then runs the compile. It is invoked by the
 * {@code web:buildWeb} Gradle task.
 *
 * <p>The default heads-up text uses LibGDX's built-in font, which lives inside the LibGDX jar, so
 * we register those two files as assets to bundle into the web build.
 */
public class TeaVMBuilder {

  public static void main(String[] args) throws IOException {
    TeaBuildConfiguration configuration = new TeaBuildConfiguration();
    configuration.mainApplicationClass = "org.physics.web.TeaVMLauncher";
    configuration.webappPath = new File("build/dist").getCanonicalPath();

    configuration.htmlTitle = "Physics Playground";
    configuration.htmlWidth = 1280;
    configuration.htmlHeight = 720;
    configuration.useDefaultHtmlIndex = true;

    // Bundle LibGDX's built-in font so the on-screen text works in the browser.
    configuration.shouldGenerateAssetFile = true;
    configuration.additionalAssetsClasspathFiles.add("com/badlogic/gdx/utils/arial-15.fnt");
    configuration.additionalAssetsClasspathFiles.add("com/badlogic/gdx/utils/arial-15.png");

    TeaVMTool tool = TeaBuilder.config(configuration);
    tool.setMainClass("org.physics.web.TeaVMLauncher");
    TeaBuilder.build(tool);
  }
}
