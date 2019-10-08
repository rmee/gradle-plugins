package com.github.rmee.jpa.schemagen;


public class SchemaGenConfig {

	private String dialect = null;

	private String packageName;

	private boolean continuousMode = true;

	/**
	 * if true will forked a new process to perform Hibernate generation.
	 */
	private boolean forked = false;

	private String persistenceUnitName;

	private String configuration = "runtime";

	private SchemaTargetType target = SchemaTargetType.FLYWAY;

	private LiquibaseExtension liquibase = new LiquibaseExtension();


	private String version;

	/**
	 * prefix to add to constraints names.
	 */
	private String constraintNamePrefix = null;

	public String getVersion() {
		return version;
	}

	public boolean isForked() {
		return forked;
	}

	public void setForked(boolean forked) {
		this.forked = forked;
	}

	public String getConstraintNamePrefix() {
		return constraintNamePrefix;
	}

	public void setConstraintNamePrefix(String constraintNamePrefix) {
		this.constraintNamePrefix = constraintNamePrefix;
		if (target != SchemaTargetType.LIQUIBASE) {
			throw new IllegalStateException("constraintNamePrefix only implemented for Liquibase yet");
		}
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public LiquibaseExtension getLiquibase() {
		return liquibase;
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
