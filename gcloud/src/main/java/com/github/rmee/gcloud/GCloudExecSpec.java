package com.github.rmee.gcloud;

import com.github.rmee.common.ClientExecSpec;

public class GCloudExecSpec extends ClientExecSpec<GCloudExecSpec> {

	@Override
	protected GCloudExecSpec newSpec() {
		return new GCloudExecSpec();
	}

	@Override
	protected void duplicate(GCloudExecSpec duplicate) {
		// nothing to do
	}
}
