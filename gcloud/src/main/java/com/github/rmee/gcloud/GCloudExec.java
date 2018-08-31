package com.github.rmee.gcloud;

import com.github.rmee.common.ClientExecSpec;
import com.github.rmee.common.internal.ClientExecBase;
import org.gradle.api.tasks.TaskAction;

public class GCloudExec extends ClientExecBase {

	private GCloudExecSpec spec = new GCloudExecSpec();

	public GCloudExec() {
		setGroup("provision");
	}

	@TaskAction
	public void run() {
		GCloudExtension extension = getProject().getExtensions().getByType(GCloudExtension.class);
		extension.exec(spec);
	}

	@Override
	protected ClientExecSpec retrieveSpec() {
		return spec;
	}
}
