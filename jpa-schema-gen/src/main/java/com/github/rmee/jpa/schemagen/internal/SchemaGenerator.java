package com.github.rmee.jpa.schemagen.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.github.rmee.jpa.schemagen.SchemaGenConfig;
import com.github.rmee.jpa.schemagen.SchemaTargetType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;

public class SchemaGenerator {

	public static void main(String[] args) throws IOException {
		SchemaGenerator gen = new SchemaGenerator();

		ObjectMapper mapper = new ObjectMapper();
		ObjectReader reader = mapper.readerFor(SchemaGenConfig.class);
		SchemaGenConfig extension = reader.readValue(new String(Base64.getDecoder().decode(args[1])));
		File outputDirectory = new File(args[0]);
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		gen.run(extension, classLoader, outputDirectory);
	}

	public void run(SchemaGenConfig extension, ClassLoader classLoader, File outputDirectory) throws IOException {
		File tempFile = File.createTempFile("ddl", ".sql");
		try {
			String dialect = extension.getDialect();
			Properties persistenceProperties = new Properties();
			if (dialect != null) {
				persistenceProperties.setProperty("hibernate.dialect", dialect);
			}
			persistenceProperties.setProperty("hibernate.hbm2ddl.delimiter", ";");
			persistenceProperties.setProperty("hibernate.format_sql", "true");
			persistenceProperties.setProperty("hibernate.hbm2ddl.auto", "none");
			persistenceProperties.setProperty("javax.persistence.schema-generation.scripts.action", "create");
			persistenceProperties
					.setProperty("javax.persistence.schema-generation.scripts.create-target", tempFile.getAbsolutePath());

			// execution in a separate thread as Hibernate searches the thread context classloader
			Thread generator = new Thread(() -> {
				URL resourceUrl = classLoader.getResource("META-INF/persistence.xml");
				if (resourceUrl == null) {
					throw new IllegalStateException("META-INF/persistence.xml not found, make sure to use proper Gradle configuration and dependencies.");
				}

				try {
					Class<?> persistenceClass = classLoader.loadClass("javax.persistence.Persistence");
					generateDirect(extension.getPersistenceUnitName(), persistenceProperties, persistenceClass);
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}

				SchemaTargetType target = extension.getTarget();
				SchemaTarget schemaTarget;
				if (target == SchemaTargetType.FLYWAY) {
					schemaTarget = newInstance("com.github.rmee.jpa.schemagen.internal.FlywaySchemaTarget");
				} else {
					schemaTarget = newInstance("com.github.rmee.jpa.schemagen.internal.LiquibaseSchemaTarget");
				}
				schemaTarget.process(tempFile, outputDirectory, extension);
			});
			generator.setContextClassLoader(classLoader);
			generator.start();
			try {
				generator.join();
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		} finally {
			FileUtils.delete(tempFile);
		}
	}

	private SchemaTarget newInstance(String name) {
		try {
			return (SchemaTarget) Class.forName(name).newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private void generateDirect(String persistenceUnitName, Properties persistenceProperties, Class<?> persistenceClass) throws Exception {
		String methodName = "generateSchema"; //NOSONAR NAME global variable refers to task name
		Method generateSchema = persistenceClass.getMethod(methodName, String.class, Map.class);
		generateSchema.invoke(persistenceClass, persistenceUnitName, persistenceProperties);
	}

}
