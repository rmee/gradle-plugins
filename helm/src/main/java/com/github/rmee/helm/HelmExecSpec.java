package com.github.rmee.helm;

import com.github.rmee.common.ClientExecSpec;

public class HelmExecSpec extends ClientExecSpec<HelmExecSpec> {

	@Override
	protected HelmExecSpec newSpec() {
		return new HelmExecSpec();
	}

	@Override
	protected void duplicate(HelmExecSpec duplicate) {
		// nothing to do
	}
}
