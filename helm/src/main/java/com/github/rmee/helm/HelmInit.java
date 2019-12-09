package com.github.rmee.helm;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class HelmInit extends DefaultTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelmInit.class);

    private boolean skipRefresh;

    private String commandLine;
	
	private boolean helm3;

    public HelmInit() {
        setGroup("kubernetes");

        getOutputs().upToDateWhen(task -> {
            HelmExtension extension = getProject().getExtensions().getByType(HelmExtension.class);
            File repositoriesFile = extension.getCli().getHome(".helm/repository/repositories.yaml");
            LOGGER.debug("helmInit up-to-data: {}", repositoriesFile.exists());
            return repositoriesFile.exists();
        });
    }

    @TaskAction
    public void exec() {
		if (commandLine == null) {
            commandLine = "helm init";
            if(!helm3) {
            	commandLine += " --client-only";
				if (skipRefresh) {
					commandLine += " --skip-refresh";
				}
			}
        }
        HelmExtension extension = getProject().getExtensions().getByType(HelmExtension.class);
        HelmExecSpec spec = new HelmExecSpec();
        spec.setCommandLine(commandLine);
        extension.exec(spec);
    }

    public String getCommandLine() {
        return commandLine;
    }

    public void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }

    public boolean isSkipRefresh() {
        return skipRefresh;
    }

    public void setSkipRefresh(boolean skipRefresh) {
        this.skipRefresh = skipRefresh;
    }
	
	public boolean isHelm3() {
		return helm3;
	}

	public void setHelm3(boolean helm3) {
		this.helm3 = helm3;
	}
}
