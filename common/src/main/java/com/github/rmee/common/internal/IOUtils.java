package com.github.rmee.common.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

public class IOUtils {

	public static String toString(final InputStream input) throws IOException {
		try (final StringBuilderWriter sw = new StringBuilderWriter()) {
			copy(input, sw);
			return sw.toString();
		}
	}

	public static void copy(final InputStream input, final Writer output)
			throws IOException {
		final InputStreamReader in = new InputStreamReader(input);
		copy(in, output);
	}

	public static int copy(final Reader input, final Writer output) throws IOException {
		final long count = copyLarge(input, output);
		if (count > Integer.MAX_VALUE) {
			return -1;
		}
		return (int) count;
	}

	public static long copyLarge(final Reader input, final Writer output) throws IOException {
		return copyLarge(input, output, new char[1000]);
	}

	public static final int EOF = -1;

	public static long copyLarge(final Reader input, final Writer output, final char[] buffer) throws IOException {
		long count = 0;
		int n;
		while (EOF != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}
}
