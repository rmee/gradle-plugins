package com.github.rmee.kubernetes.helm;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.rmee.kubernetes.common.Client;
import com.github.rmee.kubernetes.common.ClientExtensionBase;
import org.gradle.api.Project;
import org.gradle.internal.os.OperatingSystem;


public class HelmExtension extends ClientExtensionBase {

	private String tillerNamespace;

	private File sourceDir;

	private File outputDir;

	public HelmExtension() {
		client = new Client(this, "helm") {

			@Override
			protected String computeDownloadFileName() {
				OperatingSystem operatingSystem = getOperatingSystem();
				String downloadFileName = "helm-v" + getVersion();
				if (operatingSystem.isLinux()) {
					return downloadFileName + "-linux-amd64.zip";
				}
				else if (operatingSystem.isWindows()) {
					return downloadFileName + "-windows-amd64.zip";
				}
				else if (operatingSystem.isMacOsX()) {
					return downloadFileName + "-darwin-amd64.zip";
				}
				else {
					throw new IllegalStateException("unknown operation system: " + operatingSystem.getName());
				}
			}

			@Override
			protected String computeDownloadUrl(String repository, String downloadFileName) {
				String downloadUrl = repository;
				if (!downloadUrl.endsWith("/")) {
					downloadUrl += "/";
				}
				return downloadUrl + downloadFileName;
			}
		};
		client.setVersion("2.8.2");
		client.setRepository("https://storage.googleapis.com/kubernetes-helm");
	}

	public Set<String> getPackageNames() {
		if (!getSourceDir().exists()) {
			throw new IllegalStateException("helm source directory does not exist: " + getSourceDir().getAbsolutePath());
		}
		Stream<File> stream = Arrays.stream(sourceDir.listFiles());
		return stream.filter(file -> file.isDirectory() && new File(file, "Chart.yaml").exists())
				.map(file -> file.getName())
				.collect(Collectors.toSet());

	}

	public File getPackageSourceDir(String packageName) {
		return new File(getSourceDir(), packageName);
	}

	public File getSourceDir() {
		init();
		return sourceDir;
	}

	public void setSourceDir(File sourceDir) {
		checkNotInitialized();
		this.sourceDir = sourceDir;
	}

	public File getOutputFile(String packageName) {
		File outputDir = getOutputDir();
		return new File(outputDir, packageName + "-" + project.getVersion() + ".tgz");
	}

	public File getOutputDir() {
		init();
		return outputDir;
	}

	public void setOutputDir(File outputDir) {
		checkNotInitialized();
		this.outputDir = outputDir;
	}

	public String getTillerNamespace() {
		init();
		return tillerNamespace;
	}

	public void setTillerNamespace(String tillerNamespace) {
		checkNotInitialized();
		this.tillerNamespace = tillerNamespace;
	}

	public void init() {
		if (initialized) {
			return;
		}
		initialized = true;

		if (sourceDir == null) {
			sourceDir = new File(project.getProjectDir(), "src/main/helm/");
		}

		if (outputDir == null) {
			outputDir = new File(project.getBuildDir(), "distributions");
		}

		this.client.init(project);
	}

	public void exec(HelmExecSpec spec) {
		project.getLogger().warn("Executing: " + spec.getCommandLine());

		String[] args = spec.getCommandLine().split("\\s+");
		args[0] = getClient().getBinPath();
		project.exec(execSpec -> {
			String tillerNamespace = getTillerNamespace();
			Map<String, String> env = new HashMap();
			env.putAll(System.getenv());
			if (tillerNamespace != null) {
				env.put("TILLER_NAMESPACE", tillerNamespace);
			}
			execSpec.setEnvironment(env);
			execSpec.setIgnoreExitValue(spec.isIgnoreExitValue());
			execSpec.setCommandLine(Arrays.asList(args));
		});
	}

	protected void setProject(Project project) {
		this.project = project;
	}
}
