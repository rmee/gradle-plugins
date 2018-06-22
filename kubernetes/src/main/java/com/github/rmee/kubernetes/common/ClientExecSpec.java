package com.github.rmee.kubernetes.common;

import java.io.File;

public class ClientExecSpec {

	private String commandLine;

	private boolean ignoreExitValue = false;

	private File stdoutFile;

	public boolean isIgnoreExitValue() {
		return ignoreExitValue;
	}

	public void setIgnoreExitValue(boolean ignoreExitValue) {
		this.ignoreExitValue = ignoreExitValue;
	}

	public String getCommandLine() {
		return commandLine;
	}

	public void setCommandLine(String commandLine) {
		this.commandLine = commandLine;
	}

	public ClientExecSpec duplicate() {
		ClientExecSpec duplicate = new ClientExecSpec();
		duplicate.commandLine = commandLine;
		duplicate.ignoreExitValue = ignoreExitValue;
		return duplicate;
	}

	public File getStdoutFile() {
		return stdoutFile;
	}

	public void setStdoutFile(File stdoutFile) {
		this.stdoutFile = stdoutFile;
	}
}
