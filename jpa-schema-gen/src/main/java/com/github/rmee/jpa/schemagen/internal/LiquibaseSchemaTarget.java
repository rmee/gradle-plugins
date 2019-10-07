package com.github.rmee.jpa.schemagen.internal;

import com.github.rmee.jpa.schemagen.SchemaGenExtension;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.diff.DiffGeneratorFactory;
import liquibase.diff.DiffResult;
import liquibase.diff.compare.CompareControl;
import liquibase.diff.output.DiffOutputControl;
import liquibase.diff.output.changelog.DiffToChangeLog;
import liquibase.serializer.core.xml.XMLChangeLogSerializer;
import org.h2.jdbcx.JdbcDataSource;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class LiquibaseSchemaTarget implements SchemaTarget {

	@Override
	public void process(File generatedFile, File outputDirectory, SchemaGenExtension config) {
		try (Connection connection = setupDataSource(generatedFile); Connection emptyConnection = setupEmptySource()) {
			DatabaseFactory databaseFactory = DatabaseFactory.getInstance();
			Database database = databaseFactory.findCorrectDatabaseImplementation(new JdbcConnection(connection));
			Database emptyDatabase = databaseFactory.findCorrectDatabaseImplementation(new JdbcConnection(emptyConnection));

			DiffGeneratorFactory diffGeneratorFactory = DiffGeneratorFactory.getInstance();

			CompareControl compareControl = new CompareControl();
			DiffResult result = diffGeneratorFactory.compare(database, emptyDatabase, compareControl);

			DiffOutputControl outputControl = new DiffOutputControl();
			DiffToChangeLog changeLog = new DiffToChangeLog(result, outputControl);
			changeLog.setChangeSetAuthor(config.getLiquibase().getUser());
			changeLog.setIdRoot(config.getVersion());
			changeLog.generateChangeSets();

			File outputFile = new File(outputDirectory,
					config.getPackageName().replace(".", File.separator) + File.separator + config.getLiquibase().getFileName());
			outputFile.getParentFile().mkdirs();
			if (outputFile.exists()) {
				boolean deleted = outputFile.delete();
				if (!deleted) {
					throw new IllegalStateException("cannot delete " + outputFile.getAbsolutePath());
				}
			}
			changeLog.print(outputFile.getAbsolutePath(), new XMLChangeLogSerializer());

			if (config.getConstraintNamePrefix() != null) {
				String sql = FileUtils.readAsString(outputFile);
				sql = sql.replace("primaryKeyName=\"CONSTRAINT_", "primaryKeyName=\"" + config.getConstraintNamePrefix() + "CONSTRAINT_");
				FileUtils.writeString(sql, outputFile);
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private Connection setupDataSource(File file) throws SQLException, IOException {
		JdbcDataSource dataSource = new JdbcDataSource();
		dataSource.setUrl("jdbc:h2:mem:test");
		String script = FileUtils.readAsString(file);
		Connection connection = dataSource.getConnection();
		SqlUtils.executeSql(connection, script, ";");
		return connection;

	}

	private Connection setupEmptySource() throws SQLException {
		JdbcDataSource dataSource = new JdbcDataSource();
		dataSource.setUrl("jdbc:h2:mem:empty");
		return dataSource.getConnection();
	}
}
