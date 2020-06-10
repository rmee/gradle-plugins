package com.github.rmee.cli.base;

import groovy.lang.Closure;
import org.gradle.api.Project;

import java.io.File;

public abstract class ClientExtensionBase {

	protected Project project;

	protected boolean initialized = false;

	protected Cli cli;

	protected abstract void init();

	protected void checkNotInitialized() {
		if (initialized) {
			throw new IllegalStateException("already initialized, cannot modify anymore");
		}
	}

	public File getProjectDir(){
		return project.getProjectDir();
	}

	public Cli getCli() {
		return cli;
	}

	public Cli client(Closure closure) {
		return (Cli) project.configure(cli, closure);
	}

	public Cli cli(Closure closure) {
		return (Cli) project.configure(cli, closure);
	}
}
