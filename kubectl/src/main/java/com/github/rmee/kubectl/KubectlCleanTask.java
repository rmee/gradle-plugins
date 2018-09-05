package com.github.rmee.kubectl;

import com.github.rmee.common.internal.ClientCleanTask;

public class KubectlCleanTask extends ClientCleanTask {

	public KubectlCleanTask() {
		super(KubectlExtension.class);
	}
}
