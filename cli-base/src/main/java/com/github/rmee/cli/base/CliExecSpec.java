package com.github.rmee.cli.base;

import org.gradle.api.tasks.Input;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class CliExecSpec<T extends CliExecSpec> {

	private Supplier<List<String>> commandLine;

	private boolean ignoreExitValue = false;

	private File stdoutFile;

	private String volumesFrom;

	private String containerName;

	protected OutputFormat outputFormat = OutputFormat.CONSOLE;

	@Input
	public OutputFormat getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(OutputFormat outputFormat) {
		this.outputFormat = outputFormat;
	}

	public String getVolumesFrom() {
		return volumesFrom;
	}

	public void setVolumesFrom(String volumesFrom) {
		this.volumesFrom = volumesFrom;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public boolean isIgnoreExitValue() {
		return ignoreExitValue;
	}

	public void setIgnoreExitValue(boolean ignoreExitValue) {
		this.ignoreExitValue = ignoreExitValue;
	}

	public List<String> getCommandLine() {
		return commandLine.get();
	}

	public void setCommandLine(String commandLine) {
		this.commandLine = () -> Arrays.asList(commandLine.split("\\s+"));
	}

	public void setCommandLine(Supplier<String> commandLine) {
		this.commandLine = () -> Arrays.asList(commandLine.get().split("\\s+"));
	}

	public void setCommandLine(List<String> commandLine) {
		this.commandLine = () -> commandLine;
	}

	public final T duplicate() {
		CliExecSpec duplicate = newSpec();
		duplicate.commandLine = commandLine != null ? () -> new ArrayList(commandLine.get()) : null;
		duplicate.outputFormat = outputFormat;
		duplicate.ignoreExitValue = ignoreExitValue;
		duplicate.volumesFrom = volumesFrom;
		duplicate.containerName = containerName;
		duplicate((T) duplicate);
		return (T) duplicate;
	}

	protected T newSpec() {
		try {
			return (T) getClass().newInstance();
		} catch (IllegalAccessException | InstantiationException e) {
			throw new IllegalStateException(e);
		}
	}

	protected void duplicate(T duplicate) {
	}

	public File getStdoutFile() {
		return stdoutFile;
	}

	public void setStdoutFile(File stdoutFile) {
		this.stdoutFile = stdoutFile;
	}
}
