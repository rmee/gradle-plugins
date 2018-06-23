package com.github.rmee.common;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ClientExecSpec<T extends ClientExecSpec> {

	private List<String> commandLine;

	private boolean ignoreExitValue = false;

	private File stdoutFile;

	public boolean isIgnoreExitValue() {
		return ignoreExitValue;
	}

	public void setIgnoreExitValue(boolean ignoreExitValue) {
		this.ignoreExitValue = ignoreExitValue;
	}

	public List<String> getCommandLine() {
		return commandLine;
	}

	public void setCommandLine(String commandLine) {
		this.commandLine = Arrays.asList(commandLine.split("\\s+"));
	}

	public void setCommandLine(List<String> commandLine) {
		this.commandLine = commandLine;
	}

	public final T duplicate() {
		ClientExecSpec duplicate = newSpec();
		duplicate.commandLine = new ArrayList(commandLine);
		duplicate.ignoreExitValue = ignoreExitValue;
		duplicate((T) duplicate);
		return (T) duplicate;
	}

	protected abstract T newSpec();

	protected abstract void duplicate(T duplicate);

	public File getStdoutFile() {
		return stdoutFile;
	}

	public void setStdoutFile(File stdoutFile) {
		this.stdoutFile = stdoutFile;
	}
}
