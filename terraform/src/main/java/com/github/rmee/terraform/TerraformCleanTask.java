package com.github.rmee.terraform;

import com.github.rmee.common.internal.ClientCleanTask;

public class TerraformCleanTask extends ClientCleanTask {

	public TerraformCleanTask() {
		super(TerraformExtension.class);
	}
}
