package com.github.rmee.cli.base;

import java.util.HashMap;
import java.util.Map;

public class CliExecExtension {

	private Map<String, Cli> clients = new HashMap<>();

	public void register(String name, Cli cli) {
		clients.put(name, cli);
	}

	public Map<String, Cli> getClients() {
		return clients;
	}

	public void exec(CliExecSpec spec) {
		String cliName = (String) spec.getCommandLine().get(0);

		Cli cli = clients.get(cliName);
		if (cli == null) {
			throw new IllegalStateException("no CLI with name '" + cliName + "' registered");
		}

		cli.exec(spec);
	}
}
