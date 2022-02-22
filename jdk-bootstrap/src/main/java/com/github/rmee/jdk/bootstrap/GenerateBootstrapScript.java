package com.github.rmee.jdk.bootstrap;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

class GenerateBootstrapScript implements Action<Task> {

	private final JdkBootstrapExtension extension;

	public GenerateBootstrapScript(JdkBootstrapExtension extension) {
		this.extension = extension;

	}

	@Override
	public void execute(Task task) {
		Project project = task.getProject().getRootProject();
		patchShellScript(project);
		patchBatchScript(project);
	}

	private void patchShellScript(Project project) {
		patchScript(
				project,
				"gradlew",
				"# Determine the Java command to use to start the JVM.",
				"bootstrap.sh.template",
				"\n",
				s -> s.replace("#!/usr/bin/env sh", "#!/bin/bash")
		);
	}

	private void patchBatchScript(Project project) {
		patchScript(
				project,
				"gradlew.bat",
				"@rem Find java.exe",
				"bootstrap.bat.template",
				"\r\n",
				s -> s.replace("\n|\r\n", "\r\n")
		);
	}

	private void patchScript(
			Project project,
			String filename,
			String offsetMarkerText,
			String templateName,
			String lineSeparator,
			Function<String, String> otherChanges
	) {
		File projectDir = project.getProjectDir();
		File wrapperFile = new File(projectDir.getAbsoluteFile(), filename);
		try {
			String script = FileUtils.readFileToString(wrapperFile, StandardCharsets.UTF_8);
			int sep = script.indexOf(offsetMarkerText);
			if (sep == -1) {
				throw new IllegalStateException("unknown gradlew format, failed to find '" +
						offsetMarkerText + "' to setup JDK bootstrapping");
			}

			String bootstrapSnippet = generateBootstrapSnippet(templateName);

			String updatedScript = script.substring(0, sep) +
					bootstrapSnippet + lineSeparator +
					script.substring(sep);

			updatedScript = otherChanges.apply(updatedScript);

			FileUtils.write(wrapperFile, updatedScript, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private String generateBootstrapSnippet(String templateName) {
		String template = extension.getUrlTemplate()
				.replace("${env}", "JDK_ENV")
				.replace("${os}", "JDK_OS")
				.replace("${suffix}", "JDK_DIST_SUFFIX")
				.replace("${version}", extension.getVersion());

		return loadResource(templateName)
				.replace("${JDK_DOWNLOAD_URL_TEMPLATE}", template)
				.replace("${JDK_VENDOR_TEMPLATE}", extension.getVendor())
				.replace("${JDK_VERSION_TEMPLATE}", extension.getVersion())
				.replace("${OSX_NAME_TEMPLATE}", extension.getOsxName())
				.replace("${LINUX_NAME_TEMPLATE}", extension.getLinuxName())
				.replace("${WINDOWS_NAME_TEMPLATE}", extension.getWindowsName());
	}

	private String loadResource(String name) {
		try (InputStream resource = getClass().getClassLoader().getResourceAsStream(name)) {
			if (resource == null) {
				throw new UncheckedIOException(new FileNotFoundException("Resource not found: " + name));
			}
			return IOUtils.toString(resource, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
