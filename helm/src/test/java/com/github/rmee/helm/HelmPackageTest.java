package com.github.rmee.helm;

import org.apache.commons.io.FileUtils;
import org.gradle.internal.impldep.com.amazonaws.util.IOUtils;
import org.gradle.internal.impldep.org.testng.Assert;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HelmPackageTest {

	private File workingDir;

	@Before
	public void setup() throws IOException {
		File tempDir = new File("build/tmp/helm");
		tempDir.mkdirs();

		workingDir = new File(tempDir, "demo");
		workingDir.mkdirs();

		File chartFolder = new File(workingDir, "src/main/helm/helmapp");
		File remplateFolder = new File(chartFolder, "templates");
		remplateFolder.mkdirs();

		System.setProperty("org.gradle.daemon", "false");

		File gradleFile = new File(workingDir, "build.gradle");
		File chartFile = new File(chartFolder, "Chart.yaml");
		File valuesFile = new File(chartFolder, "values.yaml");
		File serviceFile = new File(remplateFolder, "service.yaml");

		ClassLoader cl = getClass().getClassLoader();
		Assert.assertNotNull(cl.getResource("plugin-under-test-metadata.properties"));

		File settingsFile = new File(workingDir, "settings.gradle");
		FileUtils.write(settingsFile, "", StandardCharsets.UTF_8);
		IOUtils.copy(cl.getResourceAsStream("build.gradle"), new FileOutputStream(gradleFile));
		IOUtils.copy(cl.getResourceAsStream("Chart.yaml"), new FileOutputStream(chartFile));
		IOUtils.copy(cl.getResourceAsStream("values.yaml"), new FileOutputStream(valuesFile));
		IOUtils.copy(cl.getResourceAsStream("service.yaml"), new FileOutputStream(serviceFile));

	}

	@Test
	public void testGenericExec() throws IOException {
		GradleRunner runner = GradleRunner.create();
		runner = runner.forwardOutput();
		runner = runner.withPluginClasspath();
		runner = runner.withProjectDir(workingDir).withArguments("testHelmGeneric", "--stacktrace").forwardOutput();
		runner.build();
	}

	@Test
	public void testPackaging() throws IOException {
		GradleRunner runner = GradleRunner.create();
		runner = runner.forwardOutput();
		runner = runner.withPluginClasspath();
		runner = runner.withProjectDir(workingDir).withArguments("helmPackage", "--stacktrace").forwardOutput();
		runner.build();

		File helmFile = new File(workingDir, "build/helm/helmapp-0.1.0.tgz");
		Assert.assertTrue(helmFile.exists());
	}
}
