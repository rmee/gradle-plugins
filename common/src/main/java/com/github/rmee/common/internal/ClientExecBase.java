package com.github.rmee.common.internal;

import com.github.rmee.common.ClientExecSpec;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;

import java.io.File;
import java.util.List;

public abstract class ClientExecBase extends DefaultTask {

	@Input
	public boolean isIgnoreExitValue() {
		return getSpec().isIgnoreExitValue();
	}

	protected abstract ClientExecSpec getSpec();


	public void setIgnoreExitValue(boolean ignoreExitValue) {
		getSpec().setIgnoreExitValue(ignoreExitValue);
	}

	@Input
	public List<String> getCommandLine() {
		return getSpec().getCommandLine();
	}

	public void setCommandLine(String commandLine) {
		getSpec().setCommandLine(commandLine);
	}

	public void setCommandLine(List<String> commandLine) {
		getSpec().setCommandLine(commandLine);
	}

	@InputFile
	public File getStdoutFile() {
		return getSpec().getStdoutFile();
	}

	public void setStdoutFile(File stdoutFile) {
		getSpec().setStdoutFile(stdoutFile);
	}
}