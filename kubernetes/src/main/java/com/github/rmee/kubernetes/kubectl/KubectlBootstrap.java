package com.github.rmee.kubernetes.kubectl;

import com.github.rmee.kubernetes.common.ClientBootstrapBase;

public class KubectlBootstrap extends ClientBootstrapBase {

	public KubectlBootstrap() {
		super(KubectlExtension.class);
	}

	@Override
	protected boolean isCompressed() {
		return false;
	}
}
