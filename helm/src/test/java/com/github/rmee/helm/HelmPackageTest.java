package com.github.rmee.helm;

import org.gradle.internal.impldep.com.amazonaws.util.IOUtils;
import org.gradle.internal.impldep.org.testng.Assert;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Ignore // FIXME
public class HelmPackageTest {

	private File workingDir;

	@Test
	public void testPackaging() throws IOException {
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

		IOUtils.copy(cl.getResourceAsStream("build.gradle"), new FileOutputStream(gradleFile));
		IOUtils.copy(cl.getResourceAsStream("Chart.yaml"), new FileOutputStream(chartFile));
		IOUtils.copy(cl.getResourceAsStream("values.yaml"), new FileOutputStream(valuesFile));
		IOUtils.copy(cl.getResourceAsStream("service.yaml"), new FileOutputStream(serviceFile));

		GradleRunner runner = GradleRunner.create();
		runner = runner.forwardOutput();
		runner = runner.withPluginClasspath();
		runner = runner.withProjectDir(workingDir).withArguments("helmInit", "helmPackage", "--stacktrace").forwardOutput();
		runner.build();

		File rpmFile = new File(workingDir, "build/distributions/helmapp-0.1.0.tgz");
		Assert.assertTrue(rpmFile.exists());
	}
}
