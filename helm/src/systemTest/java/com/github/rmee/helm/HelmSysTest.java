package com.github.rmee.helm;

import org.gradle.internal.impldep.com.amazonaws.util.IOUtils;
import org.gradle.internal.impldep.org.testng.Assert;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class HelmSysTest {

	private File workingDir;

	@Test
	public void testPackaging() throws IOException, InterruptedException {
		File tempDir = new File("build/tmp/systest");
		tempDir.mkdirs();

		workingDir = new File(tempDir, "demo");
		workingDir.mkdirs();

		File chartFolder = new File(workingDir, "src/main/helm/helmapp");
		File remplateFolder = new File(chartFolder, "templates");
		remplateFolder.mkdirs();

		System.setProperty("org.gradle.daemon", "false");

		File gradleFile = new File(workingDir, "build.gradle");
		File tillerFile = new File(workingDir, "tiller-template.yaml");
		File chartFile = new File(chartFolder, "Chart.yaml");
		File valuesFile = new File(chartFolder, "values.yaml");
		File serviceFile = new File(remplateFolder, "service.yaml");
		File tplFile = new File(remplateFolder, "_helpers.tpl");

		Assert.assertNotNull(getClass().getClassLoader().getResource("plugin-under-test-metadata.properties"));

		IOUtils.copy(getClass().getClassLoader().getResourceAsStream("build.gradle"),
				new FileOutputStream(gradleFile));
		IOUtils.copy(getClass().getClassLoader().getResourceAsStream("Chart.yaml"),
				new FileOutputStream(chartFile));
		IOUtils.copy(getClass().getClassLoader().getResourceAsStream("values.yaml"),
				new FileOutputStream(valuesFile));
		IOUtils.copy(getClass().getClassLoader().getResourceAsStream("service.yaml"),
				new FileOutputStream(serviceFile));
		IOUtils.copy(getClass().getClassLoader().getResourceAsStream("_helpers.tpl"),
				new FileOutputStream(tplFile));
		IOUtils.copy(getClass().getClassLoader().getResourceAsStream("tiller-template.yaml"),
				new FileOutputStream(tillerFile));

		try {
			GradleRunner runner = GradleRunner.create();
			runner = runner.forwardOutput();
			runner = runner.withPluginClasspath();
			runner = runner.withProjectDir(workingDir).withArguments("ocSetup", "--stacktrace").forwardOutput();
			runner.build();

			// wait with oc client
			runner = GradleRunner.create();
			runner = runner.forwardOutput();
			runner = runner.withPluginClasspath();
			runner = runner.withProjectDir(workingDir).withArguments("ocWaitForTiller", "--stacktrace").forwardOutput();
			runner.build();

			// install software
			runner = GradleRunner.create();
			runner = runner.forwardOutput();
			runner = runner.withPluginClasspath();
			runner = runner.withProjectDir(workingDir).withArguments("helmInstall", "--stacktrace").forwardOutput();
			runner.build();

			//  wait for tiller again (with kubectl client for testing purposes)
			// TODO replacce with something meaningful => setup proper app
			runner = GradleRunner.create();
			runner = runner.forwardOutput();
			runner = runner.withPluginClasspath();
			runner = runner.withProjectDir(workingDir).withArguments("kubectlWaitForTiller", "--stacktrace").forwardOutput();
			runner.build();

			File helmPackageFile = new File(workingDir, "build/distributions/helmapp-0.1.0.tgz");
			Assert.assertTrue(helmPackageFile.exists());
		} finally {
			/*
			GradleRunner runner = GradleRunner.create();
			runner = runner.forwardOutput();
			runner = runner.withPluginClasspath();
			runner = runner.withProjectDir(workingDir).withArguments("ocDeleteProject", "--stacktrace").forwardOutput();
			runner.build();
			*/
		}
	}
}
