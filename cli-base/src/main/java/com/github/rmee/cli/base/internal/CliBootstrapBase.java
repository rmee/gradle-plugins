package com.github.rmee.cli.base.internal;

import java.io.File;
import java.io.IOException;

import com.github.rmee.cli.base.Cli;
import com.github.rmee.cli.base.ClientExtensionBase;
import de.undercouch.gradle.tasks.download.Download;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.CopySpec;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CliBootstrapBase extends Download {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private Class<? extends ClientExtensionBase> extensionClass;

	protected CliBootstrapBase(Class<? extends ClientExtensionBase> extensionClass) {
		this.extensionClass = extensionClass;

		setGroup("kubernetes");

		overwrite(false);

		getOutputs().upToDateWhen(task -> {
			Project project = getProject();
			ClientExtensionBase extension = project.getExtensions().getByType(extensionClass);
			Cli cli = extension.getCli();
			return !cli.getDownload()
					|| getDownloadedFile().exists() && new File(cli.getBinPath()).exists();
		});

		doFirst(task -> {
			LOGGER.info("downloading cli from {}", getSrc());
		});

		doLast(task -> {
			Project project = getProject();
			Action<? super CopySpec> action = (Action<CopySpec>) copySpec -> {
				File compressedFile = getDownloadedFile();
				copySpec.setFileMode(755);
				if (isCompressed()) {
					if (compressedFile.getName().endsWith("zip")) {
						copySpec.from(project.zipTree(compressedFile));
					} else {
						copySpec.from(project.tarTree(project.getResources().gzip(compressedFile)));
					}

					copySpec.exclude("LICENSE");
					copySpec.exclude("README.md");

					// flatten nested tar.gz  structure and zip directories
					copySpec.eachFile(
							details -> details.setPath(details.getPath().substring(details.getPath().indexOf('/') + 1)));

				} else {
					copySpec.from(compressedFile, it -> it.setFileMode(755));
				}
				ClientExtensionBase extension = project.getExtensions().getByType(extensionClass);

				File unzippedDir = extension.getCli().getInstallDir();
				unzippedDir.mkdirs();
				copySpec.into(unzippedDir);
			};
			project.copy(action);
		});

	}

	@TaskAction
	public void download() throws IOException {
		try {
			super.download();
		} catch (IOException e) {
			throw new IOException("Failed to download " + getSrc(), e);
		}
	}

	protected File getDownloadedFile() {
		Project project = getProject();
		ClientExtensionBase extension = project.getExtensions().getByType(extensionClass);
		return extension.getCli().getDownloadedFile();
	}

	protected boolean isCompressed() {
		return true;
	}
}
