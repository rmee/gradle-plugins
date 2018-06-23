package com.github.rmee.az;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;

public class AzExec extends DefaultTask {

	private String command;

	protected boolean captureOutput = false;

	protected String output;

	public AzExec() {
		setGroup("provision");
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	@TaskAction
	public void run() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Project project = getProject();
		project.exec(execSpec -> {
			File azureConfigDir = new File(getProject().getBuildDir(), ".azure");
			azureConfigDir.mkdirs();

			AzExtension extension = project.getExtensions().getByType(AzExtension.class);
			File kubeDir = extension.getAks().getKubeDir();
			kubeDir.mkdirs();

			String[] commandLine = String.format("docker run -v %s:/root/.azure/ -v %s:/root/.kube/ microsoft/azure-cli %s",
					azureConfigDir.getAbsolutePath(), kubeDir.getAbsolutePath(), command).split("\\s");
			execSpec.setCommandLine(Arrays.asList(commandLine));

			if (captureOutput) {
				execSpec.setStandardOutput(outputStream);
			}
		});

		if (captureOutput) {
			output = outputStream.toString();
		}
	}
}
