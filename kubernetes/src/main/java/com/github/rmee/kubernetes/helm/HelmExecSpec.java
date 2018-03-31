package com.github.rmee.kubernetes.helm;

public class HelmExecSpec {

	private String commandLine;

	private boolean ignoreExitValue = false;

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

	public HelmExecSpec duplicate() {
		HelmExecSpec duplicate = new HelmExecSpec();
		duplicate.commandLine = commandLine;
		duplicate.ignoreExitValue = ignoreExitValue;
		return duplicate;
	}
}
