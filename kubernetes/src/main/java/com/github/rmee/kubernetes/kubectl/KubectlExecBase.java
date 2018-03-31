package com.github.rmee.kubernetes.kubectl;

import com.github.rmee.kubernetes.common.OutputFormat;
import org.gradle.api.DefaultTask;

public class KubectlExecBase extends DefaultTask {

	protected KubectlExecSpec spec;

	protected KubectlExecResult result;

	public KubectlExecBase() {
		setGroup("kubernetes");

		spec = createSpec();
	}

	protected KubectlExecSpec createSpec() {
		return new KubectlExecSpec();
	}

	public KubectlExecResult getResult() {
		return result;
	}

	public boolean isIgnoreExitValue() {
		return spec.isIgnoreExitValue();
	}

	public void setIgnoreExitValue(boolean ignoreExitValue) {
		spec.setIgnoreExitValue(ignoreExitValue);
	}

	public String getCommandLine() {
		return spec.getCommandLine();
	}

	public void setCommandLine(String commandLine) {
		spec.setCommandLine(commandLine);
	}

	public OutputFormat getOutputFormat() {
		return spec.getOutputFormat();
	}

	public void setOutputFormat(OutputFormat outputFormat) {
		spec.setOutputFormat(outputFormat);
	}
}
