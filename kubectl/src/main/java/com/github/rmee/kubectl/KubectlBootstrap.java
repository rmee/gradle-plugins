package com.github.rmee.kubectl;

import com.github.rmee.cli.base.internal.CliBootstrapBase;

public class KubectlBootstrap extends CliBootstrapBase {

	public KubectlBootstrap() {
		super(KubectlExtension.class);
	}

	@Override
	protected boolean isCompressed() {
		return false;
	}
}
