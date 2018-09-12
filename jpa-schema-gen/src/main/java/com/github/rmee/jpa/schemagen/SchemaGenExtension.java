package com.github.rmee.jpa.schemagen;


import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.Project;

public class SchemaGenExtension {

	private String dialect = null;

	private String packageName;

	private boolean continuousMode = true;

	private String persistenceUnitName;

	private String configuration = "runtime";

	private SchemaTargetType target = SchemaTargetType.FLYWAY;

	private LiquibaseExtension liquibase = new LiquibaseExtension();

	protected Project project;

	private String version;

	public String getVersion() {
		if (version == null) {
			version = (String) project.getVersion();
		}
		if (version == null || version.equals("unspecified")) {
			version = "0.0.1";
		}
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public LiquibaseExtension getLiquibase() {
		return liquibase;
	}

	public void liquibase(Closure<Action<LiquibaseExtension>> closure) {
		project.configure(liquibase, closure);
	}

	public boolean isContinuousMode() {
		return continuousMode;
	}

	public SchemaTargetType getTarget() {
		return target;
	}

	public void setTarget(SchemaTargetType target) {
		this.target = target;
	}

	public String getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration to use to build the classpath to run the Hibernate generator.
	 */
	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public String getDialect() {
		return dialect;
	}

	public void setDialect(String dialect) {
		this.dialect = dialect;
	}

	public String getPackageName() {
		if (packageName == null) {
			throw new IllegalStateException("schemaConfig.packageName must be set");
		}
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public boolean getContinuousMode() {
		return continuousMode;
	}

	public void setContinuousMode(boolean continuousMode) {
		this.continuousMode = continuousMode;
	}

	public void setPersistenceUnitName(String persistenceUnitName) {
		this.persistenceUnitName = persistenceUnitName;
	}

	public String getPersistenceUnitName() {
		return persistenceUnitName;
	}
}
