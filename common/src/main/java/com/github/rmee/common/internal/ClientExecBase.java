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
		return retrieveSpec().isIgnoreExitValue();
	}

	protected abstract ClientExecSpec retrieveSpec();


	public void setIgnoreExitValue(boolean ignoreExitValue) {
		retrieveSpec().setIgnoreExitValue(ignoreExitValue);
	}

	@Input
	public List<String> getCommandLine() {
		return retrieveSpec().getCommandLine();
	}

	public void setCommandLine(String commandLine) {
		retrieveSpec().setCommandLine(commandLine);
	}

	public void setCommandLine(List<String> commandLine) {
		retrieveSpec().setCommandLine(commandLine);
	}

	@InputFile
	public File getStdoutFile() {
		return retrieveSpec().getStdoutFile();
	}

	public void setStdoutFile(File stdoutFile) {
		retrieveSpec().setStdoutFile(stdoutFile);
	}
}