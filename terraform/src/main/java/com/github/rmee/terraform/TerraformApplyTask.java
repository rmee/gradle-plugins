package com.github.rmee.terraform;

public class TerraformApplyTask extends TerraformExec {

	public TerraformApplyTask() {
		retrieveSpec().setAddVariables(false);
		retrieveSpec().setAddConfigDirectory(false);
		commandLine("apply -auto-approve " + TerraformPlanTask.CONTAINER_PLAN_FILE);
	}
}
