package com.github.rmee.jpa.schemagen;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.github.rmee.jpa.schemagen.internal.FileUtils;
import com.github.rmee.jpa.schemagen.internal.SchemaTarget;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GenerateSchemaTask extends DefaultTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(GenerateSchemaTask.class);

	private FileCollection classes;

	private Configuration dependencies;

	public static final String NAME = "generateSchema";

	public GenerateSchemaTask() {
		//
		getLogging().setLevel(LogLevel.QUIET);
		setGroup("generation");
		setDescription("generate DDLs from entities with Hibernate");
	}

	@InputFiles
	@SkipWhenEmpty
	FileCollection getClasses() {
		return classes;
	}

	void setClasses(FileCollection classes) {
		this.classes = classes;
	}

	@OutputDirectory
	public File getOutputDirectory() {
		Project project = getProject();
		return new File(project.getBuildDir(), "generated/source/schema/main/");
	}

	@InputFiles
	Configuration getDependencies() {
		return dependencies;
	}

	void setDependencies(Configuration dependencies) {
		this.dependencies = dependencies;
	}

	@TaskAction
	public void generate() {
		SchemaGenExtension config = getConfig();

		Thread thread = Thread.currentThread();
		ClassLoader contextClassLoader = thread.getContextClassLoader();

		processSchemaRelatedResources();

		try {
			File tempFile = File.createTempFile("ddl", ".sql");
			try (URLClassLoader classloader = getProjectClassLoader(contextClassLoader)) {
				Properties persistenceProperties = new Properties();
				if (config.getDialect() != null) {
					persistenceProperties.setProperty("hibernate.dialect", config.getDialect());
				}
				persistenceProperties.setProperty("hibernate.hbm2ddl.delimiter", ";");
				persistenceProperties.setProperty("hibernate.format_sql", "true");
				persistenceProperties.setProperty("hibernate.hbm2ddl.auto", "none");
				persistenceProperties.setProperty("javax.persistence.schema-generation.scripts.action", "create");
				persistenceProperties
						.setProperty("javax.persistence.schema-generation.scripts.create-target", tempFile.getAbsolutePath());

				// execution in a separate thread as Hibernate searches the thread context classloader
				Thread generator = new Thread(() -> {
					URL resourceUrl = classloader.getResource("META-INF/persistence.xml");
					if (resourceUrl == null) {
						throw new IllegalStateException("META-INF/persistence.xml not found on " + config.getConfiguration() +
								" classpath: " + getProjectClassPathEntries());
					}

					try {
						Class persistenceClass = classloader.loadClass("javax.persistence.Persistence");
						String methodName = "generateSchema"; //NOSONAR NAME global variable refers to task name
						Method generateSchema = persistenceClass.getMethod(methodName, String.class, Map.class);
						generateSchema.invoke(persistenceClass, config.getPersistenceUnitName(), persistenceProperties);
					}
					catch (Exception e) {
						throw new IllegalStateException(e);
					}

					SchemaTargetType target = config.getTarget();
					SchemaTarget schemaTarget;
					if (target == SchemaTargetType.FLYWAY) {
						schemaTarget = newInstance("com.github.rmee.jpa.schemagen.internal.FlywaySchemaTarget");
					}
					else {
						schemaTarget = newInstance("com.github.rmee.jpa.schemagen.internal.LiquibaseSchemaTarget");
					}
					schemaTarget.process(tempFile, getOutputDirectory(), config);
				});
				generator.setContextClassLoader(classloader);
				generator.start();
				generator.join();
			}

			FileUtils.delete(tempFile);
		}
		catch (Exception e) {
			throw new IllegalStateException("failed to generate DDLs", e);
		}
	}

	private SchemaTarget newInstance(String name) {
		try {
			return (SchemaTarget) Class.forName(name).newInstance();
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * persistence.xml must sit next to classes in order for auto-discovery to work. This is a bit a problem
	 * as DDLs must be registered as resource, but itself is in need for both classes and resources
	 * sitting next to other in the output directory (cyclic dependency). For this reason, this takes
	 * copies the persistence.xml to the output directory, which would otherwise be done by processResources later.
	 * <p>
	 * seems like the best among a number of bad solutions.
	 */
	private void processSchemaRelatedResources() {
		copyResource("META-INF/persistence.xml", false);
	}

	private void copyResource(String name, boolean optional) {
		SourceSet sourceSet = getMainSourceSet();
		SourceDirectorySet resources = sourceSet.getResources();

		File outputDir = sourceSet.getOutput().getResourcesDir();

		File outputResourceFile = new File(outputDir, name);
		File inputResourceFile = getInputResource(resources, name);

		if (optional && inputResourceFile == null) {
			throw new IllegalStateException("no " + name + " found in " + resources.getSrcDirs());
		}
		else if (inputResourceFile != null && hasChanged(inputResourceFile, outputResourceFile)) {
			outputResourceFile.getParentFile().mkdirs();
			Project project = getProject();
			project.copy(copySpec -> {
				copySpec.from(inputResourceFile);
				copySpec.into(outputResourceFile.getParentFile());
			});
		}
	}

	private static boolean hasChanged(File inputResourceFile, File outputResourceFile) {
		return !outputResourceFile.exists() || outputResourceFile.lastModified() < inputResourceFile.lastModified()
				|| outputResourceFile.length() != inputResourceFile.length();
	}

	private static File getInputResource(SourceDirectorySet resources, String name) {
		File inputResourceFile = null;
		for (File resourceDir : resources.getSrcDirs()) {
			File candidateFile = new File(resourceDir, name);
			boolean exists = candidateFile.exists();
			if (exists && inputResourceFile == null) {
				inputResourceFile = candidateFile;
			}
			else if (exists) {
				throw new IllegalStateException(
						"duplicate " + name + ": " + candidateFile.getAbsolutePath() + " vs " + inputResourceFile
								.getAbsolutePath());
			}
		}
		return inputResourceFile;
	}

	private SchemaGenExtension getConfig() {
		Project project = getProject();
		return project.getExtensions().getByType(SchemaGenExtension.class);
	}


	private SourceSet getMainSourceSet() {
		Project project = getProject();
		SourceSetContainer sourceSets = (SourceSetContainer) project.getProperties().get("sourceSets");
		return sourceSets.getByName("main");
	}

	private Set<File> getProjectClassPathEntries() {
		Set<File> classpath = new HashSet<>();
		classpath.addAll(getClasses().getFiles());
		classpath.addAll(getMainSourceSet().getResources().getSrcDirs());
		return classpath;
	}

	private Set<File> getClassPathEntries() {
		Set<File> classpath = new HashSet<>();
		classpath.addAll(getDependencies().getFiles());
		classpath.addAll(getProjectClassPathEntries());
		LOGGER.debug("schemaGen classpath: {}", classpath);
		return classpath;
	}

	/**
	 * Build a classloader so we can do ddls generations with all the application entities in the classpath.
	 */
	public URLClassLoader getProjectClassLoader(ClassLoader parentClassLoader) {
		Set<File> classFiles = getClassPathEntries();

		// convert to url
		URL[] classURLs = classFiles.stream().map(it -> {
			try {
				return it.toURI().toURL();
			}
			catch (MalformedURLException e) {
				throw new IllegalStateException();
			}
		}).toArray(URL[]::new);


		return new URLClassLoader(classURLs, parentClassLoader);
	}

}
