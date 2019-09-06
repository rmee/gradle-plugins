package com.github.rmee.kubectl;

import com.github.rmee.cli.base.CliExecSpec;

public class KubectlExecSpec extends CliExecSpec<KubectlExecSpec> {


	protected String input;

	@Override
	protected void duplicate(KubectlExecSpec duplicate) {
		duplicate.input = input;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}
}
