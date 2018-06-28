package com.github.rmee.kubectl;

import com.github.rmee.common.ClientBootstrapBase;

public class KubectlBootstrap extends ClientBootstrapBase {

	public KubectlBootstrap() {
		super(KubectlExtension.class);
	}

	@Override
	protected boolean isCompressed() {
		return false;
	}
}
