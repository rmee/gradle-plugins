package com.github.rmee.kubernetes.oc;

import com.github.rmee.kubernetes.kubectl.KubectlExecBase;
import com.github.rmee.kubernetes.kubectl.KubectlExecSpec;
import org.gradle.api.tasks.TaskAction;

public class OcExec extends KubectlExecBase {


	public OcExec() {
		dependsOn("ocBootstrap");
		setGroup("kubernetes");
	}

	@Override
	protected KubectlExecSpec createSpec() {
		return new OcExecSpec();
	}

	public OcExecResult getResult() {
		return (OcExecResult) super.getResult();
	}

	@TaskAction
	protected void exec() {
		OcExtension extension = getProject().getExtensions().getByType(OcExtension.class);
		result = extension.exec((OcExecSpec) spec);
	}
}
