package com.github.rmee.az;

import com.github.rmee.common.ClientExecSpec;

public class AzExecSpec extends ClientExecSpec<AzExecSpec> {

	@Override
	protected AzExecSpec newSpec() {
		return new AzExecSpec();
	}

	@Override
	protected void duplicate(AzExecSpec duplicate) {
		// nothing to do
	}
}
