package com.github.rmee.gcloud;

import com.github.rmee.cli.base.Cli;
import com.github.rmee.cli.base.ClientExtensionBase;
import com.github.rmee.cli.base.internal.CliDownloadStrategy;
import com.github.rmee.gcloud.gke.GkeConfiguration;
import groovy.lang.Closure;
import org.gradle.api.Project;

import java.io.File;

public class GCloudExtension extends ClientExtensionBase {

	private GkeConfiguration gke = new GkeConfiguration();

	private File keyFile;

	private String region;

	private String zone;

	private String projectName;


	public GCloudExtension() {
		CliDownloadStrategy downloadStrategy = new CliDownloadStrategy() {
			@Override
			public String computeDownloadFileName(Cli cli) {
				throw new UnsupportedOperationException("download not supported, make use of docker");
			}

			@Override
			public String computeDownloadUrl(Cli cli, String repository, String downloadFileName) {
				throw new UnsupportedOperationException("download not supported, make use of docker");
			}
		};
		cli = new Cli("gcloud", downloadStrategy, () -> project);
	}

	public String getProject() {
		return projectName;
	}

	public void setProject(String project) {
		this.projectName = project;
	}


	public void exec(GCloudExecSpec spec) {
		cli.exec(spec);
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
