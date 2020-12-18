package com.github.rmee.jpa.schemagen;


import com.fasterxml.jackson.annotation.JsonIgnore;
import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.Project;

public class SchemaGenExtension extends SchemaGenConfig {

	@JsonIgnore
	protected Project project;

	@Override
	public String getVersion() {
		String version = super.getVersion();
		if (version == null) {
			version = (String) project.getVersion();
		}
		if (version == null || version.equals("unspecified")) {
			version = "0.0.1";
		}
		return version;
	}

	public void liquibase(Closure<Action<LiquibaseExtension>> closure) {
		project.configure(getLiquibase(), closure);
	}

	/**
	 * We validate the Gradle settings. But we don't carry along this verification with SchemaGenConfig due avoid
	 * issues when transporting this object to the fork process.
	 *
	 * @param constraintNamePrefix
	 */
	public void setConstraintNamePrefix(String constraintNamePrefix) {
		super.setConstraintNamePrefix(constraintNamePrefix);
		if (getTarget() != SchemaTargetType.LIQUIBASE) {
			throw new IllegalStateException("constraintNamePrefix only implemented for Liquibase yet");
		}
	}
}
