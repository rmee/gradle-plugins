package com.github.rmee.terraform;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.rmee.common.Client;
import com.github.rmee.common.ClientExecSpec;
import com.github.rmee.common.ClientExtensionBase;
import groovy.lang.Closure;
import org.gradle.api.Project;

public class TerraformExtension extends ClientExtensionBase {

	private File sourceDir;

	private boolean debug = false;

	private Map<String, Object> variables = new HashMap<>();

	public TerraformExtension() {
		client = new Client(this, "terraform") {
			@Override
			protected String computeDownloadFileName() {
				throw new UnsupportedOperationException("download not supported, make use of docker");
			}

			@Override
			protected String computeDownloadUrl(String repository, String downloadFileName) {
				throw new UnsupportedOperationException("download not supported, make use of docker");
			}
		};
	}

	public void init() {
		if (initialized) {
			return;
		}
		initialized = true;
		this.client.init(project);
	}

	public void exec(TerraformExecSpec spec) {
		project.getLogger().warn("Executing: " + spec.getCommandLine());

		project.exec(execSpec -> {

			// we mount the project into the docker container
			File terraformTempDir = new File(project.getBuildDir(), "terraform");
			terraformTempDir.mkdirs();

			//commandLine.add("-e");
			//commandLine.add("TF_LOG=DEBUG");

			final ClientExecSpec duplicate = spec.duplicate();
			List<String> commandLine = duplicate.getCommandLine();

			TerraformExtension extension = project.getExtensions().getByType(TerraformExtension.class);
			if (spec.getAddVariables()) {
				for (Map.Entry<String, Object> entry : extension.getVariables().entrySet()) {
					Object value = entry.getValue();
					if (value instanceof Closure) {
						value = ((Closure) value).call();
					}
					if (value != null) {
						commandLine.add("-var");
						commandLine.add(entry.getKey() + "=" + value);
					}
				}
			}

			if (spec.getAddConfigDirectory()) {
				commandLine.add("/etc/project/conf");
			}

			client.configureExec(execSpec, duplicate);
		});
	}

	public Map<String, Object> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}

	public File getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(File sourceDir) {
		this.sourceDir = sourceDir;
	}

	public boolean getDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	protected void setProject(Project project) {
		this.project = project;
	}

	public void exec(Closure<TerraformExecSpec> closure) {
		TerraformExecSpec spec = new TerraformExecSpec();
		project.configure(spec, closure);
		exec(spec);
	}
}
