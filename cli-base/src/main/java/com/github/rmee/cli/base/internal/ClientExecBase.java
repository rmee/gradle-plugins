package com.github.rmee.cli.base.internal;

import com.github.rmee.cli.base.CliExecSpec;
import groovy.lang.Closure;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;

import java.io.File;
import java.util.List;

public abstract class ClientExecBase extends DefaultTask {

	@Input
	public boolean isIgnoreExitValue() {
		return retrieveSpec().isIgnoreExitValue();
	}

	protected abstract CliExecSpec retrieveSpec();


	public void setIgnoreExitValue(boolean ignoreExitValue) {
		retrieveSpec().setIgnoreExitValue(ignoreExitValue);
	}

	public List<String> getCommandLine() {
		return retrieveSpec().getCommandLine();
	}

	public void commandLine(String commandLine) {
		retrieveSpec().setCommandLine(commandLine);
	}

	public void commandLine(Closure closure) {
	}

	public void commandLine(List<String> commandLine) {
		retrieveSpec().setCommandLine(commandLine);
	}

	public File getStdoutFile() {
		return retrieveSpec().getStdoutFile();
	}

	public void setStdoutFile(File stdoutFile) {
		retrieveSpec().setStdoutFile(stdoutFile);
	}
}