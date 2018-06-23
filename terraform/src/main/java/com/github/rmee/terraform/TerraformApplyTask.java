package com.github.rmee.terraform;

public class TerraformApplyTask extends TerraformExec {

	public TerraformApplyTask() {
		retrieveSpec().setAddVariables(false);
		retrieveSpec().setAddConfigDirectory(false);
		setCommandLine("apply -auto-approve " + TerraformPlanTask.CONTAINER_PLAN_FILE);
	}
}
