package com.github.rmee.jpa.schemagen;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.rmee.jpa.schemagen.internal.SchemaGenerator;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;


public class GenerateSchemaTask extends DefaultTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(GenerateSchemaTask.class);

	private FileCollection classes;

	private Configuration dependencies;

	public static final String NAME = "generateSchema";

	public GenerateSchemaTask() {
		// crashes gradle with NPE: getLogging().setLevel(LogLevel.QUIET);
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
		SchemaGenExtension config = getConfig();
		if (config.getOutputDirectory() != null) {
			return config.getOutputDirectory();
		}
		if (config.isContinuousMode()) {
			return new File(project.getBuildDir(), "resources/main/");
		}
		return new File(project.getProjectDir(), "src/main/resources");
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
			if (config.isForked()) {
				File buildDir = getProject().getBuildDir();
				buildDir.mkdirs();
				try (FileOutputStream out = new FileOutputStream(new File(buildDir, "schema-gen.log"))) {

					Project project = getProject();
					project.javaexec(javaExecSpec -> {
						ObjectMapper mapper = new ObjectMapper();
						ObjectWriter writer = mapper.writerFor(SchemaGenConfig.class);
						String json;
						try {
							json = writer.writeValueAsString(config);
						} catch (JsonProcessingException e) {
							throw new IllegalStateException(e);
						}
						javaExecSpec.setClasspath(project.files(getClassPathEntries()));
						javaExecSpec.setMain("com.github.rmee.jpa.schemagen.internal.SchemaGenerator");
						javaExecSpec.args(getOutputDirectory().getAbsoluteFile(), Base64.getEncoder().encodeToString(json.getBytes()));

						javaExecSpec.setStandardOutput(out);
						javaExecSpec.setErrorOutput(out);
					});
				}
			} else {
				try (URLClassLoader classloader = getProjectClassLoader(contextClassLoader)) {
					SchemaGenerator gen = new SchemaGenerator();
					gen.run(config, classloader, getOutputDirectory());
				}
			}
		} catch (Exception e) {
			Throwable cause = e.getCause();
			throw new IllegalStateException("failed to generate DDLs: " + e.getMessage() + (cause != null ? ". Caused by " + cause.getMessage() : "") + ". Further information may can be found in build/schema-gen.log.", e);
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
		} else if (inputResourceFile != null && hasChanged(inputResourceFile, outputResourceFile)) {
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
			} else if (exists) {
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

	@Input
	public Set<File> getProjectClassPathEntries() {
		Set<File> classpath = new HashSet<>();
		classpath.addAll(getClasses().getFiles());
		classpath.addAll(getGradleEntries());
		classpath.addAll(getMainSourceSet().getResources().getSrcDirs());
		return classpath;
	}

	private Set<File> getGradleEntries() {
		Set<File> classpath = new HashSet<>();
		URLClassLoader classLoader = (URLClassLoader) getClass().getClassLoader();
		for (URL gradleClassUrl : classLoader.getURLs()) {
			String file = gradleClassUrl.getFile();
			if (file.contains("jpa-schema-gen") || file.contains("jackson")) {
				classpath.add(toFile(gradleClassUrl));
			}
		}
		return classpath;
	}


	private File toFile(URL url) {
		try {
			return Paths.get(url.toURI()).toFile();
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	private Set<File> getClassPathEntries() {
		Set<File> classpath = new HashSet<>();
		classpath.addAll(getDependencies().getFiles());
		classpath.addAll(getProjectClassPathEntries());
		classpath.addAll(getGradleEntries());
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
			} catch (MalformedURLException e) {
				throw new IllegalStateException();
			}
		}).toArray(URL[]::new);


		return new URLClassLoader(classURLs, parentClassLoader);
	}

}
