package com.github.rmee.jpa.schemagen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.gradle.internal.impldep.com.amazonaws.util.IOUtils;
import org.gradle.internal.impldep.org.apache.commons.io.FileUtils;
import org.gradle.internal.impldep.org.testng.Assert;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class SchemaGenerationPluginTest {

	public static final String FLYWAY_ACTUAL_SCRIPT =
			"build/generated/source/schema/main/example/current/v0.0.1.200__create_tables.sql";

	public static final String LIQUIBASE_ACTUAL_CHANGELOG =
			"build/generated/source/schema/main/example/liquibase-changelog.xml";

	public TemporaryFolder testFolder;

	private File workingDir;

	@Before
	public void setup() {
		File tmpDir = new File("build/tmp");
		tmpDir.mkdirs();
		testFolder = new TemporaryFolder(tmpDir);
	}

	@Test
	public void checkBasicFlyway() throws IOException {
		check(false, "basic-creation");

		File expectedSql = new File(workingDir, "expected_create.sql");
		IOUtils.copy(getClass().getClassLoader().getResourceAsStream("basic-creation/expected_create.sql"),
				new FileOutputStream(expectedSql));
		Assert.assertTrue(
				FileUtils.contentEqualsIgnoreEOL(
						expectedSql,
						new File(workingDir, FLYWAY_ACTUAL_SCRIPT),
						"UTF-8"
				)
		);
	}

	@Test
	public void checkBasicLiquibase() throws IOException {
		check(true, "basic-creation");

		File expectedFile = new File(workingDir, LIQUIBASE_ACTUAL_CHANGELOG);
		Assert.assertTrue(expectedFile.exists());
	}

	@Test
	public void checkFilteredFlyway() throws IOException {
		check(false, "filtered-creation");

		File expectedSql = new File(workingDir, "expected_create.sql");
		IOUtils.copy(getClass().getClassLoader().getResourceAsStream("filtered-creation/expected_create.sql"),
				new FileOutputStream(expectedSql));
		File actualSql = new File(workingDir, FLYWAY_ACTUAL_SCRIPT);
		Assert.assertTrue(
				FileUtils.contentEqualsIgnoreEOL(
						expectedSql,
						actualSql,
						"UTF-8"
				),
				"The generated SQL script does not match the expectations. Expected: " + expectedSql + " actual: " + actualSql);
	}

	private void check(boolean liquibase, String example) throws IOException {
		testFolder.create();
		workingDir = testFolder.getRoot();

		File javaFolder = testFolder.newFolder("src", "main", "java", "example");
		File secondJavaPackage = testFolder.newFolder("src", "main", "java", "excluded");
		File resourceFolder = testFolder.newFolder("src", "main", "resources");
		File metaInfFolder = testFolder.newFolder("src", "main", "resources", "META-INF");

		System.setProperty("org.gradle.daemon", "false");

		File gradleFile = testFolder.newFile("build.gradle");
		File entityFile = new File(javaFolder, "ExampleEntity.java");
		File excludedFile = new File(secondJavaPackage, "ExcludedEntity.java");
		File persistenceFile = new File(metaInfFolder, "persistence.xml");
		File exampleResource = new File(resourceFolder, "example_config.properties");

		// make sure to run gradle first
		Assert.assertNotNull(getClass().getClassLoader().getResource("plugin-under-test-metadata.properties"));

		IOUtils.copy(getClass().getClassLoader().getResourceAsStream(
				example +  (liquibase ? "/input_liquibase.gradle" : "/input_flyway.gradle")),
				new FileOutputStream(gradleFile));
		IOUtils.copy(getClass().getClassLoader().getResourceAsStream(example + "/input_persistence.xml"),
				new FileOutputStream(persistenceFile));
		IOUtils.copy(getClass().getClassLoader().getResourceAsStream(example + "/input_entity.java"),
				new FileOutputStream(entityFile));
		InputStream excludedFileContents = getClass().getClassLoader().getResourceAsStream(example + "/input_excluded_entity.java");
		if(excludedFileContents != null) {
			IOUtils.copy(excludedFileContents, new FileOutputStream(excludedFile));
		}
		IOUtils.copy(getClass().getClassLoader().getResourceAsStream(example + "/input_example_config.properties"),
				new FileOutputStream(exampleResource));

		GradleRunner runner = GradleRunner.create();
		runner = runner.withPluginClasspath().withDebug(true);
		List<String> args = new ArrayList<>();
		args.add("build");
		args.add("--stacktrace");
		httpProxyArguments(args);
		runner = runner.withProjectDir(workingDir).withArguments(args).forwardOutput();
		runner.build();

		File exampleResourceOutputFile = new File(workingDir, "build/classes/java/main/example_config.properties");
		Assert.assertTrue(exampleResourceOutputFile.exists());
	}

	private void httpProxyArguments(List<String> args) {
		copySystemProperty(args, "http.proxyHost");
		copySystemProperty(args, "http.proxyPort");
		copySystemProperty(args, "https.proxyHost");
		copySystemProperty(args, "https.proxyPort");
	}

	private void copySystemProperty(List<String> args, String propertyName) {
		String propertyValue = System.getProperty(propertyName);
		if(propertyValue != null) {
			args.add("-D" + propertyName + "=" + propertyValue);
		}
	}

}
