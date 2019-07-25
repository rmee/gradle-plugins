package com.github.rmee.cli.base;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CliExecSpec<T extends CliExecSpec> {

    private List<String> commandLine;

    private boolean ignoreExitValue = false;

    private File stdoutFile;

    private String volumesFrom;

    private String containerName;

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
        return commandLine;
    }

    public void setCommandLine(String commandLine) {
        this.commandLine = Arrays.asList(commandLine.split("\\s+"));
    }

    public void setCommandLine(List<String> commandLine) {
        this.commandLine = commandLine;
    }

    public final T duplicate() {
        CliExecSpec duplicate = newSpec();
        duplicate.commandLine = new ArrayList(commandLine);
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
