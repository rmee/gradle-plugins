package com.github.rmee.kubectl;

import com.github.rmee.cli.base.ExecResult;
import com.github.rmee.cli.base.OutputFormat;
import com.github.rmee.cli.base.internal.ClientExecBase;
import org.gradle.api.tasks.Input;

public class KubectlExecBase extends ClientExecBase {

	protected KubectlExecSpec spec;

	protected ExecResult result;

	public KubectlExecBase() {
		setGroup("kubernetes");

		spec = createSpec();
	}

	protected KubectlExecSpec createSpec() {
		return new KubectlExecSpec();
	}

	public ExecResult getResult() {
		return result;
	}

	@Input
	public OutputFormat getOutputFormat() {
		return spec.getOutputFormat();
	}

	public void setOutputFormat(OutputFormat outputFormat) {
		spec.setOutputFormat(outputFormat);
	}

	@Override
	protected KubectlExecSpec retrieveSpec() {
		return spec;
	}
}
