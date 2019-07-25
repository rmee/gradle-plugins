package com.github.rmee.helm;

import com.github.rmee.cli.base.CliExecSpec;

public class HelmExecSpec extends CliExecSpec<HelmExecSpec> {

	@Override
	protected HelmExecSpec newSpec() {
		return new HelmExecSpec();
	}

	@Override
	protected void duplicate(HelmExecSpec duplicate) {
		// nothing to do
	}
}
