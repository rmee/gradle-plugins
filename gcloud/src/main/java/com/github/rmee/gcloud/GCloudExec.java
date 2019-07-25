package com.github.rmee.gcloud;

import com.github.rmee.cli.base.CliExecSpec;
import com.github.rmee.cli.base.internal.ClientExecBase;
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
	protected CliExecSpec retrieveSpec() {
		return spec;
	}
}
