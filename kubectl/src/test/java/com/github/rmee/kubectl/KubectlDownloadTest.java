package com.github.rmee.kubectl;

import com.github.rmee.cli.base.Cli;
import org.gradle.api.Project;
import org.gradle.internal.os.OperatingSystem;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class KubectlDownloadTest {

	@Test
	public void testWindowsDownload() throws IOException {
		testDownload(OperatingSystem.WINDOWS);
	}

	@Test
	public void testLinuxDownload() throws IOException {
		testDownload(OperatingSystem.LINUX);
	}

	@Test
	public void testMacDownload() throws IOException {
		testDownload(OperatingSystem.MAC_OS);
	}

	private void testDownload(OperatingSystem operatingSystem) throws IOException {
		KubectlExtension extension = new KubectlExtension();
		Cli cli = extension.getCli();
		cli.setVersion("1.8.0");
		cli.setDockerized(false);
		cli.setOperationSystem(operatingSystem);

		extension.setProject(Mockito.mock(Project.class));
		extension.setUrl("test");
		extension.init();

		String downloadUrl = cli.getDownloadUrl();
		try {
			try (InputStream inputStream = new URL(downloadUrl).openStream()) {
				inputStream.read();
			}
		} catch (IOException e) {
			throw new IllegalStateException(downloadUrl, e);
		}
	}
}
