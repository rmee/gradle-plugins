package com.github.rmee.gcloud;

import com.github.rmee.gcloud.aks.AksConfiguration;
import com.github.rmee.common.Client;
import com.github.rmee.common.ClientExtensionBase;
import groovy.lang.Closure;
import org.gradle.api.Project;

public class GCloudExtension extends ClientExtensionBase {

	private AksConfiguration aks = new AksConfiguration();

	private String subscriptionId;

	private String userName;

	private String password;

	private String tenantId;

	private String resourceGroup;

	private boolean servicePrincipal;

	public GCloudExtension() {
		client = new Client(this, "az") {
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

	public void exec(GCloudExecSpec spec) {
		project.exec(execSpec -> {
			client.configureExec(execSpec, spec);
		});
	}

	public void aks(Closure<AksConfiguration> closure) {
		project.configure(aks, closure);
	}

	public AksConfiguration getAks() {
		return aks;
	}

	public void setAks(AksConfiguration aks) {
		this.aks = aks;
	}

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String clientId) {
		this.userName = clientId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public void setResourceGroup(String resourceGroup) {
		this.resourceGroup = resourceGroup;
	}

	public String getResourceGroup() {
		return resourceGroup;
	}

	public boolean isServicePrincipal() {
		return servicePrincipal;
	}

	public void setServicePrincipal(boolean servicePrincipal) {
		this.servicePrincipal = servicePrincipal;
	}

	protected void setProject(Project project) {
		super.project = project;
	}
}
