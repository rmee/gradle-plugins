package com.github.rmee.kubernetes.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.util.StringUtil;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.internal.os.OperatingSystem;
import org.gradle.process.ExecSpec;

public abstract class Client {

	private ClientExtensionBase extension;

	private String version;

	private Map<String, String> environment = new HashMap();

	private boolean download = true;

	private String binPath;

	private boolean dockerized = true;

	private String imageName;

	private String repository;

	private String downloadUrl;

	private String downloadFileName;

	private String binName;

	private File downloadDir;

	private File installDir;

	private OperatingSystem operatingSystem;

	private Map<String, String> volumeMappings = new HashMap<>();

	private boolean useWrapper = true;

	public Client(ClientExtensionBase extension, String binName) {
		this.binName = binName;
		this.extension = extension;

		String proxyHostName = System.getProperty("http.proxyHost");
		String proxyPort = System.getProperty("http.proxyPort");
		String proxyUrl;
		if (proxyHostName == null) {
			proxyUrl = System.getenv("HTTP_PROXY");
		} else {
			proxyUrl = "http://" + proxyHostName + ":" + proxyPort;
		}
		if (proxyUrl != null) {
			environment.put("HTTP_PROXY", proxyUrl);
		}
	}

	public Map<String, String> getVolumeMappings() {
		return volumeMappings;
	}

	public void setVolumeMappings(Map<String, String> volumeMappings) {
		this.volumeMappings = volumeMappings;
	}

	public Map<String, String> getEnvironment() {
		return environment;
	}

	public void setEnvironment(Map<String, String> environment) {
		this.environment = environment;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getVersion() {
		extension.init();
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isDockerized() {
		return dockerized;
	}

	public void setDockerized(boolean dockerized) {
		this.dockerized = dockerized;
	}

	public OperatingSystem getOperatingSystem() {
		checkNotDockerized();
		return operatingSystem;
	}

	public void setOperationSystem(OperatingSystem operatingSystem) {
		checkNotDockerized();
		this.operatingSystem = operatingSystem;
	}

	public void init(Project project) {
		if (!dockerized) {
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
	}

	protected abstract String computeDownloadFileName();

	/**
	 * @deprecated move to dockerized version
	 */
	@Deprecated
	protected String getBinSuffix() {
		checkNotDockerized();
		return operatingSystem.isWindows() ? ".exe" : "";
	}

	protected abstract String computeDownloadUrl(String repository, String downloadFileName);

	/**
	 * @deprecated move to dockerized version
	 */
	@Deprecated
	public File getInstallDir() {
		checkNotDockerized();
		extension.init();
		return installDir;
	}

	/**
	 * @deprecated move to dockerized version
	 */
	@Deprecated
	public void setInstallDir(File installDir) {
		this.installDir = installDir;
	}

	/**
	 * @deprecated move to dockerized version
	 */
	@Deprecated
	public File getDownloadDir() {
		extension.init();
		return downloadDir;
	}

	/**
	 * @deprecated move to dockerized version
	 */
	@Deprecated
	public void setDownloadDir(File downloadDir) {
		checkNotDockerized();
		this.downloadDir = downloadDir;
	}

	/**
	 * @deprecated move to dockerized version
	 */
	@Deprecated
	public String getRepository() {
		checkNotDockerized();
		extension.init();
		return repository;
	}

	/**
	 * @deprecated move to dockerized version
	 */
	@Deprecated
	public void setRepository(String repository) {
		this.repository = repository;
	}

	/**
	 * @deprecated move to dockerized version
	 */
	@Deprecated
	public String getDownloadUrl() {
		extension.init();
		return downloadUrl;
	}

	protected String getDownloadFileName() {
		checkNotDockerized();
		extension.init();
		return downloadFileName;
	}

	/**
	 * @deprecated move to dockerized version
	 */
	@Deprecated
	public boolean getDownload() {
		extension.init();
		return download;
	}

	/**
	 * @deprecated move to dockerized version
	 */
	@Deprecated
	public void setDownload(boolean download) {
		checkNotDockerized();
		this.download = download;
	}

	/**
	 * @deprecated move to dockerized version
	 */
	@Deprecated
	public String getBinPath() {
		extension.init();
		return binPath;
	}

	public void setBinPath(String binPath) {
		checkNotDockerized();
		this.binPath = binPath;
	}

	/**
	 * @deprecated move to dockerized version
	 */
	@Deprecated
	public File getDownloadedFile() {
		extension.init();
		return new File(downloadDir, getDownloadFileName());
	}

	public String getBinName() {
		return binName;
	}


	private void checkNotDockerized() {
		if (dockerized) {
			throw new IllegalStateException("not necssary when in dockerized mode");
		}
	}

	public void configureExec(ExecSpec execSpec, ClientExecSpec clientExecSpec) {
		Map<String, String> execEnv = new HashMap<>();
		execEnv.putAll(environment);

		execSpec.setEnvironment(execEnv);
		execSpec.setIgnoreExitValue(clientExecSpec.isIgnoreExitValue());

		String[] args = clientExecSpec.getCommandLine().split("\\s+");
		if (dockerized) {
			List<String> commandLine = new ArrayList<>();
			commandLine.addAll(buildBaseCommandLine());
			commandLine.addAll(Arrays.asList(args));
			System.out.println("Executing: " + commandLine);
			execSpec.setCommandLine(commandLine);
		} else {
			args[0] = getBinPath();
			execSpec.setCommandLine(Arrays.asList(args));
		}

		File stdoutFile = clientExecSpec.getStdoutFile();
		if (stdoutFile != null) {
			try {
				if (stdoutFile.exists() && !stdoutFile.delete()) {
					throw new IllegalStateException("failed to delete " + stdoutFile);
				}
				execSpec.setStandardOutput(new FileOutputStream(stdoutFile));
			} catch (FileNotFoundException e) {
				throw new IllegalStateException("failed to redirect helm stdout: " + e.getMessage(), e);
			}
		}

	}

	private Collection<String> buildBaseCommandLine() {
		List<String> commandLine = new ArrayList<>();
		commandLine.add("docker");
		commandLine.add("run");

		for (Map.Entry<String, String> entry : environment.entrySet()) {
			commandLine.add("-e");
			commandLine.add(entry.getKey() + "=" + entry.getValue());
		}

		for (Map.Entry<String, String> entry : volumeMappings.entrySet()) {
			commandLine.add("-v");
			commandLine.add(entry.getValue() + ":" + entry.getKey());
		}

		commandLine.add(imageName + ":" + version);
		return commandLine;
	}

	public boolean useWrapper() {
		return useWrapper;
	}

	public void setWrapper(boolean useWrapper) {
		this.useWrapper = useWrapper;
	}

	public void setupWrapper(Project project) {
		if (useWrapper()) {
			Project rootProject = project.getRootProject();
			Task wrapper = rootProject.getTasks().getByName("wrapper");
			wrapper.doLast(task -> {
				StringBuilder builder = new StringBuilder();

				builder.append("#!/usr/bin/env sh\n");
				builder.append("exec");
				Collection<String> commandLine = buildBaseCommandLine();
				for (String element : commandLine) {
					builder.append(' ');

					String rootPath = rootProject.getProjectDir().getAbsolutePath();
					if (element.startsWith(rootPath)) {
						element = element.substring(rootPath.length());
						if (element.startsWith(File.separator)) {
							element = element.substring(1);
						}
					}

					builder.append(element);
				}
				builder.append(' ');
				builder.append(binName);
				builder.append(" \"$@\"\n");

				File file = new File(rootProject.getProjectDir(), binName);
				try (FileWriter writer = new FileWriter(file)) {
					writer.write(builder.toString());
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			});
		}
	}
}
