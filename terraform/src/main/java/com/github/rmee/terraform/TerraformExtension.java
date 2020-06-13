package com.github.rmee.terraform;

import com.github.rmee.cli.base.Cli;
import com.github.rmee.cli.base.CliExecSpec;
import com.github.rmee.cli.base.ClientExtensionBase;
import com.github.rmee.cli.base.ExecResult;
import com.github.rmee.cli.base.OutputFormat;
import com.github.rmee.cli.base.internal.CliDownloadStrategy;
import groovy.lang.Closure;
import org.gradle.api.Project;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TerraformExtension extends ClientExtensionBase {

	private File sourceDir;

	private boolean debug = false;

	private Map<String, Object> variables = new HashMap<>();

	public TerraformExtension() {
		CliDownloadStrategy downloadStratey = new CliDownloadStrategy() {
			@Override
			public String computeDownloadFileName(Cli cli) {
				throw new UnsupportedOperationException("download not  yet supported, make use of docker");
			}

			@Override
			public String computeDownloadUrl(Cli cli, String repository, String downloadFileName) {
				throw new UnsupportedOperationException("download not  yet supported, make use of docker");
			}
		};
		cli = new Cli("terraform", downloadStratey, () -> project);
		cli.setAppendBinaryName(false); // official docker image does not allow it
	}

	public ExecResult exec(TerraformExecSpec spec) {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

			project.getLogger().warn("Executing: " + spec.getCommandLine().stream().collect(Collectors.joining(" ")));

			project.exec(execSpec -> {

				// we mount the project into the docker container
				File terraformTempDir = new File(project.getBuildDir(), "terraform");
				terraformTempDir.mkdirs();

				final CliExecSpec duplicate = spec.duplicate();
				List<String> commandLine = duplicate.getCommandLine();

				if (debug) {
					cli.getDockerEnvironment().put("TF_LOG", "DEBUG");
				}

				if (spec.getOutputFormat() != OutputFormat.CONSOLE) {
					execSpec.setStandardOutput(outputStream);
				}

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
					commandLine.add("/workdir/src/main/terraform");
				}

				cli.configureExec(execSpec, duplicate);
			});

			String output = outputStream.toString();
			return new ExecResult(output);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
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

	public ExecResult exec(Closure<TerraformExecSpec> closure) {
		TerraformExecSpec spec = new TerraformExecSpec();
		project.configure(spec, closure);
		return exec(spec);
	}
}
