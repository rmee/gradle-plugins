package com.github.rmee.jpa.schemagen.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.rmee.jpa.schemagen.SchemaGenExtension;

public class FlywaySchemaTarget implements SchemaTarget {

	@Override
	public void process(File generatedFile, File outputDirectory, SchemaGenExtension config) {
		// split the single hibernate generated files into multiple, properly formatted ones

		String version = config.getVersion();

		File outputPackageDirectory = new File(outputDirectory, config.getPackageName().replace(".", File.separator));
		outputDirectory.mkdirs();
		File tableFile = new File(outputPackageDirectory, "v" + version + ".200__create_tables.sql");
		File constraintFile = new File(outputPackageDirectory, "v" + version + ".300__create_constraints.sql");
		File indexFile = new File(outputPackageDirectory, "v" + version + ".400__create_indices.sql");
		FileUtils.delete(tableFile);
		FileUtils.delete(constraintFile);
		FileUtils.delete(indexFile);
		format(generatedFile, tableFile, constraintFile, indexFile);
	}

	public static void format(File file, File tableFile, File constraintFile, File indexFile) {
		try (
				BufferedReader reader = new BufferedReader(new FileReader(file));
				FileWriter tableStmts = new FileWriter(tableFile);
				FileWriter indexStmts = new FileWriter(indexFile);
				FileWriter constraintStmts = new FileWriter(constraintFile);) {

			checkExists(file);

			List<String> lines = readLines(reader);

			checkNotEmpty(lines, file);
			FileWriter writer = null;
			for (int i = 0; i < lines.size(); i++) {
				String line = lines.get(i);

				if (isCreateIndexLine(line)) {
					writer = indexStmts;
				}
				else if (isAddConstraintLine(line, lines, i)) {
					writer = constraintStmts;
				}
				else if (writer == null || isCreateTableLine(line)) {
					writer = tableStmts;
				}

				writer.append(line);
				writer.append("\n");

				if (":".equals(line)) {
					writer = null;
				}
			}
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private static void checkExists(File file) {
		if (!file.exists()) {
			throw new IllegalStateException("generated ddls not found in " + file.getAbsolutePath());
		}
	}

	private static void checkNotEmpty(List<String> lines, File file) {
		if (lines.isEmpty()) {
			throw new IllegalStateException("no ddls found in " + file.getAbsolutePath());
		}
	}

	private static boolean isAddConstraintLine(String line, List<String> lines, int i) {
		return line.startsWith("alter table") && i < lines.size() - 1 && lines.get(i + 1).contains("add constraint");
	}

	private static boolean isCreateTableLine(String line) {
		return line.startsWith("create table");
	}

	private static boolean isCreateIndexLine(String line) {
		return line.startsWith("create index");
	}

	private static List<String> readLines(BufferedReader reader) throws IOException {
		List<String> lines = new ArrayList<>();
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("    ")) {
				line = line.substring(4);
			}
			lines.add(line);
		}
		return lines;
	}
}
