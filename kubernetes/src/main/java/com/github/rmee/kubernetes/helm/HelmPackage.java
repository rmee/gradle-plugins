package com.github.rmee.kubernetes.helm;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFiles;
import org.gradle.api.tasks.TaskAction;

public class HelmPackage extends DefaultTask {

	@TaskAction
	public void exec() {
		HelmExtension extension = getExtension();
		File outputDir = extension.getOutputDir();
		outputDir.mkdirs();

		Project project = getProject();

		Set<String> packageNames = extension.getPackageNames();
		for (String packageName : packageNames) {
			HelmExecSpec spec = new HelmExecSpec();
			File sourceDir = extension.getPackageSourceDir(packageName);
			spec.setCommandLine("helm package " + sourceDir.getAbsolutePath() + " --destination " + outputDir.getAbsolutePath()
					+ " --version " + project.getVersion());

			extension.exec(spec);
		}
	}

	@InputDirectory
	public File getSourceDir() {
		HelmExtension extension = getExtension();
		return extension.getSourceDir();
	}

	@OutputFiles
	public Set<File> getOutputFiles() {
		HelmExtension extension = getExtension();
		Set<String> packageNames = extension.getPackageNames();
		return packageNames.stream()
				.map(name -> extension.getOutputFile(name))
				.collect(Collectors.toSet());
	}

	public HelmExtension getExtension() {
		return getProject().getExtensions().getByType(HelmExtension.class);
	}
}
