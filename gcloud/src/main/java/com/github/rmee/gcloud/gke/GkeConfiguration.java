package com.github.rmee.gcloud.gke;

import java.io.File;

public class GkeConfiguration {

	private File kubeDir;

	private String clusterName;

	public File getKubeDir() {
		return kubeDir;
	}

	public void setKubeDir(File kubeDir) {
		this.kubeDir = kubeDir;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
}
