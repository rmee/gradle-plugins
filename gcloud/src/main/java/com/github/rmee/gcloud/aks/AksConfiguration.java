package com.github.rmee.gcloud.aks;

import java.io.File;

public class AksConfiguration {

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
