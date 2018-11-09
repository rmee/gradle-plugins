package com.github.rmee.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.rmee.common.internal.UnixUtils;
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

	private Map<String, File> volumeMappings = new HashMap<>();

	private Map<Integer, Integer> portMappings = new HashMap<>();

	private boolean useWrapper = true;

	private List<String> outputPaths = new ArrayList<>();

	private String runAs;

	private boolean runAsEnabled = true;

	public Client(ClientExtensionBase extension, String binName) {
		this.binName = binName;
		this.extension = extension;

		String proxyHostName = System.getProperty("http.proxyHost");
		String proxyPort = System.getProperty("http.proxyPort");
		String proxyUrl;
		if (proxyHostName == null) {
			proxyUrl = System.getenv("HTTP_PROXY");
		}
		else {
			proxyUrl = "http://" + proxyHostName + ":" + proxyPort;
		}
		if (proxyUrl != null) {
			environment.put("HTTP_PROXY", proxyUrl);
		}
		environment.put("HOME", "/build/wrapper");
	}

	public String getRunAs() {
		if (runAs == null && runAsEnabled && getOperatingSystem() != OperatingSystem.WINDOWS) {
			runAs = UnixUtils.getUid();
		}
		return runAs;
	}

	public void setRunAs(String runAs) {
		this.runAs = runAs;
	}

	public boolean isRunAsEnabled() {
		return runAsEnabled;
	}

	public void setRunAsEnabled(boolean runAsEnabled) {
		this.runAsEnabled = runAsEnabled;
	}

	/**
	 * Mapping from docker path to host path (the other way around compared to docker to ease configuration)
	 */
	public Map<String, File> getVolumeMappings() {
		return volumeMappings;
	}

	public void setVolumeMappings(Map<String, File> volumeMappings) {
		this.volumeMappings = volumeMappings;
	}

	public Map<Integer, Integer> getPortMappings() {
		return portMappings;
	}

	public void setPortMappings(Map<Integer, Integer> portMappings) {
		this.portMappings = portMappings;
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

	public List<String> getOutputPaths() {
		return outputPaths;
	}

	public void setOutputPaths(List<String> outputPaths) {
		this.outputPaths = outputPaths;
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
		if (operatingSystem == null) {
			operatingSystem = org.gradle.internal.os.OperatingSystem.current();
		}
		return operatingSystem;
	}

	public void setOperationSystem(OperatingSystem operatingSystem) {
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

		List<String> args = clientExecSpec.getCommandLine();
		if (dockerized) {
			List<String> commandLine = new ArrayList<>();
			commandLine.addAll(buildBaseCommandLine());

			String containerName = clientExecSpec.getContainerName();
			if (containerName != null) {
				commandLine.add("--name");
				commandLine.add(containerName);
			}

			String volumesFrom = clientExecSpec.getVolumesFrom();
			if (volumesFrom != null) {
				commandLine.add("--volumes-from");
				commandLine.add(volumesFrom);
			}

			commandLine.add(imageName + ":" + version);

			for (String arg : args) {
				commandLine.add(mapArg(arg));
			}
			System.out.println("Executing: " + commandLine);
			execSpec.setCommandLine(commandLine);

		}
		else {
			args.set(0, getBinPath());
			execSpec.setCommandLine(args);
		}

		File stdoutFile = clientExecSpec.getStdoutFile();
		if (stdoutFile != null) {
			try {
				if (stdoutFile.exists() && !stdoutFile.delete()) {
					throw new IllegalStateException("failed to delete " + stdoutFile);
				}
				execSpec.setStandardOutput(new FileOutputStream(stdoutFile));
			}
			catch (FileNotFoundException e) {
				throw new IllegalStateException("failed to redirect helm stdout: " + e.getMessage(), e);
			}
		}

	}

	/**
	 * Apply volume mappings to arguments
	 */
	private String mapArg(String arg) {
		for (Map.Entry<String, File> entry : volumeMappings.entrySet()) {
			String path = entry.getValue().getAbsolutePath();
			if (arg.startsWith(path)) {
				arg = entry.getKey() + arg.substring(path.length());
				break;
			}
		}
		arg = arg.replace('\\', '/');
		return arg;
	}

	private Collection<String> buildBaseCommandLine() {
		List<String> commandLine = new ArrayList<>();
		commandLine.add("docker");
		commandLine.add("run");
		commandLine.add("-i");
		commandLine.add("--rm");

		// directly running docker images as non-root is causing permission issues with many images
		// we fix the permissions of the output files instead
		String runAs = getRunAs();
		if (runAs != null) {
			commandLine.add("-u");
			commandLine.add(runAs);
		}

		for (Map.Entry<String, String> entry : environment.entrySet()) {
			commandLine.add("-e");
			commandLine.add(entry.getKey() + "=" + entry.getValue());
		}

		for (Map.Entry<String, File> entry : volumeMappings.entrySet()) {
			addVolumeMapping(commandLine, entry);
		}

		for (Map.Entry<Integer, Integer> entry : portMappings.entrySet()) {
			commandLine.add("-p");
			commandLine.add(entry.getValue() + ":" + entry.getKey());
		}

		if (version == null) {
			throw new IllegalStateException("no version specified");
		}

		return commandLine;
	}

	private void addVolumeMapping(List<String> commandLine, Map.Entry<String, File> entry) {
		addVolumeMapping(commandLine, entry.getKey(), entry.getValue());
	}

	private void addVolumeMapping(List<String> commandLine, String guestPath, File hostDir) {
		String hostPath = hostDir.getAbsolutePath();

		// fix hostPath issues with windows
		hostPath = hostPath.replace('\\', '/');

		commandLine.add("-v");
		commandLine.add(hostPath + ":" + guestPath);

		hostDir.mkdirs();
	}

	public boolean useWrapper() {
		return useWrapper;
	}

	public void setWrapper(boolean useWrapper) {
		this.useWrapper = useWrapper;
	}

	public void setupWrapper(Project project) {
		setupWrapper(project, true);
	}

	public void setupWrapper(Project project, boolean mustIncludeBinary) {
		if (useWrapper()) {
			Project rootProject = project.getRootProject();
			Task wrapper = rootProject.getTasks().getByName("wrapper");
			wrapper.doLast(task -> {
				StringBuilder builder = new StringBuilder();

				builder.append("#!/usr/bin/env sh\n");

				// fix paths for mingw
				builder.append("case \"`uname`\" in\n");
				builder.append("  MINGW* )\n");
				builder.append("  msys=true\n");
				builder.append("  ;;\n");
				builder.append("esac\n");
				builder.append("NORMPWD=PWD\n");  // NOSONAR PWD is not an issue password issue
				builder.append("if [ \"$msys\" = \"true\" ] ; then\n");
				builder.append("  export MSYS_NO_PATHCONV=1\n");
				builder.append("  export MSYS2_ARG_CONV_EXC=\"*\"\n");
				builder.append("  NORMPWD=$(cygpath -w \"$PWD\")\n");  // NOSONAR PWD is not an issue password issue
				builder.append("  NORMPWD=${NORMPWD//'\\'/'/'}\n"); // NOSONAR PWD is not an issue password issue
				builder.append("fi\n");

				builder.append("exec");
				Collection<String> commandLine = buildBaseCommandLine();

				commandLine.add(imageName + ":" + version);

				for (String element : commandLine) {
					builder.append(' ');

					// avoid absolute paths
					String projectPath = project.getProjectDir().getAbsolutePath().replace('\\', '/');
					if (element.startsWith(projectPath)) {
						element = "$NORMPWD/" + element.substring(projectPath.length() + 1);
					}
					builder.append(element);
				}
				if (mustIncludeBinary) {
					builder.append(' ');
					builder.append(binName);
				}
				builder.append(" \"$@\"\n");

				File file = new File(project.getProjectDir(), binName);
				try (FileWriter writer = new FileWriter(file)) {
					writer.write(builder.toString());
				}
				catch (IOException e) {
					throw new IllegalStateException(e);
				}
			});
		}
	}

	public void exec(ClientExecSpec spec) {
		Project project = extension.project;
		project.exec(execSpec -> {
			configureExec(execSpec, spec);
		});
	}

	public void deleteOutputFiles() {
		for (String path : outputPaths) {
			deleteOutputFile(path);
		}
	}

	private void deleteOutputFile(String path) {
		File hostDir = volumeMappings.get(path);
		if (hostDir == null) {
			throw new IllegalStateException("volume mapping not found for " + path);
		}

		if (hostDir.exists()) {
			for (File file : hostDir.listFiles()) {
				if (!file.delete()) {
					// permission issue => delete with docker as ROOT
					List<String> commandLine = new ArrayList<>();
					commandLine.add("docker");
					commandLine.add("run");
					commandLine.add("-i");
					addVolumeMapping(commandLine, path, hostDir);
					commandLine.add("bash");
					commandLine.add("rm");
					commandLine.add("-f");
					commandLine.add(path + "/" + file.getName());

					Project project = extension.project;
					project.exec(execSpec -> execSpec.commandLine(commandLine));
				}
			}
		}
	}
}
