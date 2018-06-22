package com.github.rmee.kubernetes.common;

import groovy.lang.Closure;
import org.gradle.api.Project;

public abstract class ClientExtensionBase {

	protected Project project;

	protected boolean initialized = false;

	protected Client client;

	protected abstract void init();

	protected void checkNotInitialized() {
		if (initialized) {
			throw new IllegalStateException("already initialized, cannot modify anymore");
		}
	}

	public Client getClient() {
		return client;
	}

	public Client client(Closure closure) {
		return (Client) project.configure(client, closure);
	}
}
