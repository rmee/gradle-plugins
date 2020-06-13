package com.github.rmee.cli.base;

import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.Project;

import java.util.HashMap;
import java.util.Map;

public class CliExecExtension {

	private Map<String, Cli> clients = new HashMap<>();

	public void register(String name, Cli cli) {
		clients.put(name, cli);
	}

	protected Project project;

	/**
	 * Sets a shared image name for all clients
	 */
	public void setImageName(String imageName) {
		clients.values().forEach(it -> it.setImageName(imageName));
	}

	/**
	 * Sets a shared image tag for all clients.
	 */
	public void setImageTag(String imageTag) {
		clients.values().forEach(it -> it.setImageTag(imageTag));
	}

	/**
	 * Sets whether the clients run within docker.
	 */
	public void setDockerized(boolean dockerized) {
		clients.values().forEach(it -> it.setDockerized(dockerized));
	}

	/**
	 * Sets whether the clients are downloaded.
	 */
	public void setDownload(boolean download) {
		clients.values().forEach(it -> it.setDownload(download));
	}

	/**
	 * Sets the environment
	 */
	public void setEnvironment(Map<String, String> env) {
		clients.values().forEach(it -> it.setEnvironment(env));
	}

	public void register(String name) {
		Cli cli = new Cli(name, () -> project);
		clients.put(name, cli);
	}

	public void register(String name, Action<Cli> action) {
		Cli cli = new Cli(name, () -> project);
		action.execute(cli);
		clients.put(name, cli);
	}

	public void register(String name, Closure<Cli> action) {
		Cli cli = new Cli(name, () -> project);
		project.configure(cli, action);
		clients.put(name, cli);
	}


	public Map<String, Cli> getClients() {
		return clients;
	}

	public ExecResult exec(CliExecSpec spec) {
		String cliName = (String) spec.getCommandLine().get(0);

		Cli cli = clients.get(cliName);
		if (cli == null) {
			throw new IllegalStateException("no CLI with name '" + cliName + "' registered");
		}

		return cli.exec(spec);
	}

	public ExecResult exec(Closure<CliExecSpec> closure) {
		CliExecSpec spec = new CliExecSpec();
		project.configure(spec, closure);
		return exec(spec);
	}

	public void init() {
		clients.values().forEach(it -> it.init());
	}
}
