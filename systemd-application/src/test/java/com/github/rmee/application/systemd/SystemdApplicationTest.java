package com.github.rmee.application.systemd;

import org.gradle.internal.impldep.com.amazonaws.util.IOUtils;
import org.gradle.internal.impldep.org.testng.Assert;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SystemdApplicationTest {

    public TemporaryFolder testFolder;

    private File workingDir;

    @Test
    public void check() throws IOException {
        File tempDir = new File("build/tmp");
        tempDir.mkdirs();
        testFolder = new TemporaryFolder(tempDir);

        testFolder.create();
        workingDir = new File(testFolder.getRoot(), "demo");
        workingDir.mkdirs();

        File javaFolder = new File(workingDir, "src/main/java/example");
        javaFolder.mkdirs();
        File rpmFolder = new File(workingDir, "src/main/rpm");
        rpmFolder.mkdirs();

        System.setProperty("org.gradle.daemon", "false");

        File gradleFile = new File(workingDir, "build.gradle");
        File settingsFile = new File(workingDir, "settings.gradle");
        File entityFile = new File(javaFolder, "Main.java");
        File propertiesFile = new File(rpmFolder, "application.properties");

        Assert.assertNotNull(getClass().getClassLoader().getResource("plugin-under-test-metadata.properties"));

        IOUtils.copy(getClass().getClassLoader().getResourceAsStream("helm-app/input.gradle"),
                new FileOutputStream(gradleFile));
        IOUtils.copy(getClass().getClassLoader().getResourceAsStream("helm-app/input_settings.gradle"),
                new FileOutputStream(settingsFile));
        IOUtils.copy(getClass().getClassLoader().getResourceAsStream("helm-app/input_main.java"),
                new FileOutputStream(entityFile));
        IOUtils.copy(getClass().getClassLoader().getResourceAsStream("helm-app/input_application.properties"),
                new FileOutputStream(propertiesFile));

        GradleRunner runner = GradleRunner.create();
        runner = runner.forwardOutput();
        runner = runner.withPluginClasspath();
        runner = runner.withProjectDir(workingDir).withArguments("buildRpm", "--stacktrace").forwardOutput();
        runner.build();

        File rpmFile = new File(workingDir, "build/distributions/demo.rpm");
        Assert.assertTrue(rpmFile.exists());

        File serviceFile = new File(workingDir, "build/systemd/services/demo-app.service");
        Assert.assertTrue(serviceFile.exists());

        String serviceDesc = IOUtils.toString(new FileInputStream(serviceFile));
        Assert.assertTrue(serviceDesc.contains("ExecStart=/var/demo-app/bin/demo run"));
    }
}
