package com.github.rmee.common.internal;

import java.io.IOException;
import java.io.InputStream;

public class UnixUtils {

	private static String gid;

	private static String uid;

	public static String getGid() {
		if (gid == null) {
			gid = findId(true);
		}
		return gid;
	}

	public static String getUid() {
		if (uid == null) {
			uid = findId(false);
		}
		return uid;
	}

	private static String findId(boolean group) {
		try {
			String userName = System.getProperty("user.name");
			String command = "id" + (group ? " -g " : " -u ") + userName;
			Process child = Runtime.getRuntime().exec(command);

			// Get the input stream and read from it
			StringBuilder builder = new StringBuilder();
			InputStream in = child.getInputStream();
			int c;
			while ((c = in.read()) != -1) {
				builder.append((char) c);
			}
			in.close();
			return builder.toString().trim();
		}
		catch (IOException e) {
			throw new IllegalStateException("failed to obtain uid/gid", e);
		}
	}
}
