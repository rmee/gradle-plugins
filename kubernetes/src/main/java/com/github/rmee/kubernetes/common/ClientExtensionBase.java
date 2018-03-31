package com.github.rmee.kubernetes.common;

import java.util.Arrays;
import java.util.List;

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

	protected List<String> parseCommand(String command) {
		String[] args = command.split("\\s+");
		args[0] = client.getBinPath();
		return Arrays.asList(args);
	}
}
