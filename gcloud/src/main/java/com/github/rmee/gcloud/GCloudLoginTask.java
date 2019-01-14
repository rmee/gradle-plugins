package com.github.rmee.gcloud;

import org.gradle.api.tasks.TaskAction;

public class GCloudLoginTask extends GCloudLoginTaskBase {

    @TaskAction
    public void run() {
        StringBuilder command = new StringBuilder();
        command.append("gcloud auth login");

        GCloudExecSpec execSpec = new GCloudExecSpec();
        execSpec.setCommandLine(command.toString());
        getExtension().exec(execSpec);
    }
}
