package com.github.rmee.helm;

import java.io.File;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

public class HelmPackage extends DefaultTask {

	private String packageName;

	public HelmPackage() {
		setGroup("kubernetes");
	}

	@TaskAction
	public void exec() {
		HelmExtension extension = getProject().getExtensions().getByType(HelmExtension.class);

		String outputDir;
		if (extension.getClient().isDockerized()) {
			outputDir = HelmPlugin.HELM_OUTPUT_DIR;
		}
		else {
			File fileOutputDir = extension.getOutputDir();
			fileOutputDir.mkdirs();
			outputDir = fileOutputDir.getAbsolutePath();
		}

		Project project = getProject();

		HelmExecSpec spec = new HelmExecSpec();
		String sourceDir = getSourceDir().getAbsolutePath();
		spec.setCommandLine("helm package " + sourceDir + " --destination " + outputDir
				+ " --version " + project.getVersion());

		extension.exec(spec);
	}



	@InputDirectory
	public File getSourceDir() {
		HelmExtension extension = getProject().getExtensions().getByType(HelmExtension.class);
		return new File(extension.getSourceDir(), packageName);
	}

	@OutputFile
	public File getOutputFile() {
		HelmExtension extension = getProject().getExtensions().getByType(HelmExtension.class);
		return extension.getOutputFile(packageName);
	}

	@Input
	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
}
