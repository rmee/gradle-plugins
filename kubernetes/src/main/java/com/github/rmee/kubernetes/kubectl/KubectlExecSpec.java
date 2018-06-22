package com.github.rmee.kubernetes.kubectl;

import com.github.rmee.kubernetes.common.ClientExecSpec;
import com.github.rmee.kubernetes.common.OutputFormat;

public class KubectlExecSpec extends ClientExecSpec {

	protected OutputFormat outputFormat = OutputFormat.CONSOLE;

	protected String input;

	public OutputFormat getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(OutputFormat outputFormat) {
		this.outputFormat = outputFormat;
	}

	public KubectlExecSpec duplicate() {
		KubectlExecSpec duplicate = new KubectlExecSpec();
		duplicate.setCommandLine(getCommandLine());
		duplicate.setIgnoreExitValue(isIgnoreExitValue());
		duplicate.setStdoutFile(getStdoutFile());
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
