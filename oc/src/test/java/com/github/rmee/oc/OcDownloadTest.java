package com.github.rmee.oc;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.github.rmee.common.Client;
import com.github.rmee.oc.OcExtension;
import org.gradle.api.Project;
import org.gradle.internal.os.OperatingSystem;
import org.junit.Test;
import org.mockito.Mockito;

public class OcDownloadTest {

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
		OcExtension extension = new OcExtension();
		extension.setUrl("test");
		Client client = extension.getClient();
		client.setOperationSystem(operatingSystem);

		extension.setProject(Mockito.mock(Project.class));
		extension.init();
		String downloadUrl = client.getDownloadUrl();

		try {
			URL url = new URL(downloadUrl);
			try (InputStream inputStream = url.openStream()) {
				inputStream.read();
			}
		}
		catch (IOException e) {
			throw new IllegalStateException(downloadUrl, e);
		}
	}
}
