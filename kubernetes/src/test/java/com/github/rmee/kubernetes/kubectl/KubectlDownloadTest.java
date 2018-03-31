package com.github.rmee.kubernetes.kubectl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.github.rmee.kubernetes.common.Client;
import org.gradle.api.Project;
import org.gradle.internal.os.OperatingSystem;
import org.junit.Test;
import org.mockito.Mockito;

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
		Client client = extension.getClient();
		client.setOperationSystem(operatingSystem);

		extension.setProject(Mockito.mock(Project.class));
		extension.setUrl("test");
		extension.init();

		String downloadUrl = client.getDownloadUrl();
		try {
			try (InputStream inputStream = new URL(downloadUrl).openStream()) {
				inputStream.read();
			}
		}
		catch (IOException e) {
			throw new IllegalStateException(downloadUrl, e);
		}
	}
}
