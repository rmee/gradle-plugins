package com.github.rmee.kubectl;

import java.net.MalformedURLException;
import java.net.URL;

import com.github.rmee.common.Credentials;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class KubectlUseContext extends DefaultTask {

	private String contextId;


	@TaskAction
	protected void exec() {
		KubectlExtension extension = getExtension();
		Credentials credentials = extension.getCredentials();

		if (contextId == null) {
			boolean skipTls = true;
			String userName = credentials.getUserName();
			String password = credentials.getPassword();
			String token = credentials.getToken();
			URL url;
			try {
				url = new URL(extension.getUrl());
			}
			catch (MalformedURLException e) {
				throw new IllegalStateException(e);
			}
			String clusterId = url.getHost();
			String namespace = extension.getNamespace();
			String credId = userName + "/" + clusterId;

			contextId = namespace + "/" + clusterId + "/" + userName;

			if (userName == null) {
				throw new IllegalStateException("kubectl.userName not specified");
			}

			if (password != null) {
				exec("kubectl config set-credentials " + credId + " --username=" + userName + " --password=" + password);
			}
			else if (token != null) {
				exec("kubectl config set-credentials " + credId + " --token=" + token);
			}
			else {
				throw new IllegalStateException("neither kubectl.password nor kubectl.token specified");
			}
			exec("kubectl config set-cluster " + clusterId + " --insecure-skip-tls-verify=" + skipTls + " --server=" + url);
			exec("kubectl config set-context " + contextId + " --user=" + credId + " "
					+ "--namespace=" + namespace + " "
					+ "--cluster=" + clusterId);
		}
		exec("kubectl config use-context " + contextId);
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	private void exec(String command) {
		KubectlExtension extension = getExtension();
		KubectlExecSpec spec = new KubectlExecSpec();
		spec.setCommandLine(command);
		extension.exec(spec);
	}

	private KubectlExtension getExtension() {
		return getProject().getExtensions().getByType(KubectlExtension.class);
	}
}
