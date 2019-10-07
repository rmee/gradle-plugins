package com.github.rmee.jpa.schemagen.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {

	public static final void delete(File file) {
		if (file.exists()) {
			boolean deleted = file.delete();
			if (!deleted) {
				throw new IllegalStateException("cannot delete file " + file.getAbsolutePath());
			}
		}
	}

	public static final String readAsString(File file) throws IOException {
		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] data = new byte[(int) file.length()];
			fis.read(data);
			return new String(data, "UTF-8");
		}

	}

	public static final void writeString(String contents, File file) throws IOException {
		try (FileWriter fis = new FileWriter(file)) {
			fis.write(contents);
		}

	}
}
