package com.github.rmee.kubernetes.common;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.internal.os.OperatingSystem;

public abstract class Client {

	private ClientExtensionBase extension;

	private String version;

	private boolean download = true;

	private String binPath;


	private String repository;

	private String downloadUrl;

	private String downloadFileName;

	private String binName;

	private File downloadDir;

	private File installDir;

	private OperatingSystem operatingSystem;

	public Client(ClientExtensionBase extension, String binName) {
		this.binName = binName;
		this.extension = extension;
	}

	public OperatingSystem getOperatingSystem() {
		return operatingSystem;
	}

	public void setOperationSystem(OperatingSystem operatingSystem) {
		this.operatingSystem = operatingSystem;
	}

	public void init(Project project) {
		if (downloadDir == null && download) {
			downloadDir = new File(project.getBuildDir(), "tmp/" + binName + "/v" + version);
			downloadDir.mkdirs();
		}
		if (installDir == null) {
			installDir = new File(project.getBuildDir(), "/kubernetes/");
		}
		if (operatingSystem == null) {
			operatingSystem = org.gradle.internal.os.OperatingSystem.current();
		}

		if (binPath == null) {
			String binSuffix = getBinSuffix();
			binPath = new File(installDir, binName + binSuffix).getAbsolutePath();
			if (downloadUrl == null && download) {
				downloadFileName = computeDownloadFileName();
				downloadUrl = computeDownloadUrl(repository, downloadFileName);
			}
		}
	}

	protected abstract String computeDownloadFileName();

	protected String getBinSuffix() {
		return operatingSystem.isWindows() ? ".exe" : "";
	}

	protected abstract String computeDownloadUrl(String repository, String downloadFileName);

	public File getInstallDir() {
		extension.init();
		return installDir;
	}

	public void setInstallDir(File installDir) {
		this.installDir = installDir;
	}

	public File getDownloadDir() {
		extension.init();
		return downloadDir;
	}

	public void setDownloadDir(File downloadDir) {
		this.downloadDir = downloadDir;
	}

	public String getRepository() {
		extension.init();
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public String getDownloadUrl() {
		extension.init();
		return downloadUrl;
	}

	protected String getDownloadFileName() {
		extension.init();
		return downloadFileName;
	}

	public String getVersion() {
		extension.init();
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean getDownload() {
		extension.init();
		return download;
	}

	public void setDownload(boolean download) {
		this.download = download;
	}

	public String getBinPath() {
		extension.init();
		return binPath;
	}

	public void setBinPath(String binPath) {
		this.binPath = binPath;
	}

	public File getDownloadedFile() {
		extension.init();
		return new File(downloadDir, getDownloadFileName());
	}

	public String getBinName() {
		return binName;
	}
}
