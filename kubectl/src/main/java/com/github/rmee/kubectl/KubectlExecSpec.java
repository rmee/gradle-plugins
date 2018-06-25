package com.github.rmee.kubectl;

import com.github.rmee.common.ClientExecSpec;
import com.github.rmee.common.OutputFormat;
import org.gradle.api.tasks.Input;

public class KubectlExecSpec extends ClientExecSpec<KubectlExecSpec> {

	protected OutputFormat outputFormat = OutputFormat.CONSOLE;

	protected String input;

	@Input
	public OutputFormat getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(OutputFormat outputFormat) {
		this.outputFormat = outputFormat;
	}

	@Override
	protected KubectlExecSpec newSpec() {
		return new KubectlExecSpec();
	}

	@Override
	protected void duplicate(KubectlExecSpec duplicate) {
		duplicate.outputFormat = outputFormat;
		duplicate.input = input;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}
}
