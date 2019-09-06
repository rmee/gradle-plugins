package com.github.rmee.kubectl.tasks;

import com.github.rmee.cli.base.OutputFormat;
import com.github.rmee.cli.base.ExecResult;
import com.github.rmee.kubectl.KubectlExecSpec;
import com.github.rmee.kubectl.KubectlExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.util.function.Supplier;

public class WaitForRolloutTask extends DefaultTask {

	private Supplier<String> namespace;

	@Input
	public String getNamespace() {
		return namespace.get();
	}

	public void setNamespace(String namespace) {
		this.namespace = () -> namespace;
	}

	public void setNamespace(Supplier<String> namespace) {
		this.namespace = namespace;
	}

	@TaskAction
	public void run() {
		KubectlExtension kubectl = getProject().getExtensions().getByType(KubectlExtension.class);
		KubectlExecSpec execSpec = new KubectlExecSpec();
		execSpec.setCommandLine(String.format("kubectl get deployment,statefulset -o name --namespace=%s", getNamespace()));
		execSpec.setOutputFormat(OutputFormat.TEXT);
		ExecResult result = kubectl.exec(execSpec);
		String[] components = result.getText().split("\n");
		for (String component : components) {
			waitFor(kubectl, component);
		}
	}

	private void waitFor(KubectlExtension kubectl, String componentName) {
		KubectlExecSpec execSpec = new KubectlExecSpec();
		execSpec.setCommandLine(
				String.format("kubectl rollout status %s --watch=true --namespace=%s --timeout=360s",
						componentName,
						getNamespace()));
		kubectl.exec(execSpec);
	}
}
