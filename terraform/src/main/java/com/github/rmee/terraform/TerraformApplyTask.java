package com.github.rmee.terraform;

public class TerraformApplyTask extends TerraformExec {

	public TerraformApplyTask() {
		getSpec().setAddVariables(false);
		getSpec().setAddConfigDirectory(false);
		setCommandLine("apply -auto-approve " + TerraformPlanTask.CONTAINER_PLAN_FILE);
	}
}
