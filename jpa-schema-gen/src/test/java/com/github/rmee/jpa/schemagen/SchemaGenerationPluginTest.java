package com.github.rmee.jpa.schemagen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.gradle.internal.impldep.com.amazonaws.util.IOUtils;
import org.gradle.internal.impldep.org.apache.commons.io.FileUtils;
import org.gradle.internal.impldep.org.testng.Assert;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class SchemaGenerationPluginTest {

	public TemporaryFolder testFolder;

	private File workingDir;

	@Before
	public void setup() {
		File tmpDir = new File("build/tmp");
		tmpDir.mkdirs();
		testFolder = new TemporaryFolder(tmpDir);
	}

	@Test
	public void checkFlyway() throws IOException {
		check(false);

		File expectedSql = new File(workingDir, "expected_create.sql");
		IOUtils.copy(getClass().getClassLoader().getResourceAsStream("basic-creation/expected_create.sql"),
				new FileOutputStream(expectedSql));
		Assert.assertTrue(
				FileUtils.contentEqualsIgnoreEOL(
						expectedSql,
						new File(workingDir, "build/generated/source/schema/main/example/current/v0.0.1.200__create_tables.sql"),
						"UTF-8"
				)
		);
	}


	@Test
	public void checkLiquibase() throws IOException {
		check(true);

		File expectedFile = new File(workingDir, "build/generated/source/schema/main/example/liquibase-changelog.xml");
		Assert.assertTrue(expectedFile.exists());
	}

	private void check(boolean liquibase) throws IOException {
		testFolder.create();
		workingDir = testFolder.getRoot();

		File javaFolder = testFolder.newFolder("src", "main", "java", "example");
		File resourceFolder = testFolder.newFolder("src", "main", "resources");
		File metaInfFolder = testFolder.newFolder("src", "main", "resources", "META-INF");

		System.setProperty("org.gradle.daemon", "false");

		File gradleFile = testFolder.newFile("build.gradle");
		File entityFile = new File(javaFolder, "ExampleEntity.java");
		File persistenceFile = new File(metaInfFolder, "persistence.xml");
		File exampleResource = new File(resourceFolder, "example_config.properties");

		// make sure to run gradle first
		Assert.assertNotNull(getClass().getClassLoader().getResource("plugin-under-test-metadata.properties"));

		IOUtils.copy(getClass().getClassLoader().getResourceAsStream(
				liquibase ? "basic-creation/input_liquibase.gradle" : "basic-creation/input_flyway.gradle"),
				new FileOutputStream(gradleFile));
		IOUtils.copy(getClass().getClassLoader().getResourceAsStream("basic-creation/input_persistence.xml"),
				new FileOutputStream(persistenceFile));
		IOUtils.copy(getClass().getClassLoader().getResourceAsStream("basic-creation/input_entity.java"),
				new FileOutputStream(entityFile));
		IOUtils.copy(getClass().getClassLoader().getResourceAsStream("basic-creation/input_example_config.properties"),
				new FileOutputStream(exampleResource));

		GradleRunner runner = GradleRunner.create();
		runner = runner.withPluginClasspath();
		runner = runner.withProjectDir(workingDir).withArguments("build", "--stacktrace").forwardOutput();
		runner.build();

		File exampleResourceOutputFile = new File(workingDir, "build/classes/java/main/example_config.properties");
		Assert.assertTrue(exampleResourceOutputFile.exists());
	}

}
