package com.github.rmee.helm;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class HelmPublish extends DefaultTask {

	public HelmPublish() {
		setGroup("kubernetes");
	}

	@TaskAction
	public void run() throws IOException {
		Project project = getProject();
		HelmExtension extension = project.getExtensions().getByType(HelmExtension.class);
		HelmExtension helm = project.getExtensions().getByType(HelmExtension.class);

		CredentialsProvider credentialsProvider = getCredentialsProvider();
		Set<String> packageNames = helm.getPackageNames();

		for (String packageName : packageNames) {
			try (CloseableHttpClient client = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build()) {
				String url = extension.getRepository() + "/" + packageName + "-" + project.getVersion() + ".tgz";
				File helmChart = helm.getOutputFile(packageName);
				HttpPut post = new HttpPut(url);

				FileEntity entity = new FileEntity(helmChart);
				post.setEntity(entity);

				HttpResponse response = client.execute(post);
				if (response.getStatusLine().getStatusCode() > 299) {
					throw new IOException(
							"failed to publish " + packageName + " to " + url + ", reason=" + response.getStatusLine());
				}
			}
		}
	}

	private CredentialsProvider getCredentialsProvider() {
		Project project = getProject();
		HelmExtension extension = project.getExtensions().getByType(HelmExtension.class);
		String helmUser = extension.getCredentials().getUser();
		String helmPass = extension.getCredentials().getPass();
		if (helmUser == null) {
			throw new IllegalArgumentException("deployment.helmUser not specified");
		}
		if (helmPass == null) {
			throw new IllegalArgumentException("deployment.helmPass not specified");
		}
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(helmUser, helmPass);
		provider.setCredentials(AuthScope.ANY, credentials);
		return provider;
	}
}
