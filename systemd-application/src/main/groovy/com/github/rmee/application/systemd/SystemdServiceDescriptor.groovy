package com.github.rmee.application.systemd

class SystemdServiceDescriptor {

	HashMap<String, String> unit = new HashMap()

	HashMap<String, String> service = new HashMap()

	HashMap<String, String> install = new HashMap()

	String toText() {
		StringBuilder builder = new StringBuilder()
		writeSection(builder, "Unit", unit)
		writeSection(builder, "Service", service)
		writeSection(builder, "Install", install)
		return builder.toString()
	}

	private String writeSection(StringBuilder builder, String section, HashMap<String, String> properties) {
		if (!properties.isEmpty()) {
			builder.append("[${section}]\n")
			for (Map.Entry entry : properties.entrySet()) {
				builder.append("${entry.key}=${entry.value}\n")
			}
			builder.append("\n")
		}
	}
}
