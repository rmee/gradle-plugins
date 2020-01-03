package com.github.rmee.jdk.bootstrap;


import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;

class GenerateBootstrapScript implements Action<Task> {

	private final JdkBootstrapExtension extension;

	public GenerateBootstrapScript(JdkBootstrapExtension extension) {
		this.extension = extension;

	}

	@Override
	public void execute(Task task) {
		Project project = task.getProject().getRootProject();
		patchShellScript(project);

	}

	private void patchShellScript(Project project) {
		File projectDir = project.getProjectDir();
		File wrapperFile = new File(projectDir.getAbsoluteFile(), "gradlew");
		try {
			String script = FileUtils.readFileToString(wrapperFile);
			int sep = script.indexOf("# Determine the Java command to use to start the JVM.");
			if (sep == -1) {
				throw new IllegalStateException("unknown gradlew format, failed to find '# Determine the Java command to use to start the JVM.' to setup JDK bootstrapping");
			}
			String bootstrapScript = generateBootstrapScript();

			String updatedScript = script.substring(0, sep) + bootstrapScript + '\n' + script.substring(sep);

			updatedScript = updatedScript.replace("#!/usr/bin/env sh", "#!/bin/bash");

			FileUtils.write(wrapperFile, updatedScript);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private String generateBootstrapScript() throws IOException {
		String bootstrapSnipped = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("bootstrap.sh.template"));

		String template = extension.getUrlTemplate();
		template = template.replace("${env}", "JDK_ENV");
		template = template.replace("${os}", "JDK_OS");
		template = template.replace("${suffix}", "JDK_DIST_SUFFIX");
		template = template.replace("${version}", extension.getVersion());

		bootstrapSnipped = bootstrapSnipped.replace("${JDK_DOWNLOAD_URL_TEMPLATE}", template);
		bootstrapSnipped = bootstrapSnipped.replace("${JDK_VERSION_TEMPLATE}", extension.getVersion());
		bootstrapSnipped = bootstrapSnipped.replace("${OSX_NAME_TEMPLATE}", extension.getOsxName());
		bootstrapSnipped = bootstrapSnipped.replace("${LINUX_NAME_TEMPLATE}", extension.getLinuxName());
		bootstrapSnipped = bootstrapSnipped.replace("${WINDOWS_NAME_TEMPLATE}", extension.getWindowsName());
		return bootstrapSnipped;
	}
}
