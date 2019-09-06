package com.github.rmee.kubectl.tasks;

import com.github.rmee.cli.base.OutputFormat;
import com.github.rmee.cli.base.ExecResult;
import com.github.rmee.kubectl.KubectlExecSpec;
import com.github.rmee.kubectl.KubectlExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Supplier;

public class FetchLogTask extends DefaultTask {

	private Supplier<String> namespace;

	public FetchLogTask() {
		setDescription("fetches all logs from all pods collected within the last 5 minutes");
	}

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
	public void run() throws IOException {
		Project project = getProject();
		KubectlExtension extension = project.getExtensions().getByType(KubectlExtension.class);

		KubectlExecSpec execSpec = new KubectlExecSpec();
		execSpec.setCommandLine(String.format("kubectl get deployment,statefulset -o name --namespace=%s", namespace));
		execSpec.setOutputFormat(OutputFormat.TEXT);
		ExecResult result = extension.exec(execSpec);
		for (String component : result.getText().trim().split("\n")) {
			KubectlExecSpec execSpecComp = new KubectlExecSpec();
			execSpecComp.setCommandLine(
					String.format("kubectl logs %s --namespace=%s --since=5m --all-containers=true", component, namespace)
			);
			execSpecComp.setOutputFormat(OutputFormat.TEXT);
			ExecResult resultComp = extension.exec(execSpecComp);

			File logFile = project.file("build/logs/" + namespace + "/" + component + ".log");
			logFile.getParentFile().mkdirs();

			try (FileWriter writer = new FileWriter(logFile)) {
				writer.write(resultComp.getText());
			}
		}
	}
}