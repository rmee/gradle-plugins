package com.github.rmee.helm;

import com.github.rmee.common.internal.ClientCleanTask;

public class HelmClean extends ClientCleanTask {

	public HelmClean() {
		super(HelmExtension.class);
	}
}
