package com.github.rmee.terraform;

public class TerraformDestroyTask extends TerraformExec {

	public TerraformDestroyTask() {
		getSpec().setAddVariables(false);
		getSpec().setAddConfigDirectory(false);
		setCommandLine("destroy -auto-approve");
	}
}
