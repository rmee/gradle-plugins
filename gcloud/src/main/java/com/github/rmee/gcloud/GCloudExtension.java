package com.github.rmee.gcloud;

import java.io.File;

import com.github.rmee.common.Client;
import com.github.rmee.common.ClientExtensionBase;
import com.github.rmee.gcloud.gke.GkeConfiguration;
import groovy.lang.Closure;
import org.gradle.api.Project;

public class GCloudExtension extends ClientExtensionBase {

	private GkeConfiguration gke = new GkeConfiguration();

	private File keyFile;

	private String region;

	private String zone;

	private String projectName;


	public GCloudExtension() {
		client = new Client(this, "gcloud") {
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

	public String getProject() {
		return projectName;
	}

	public void setProject(String project) {
		this.projectName = project;
	}

	public void init() {
		if (initialized) {
			return;
		}
		initialized = true;
		this.client.init(project);
	}

	public void exec(GCloudExecSpec spec) {
		client.exec(spec);
	}

	public void gke(Closure<GkeConfiguration> closure) {
		project.configure(gke, closure);
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public GkeConfiguration getGke() {
		return gke;
	}

	public void setGke(GkeConfiguration gke) {
		this.gke = gke;
	}

	public File getKeyFile() {
		return keyFile;
	}

	public void setKeyFile(File keyFile) {
		this.keyFile = keyFile;
	}

	protected void initProject(Project project) {
		super.project = project;
	}
}
