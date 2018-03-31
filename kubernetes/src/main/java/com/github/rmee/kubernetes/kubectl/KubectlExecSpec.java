package com.github.rmee.kubernetes.kubectl;

import com.github.rmee.kubernetes.common.OutputFormat;

public class KubectlExecSpec {

	protected String commandLine;

	protected boolean ignoreExitValue = false;

	protected OutputFormat outputFormat = OutputFormat.CONSOLE;

	protected String input;

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

	public OutputFormat getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(OutputFormat outputFormat) {
		this.outputFormat = outputFormat;
	}

	public KubectlExecSpec duplicate() {
		KubectlExecSpec duplicate = new KubectlExecSpec();
		duplicate.commandLine = commandLine;
		duplicate.ignoreExitValue = ignoreExitValue;
		duplicate.outputFormat = outputFormat;
		return duplicate;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}
}
