package com.github.rmee.kubernetes.kubectl;

import com.github.rmee.kubernetes.common.Client;
import com.github.rmee.kubernetes.common.ClientExtensionBase;
import com.github.rmee.kubernetes.common.Credentials;
import com.github.rmee.kubernetes.common.OutputFormat;
import groovy.lang.Closure;
import org.gradle.api.Project;
import org.gradle.internal.os.OperatingSystem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class KubectlExtensionBase extends ClientExtensionBase {

	private String url;

	private Credentials credentials;

	private String namespace;

	public KubectlExtensionBase() {
		credentials = new Credentials(this);
		client = createClient();
	}

	protected abstract Client createClient();


	public Credentials credentials(Closure closure) {
		return (Credentials) project.configure(credentials, closure);
	}

	public String getUrl() {
		init();
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Credentials getCredentials() {
		init();
		return credentials;
	}

	public String getNamespace() {
		init();
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getToken(String serviceAccount) {
		KubectlExecSpec spec = new KubectlExecSpec();
		spec.setCommandLine(client.getBinName() + " describe serviceaccount " + serviceAccount);
		KubectlExecResult result = exec(spec);
		String tokenName = result.getProperty("tokens");

		spec.setCommandLine(client.getBinName() + " describe secret " + tokenName);
		result = exec(spec);
		return result.getProperty("token");
	}

	public KubectlExecResult exec(String command) {
		KubectlExecSpec spec = new KubectlExecSpec();
		spec.setCommandLine(command);
		return exec(spec);
	}

	protected KubectlExecResult exec(KubectlExecSpec spec) {
		String commandLine = spec.getCommandLine();

		int pipeIndex = commandLine.indexOf(" | ");
		if (pipeIndex == -1) {

			if (!commandLine.contains("token") && !commandLine.contains("pass")) {
				project.getLogger().warn("Executing: " + commandLine);
			} else {
				project.getLogger().debug("Executing: " + commandLine);
			}

			if (spec.getOutputFormat() == OutputFormat.JSON) {
				commandLine += " --output=json";
			}

			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
				String finalCommand = commandLine;
				project.exec(execSpec -> {
					execSpec.setIgnoreExitValue(spec.isIgnoreExitValue());
					execSpec.setCommandLine(parseCommand(finalCommand));
					if (spec.getInput() != null) {
						execSpec.setStandardInput(new ByteArrayInputStream(spec.getInput().getBytes()));
					}
					if (spec.getOutputFormat() != OutputFormat.CONSOLE) {
						execSpec.setStandardOutput(outputStream);
					}
				});

				String output = outputStream.toString();
				return createResult(output);
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		} else {
			KubectlExecSpec leftSpec = spec.duplicate();
			leftSpec.setCommandLine(commandLine.substring(0, pipeIndex).trim());
			leftSpec.setOutputFormat(OutputFormat.TEXT);
			KubectlExecResult leftResult = exec(leftSpec);

			KubectlExecSpec rightSpec = spec.duplicate();
			rightSpec.setCommandLine(commandLine.substring(pipeIndex + 3).trim());
			rightSpec.setInput(leftResult.getText());
			return exec(rightSpec);
		}
	}

	protected KubectlExecResult createResult(String output) {
		return new KubectlExecResult(output);
	}

	@Override
	public void init() {
		if (initialized) {
			return;
		}
		initialized = true;

		if (url == null) {
			throw new IllegalArgumentException("url must be set");
		}

		client.init(project);
	}

	protected void setProject(Project project) {
		this.project = project;
	}
}
