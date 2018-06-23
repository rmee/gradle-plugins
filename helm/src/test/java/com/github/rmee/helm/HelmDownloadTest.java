package com.github.rmee.helm;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.github.rmee.common.Client;
import org.gradle.api.Project;
import org.gradle.internal.os.OperatingSystem;
import org.junit.Test;
import org.mockito.Mockito;

public class HelmDownloadTest {

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
		HelmExtension extension = new HelmExtension();
		Client client = extension.getClient();
		client.setDockerized(false);
		client.setOperationSystem(operatingSystem);

		extension.setProject(Mockito.mock(Project.class));
		extension.init();
		String downloadUrl = client.getDownloadUrl();

		try {
			try (InputStream inputStream = new URL(downloadUrl).openStream()) {
				inputStream.read();
			}
		} catch (IOException e) {
			throw new IllegalStateException(downloadUrl, e);
		}
	}
}
