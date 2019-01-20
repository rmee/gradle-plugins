package com.github.rmee.gcloud;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class GCloudSetProjectTask extends DefaultTask {

    private GCloudExtension getExtension() {
        return getProject().getExtensions().getByType(GCloudExtension.class);
    }

    @Input
    public String getProjectName() {
        return getExtension().getProject();
    }

    // TODO deletes config_default from time to time => find better way
    // @OutputFile
    //public File getConfigDefaultFile() {
    //	return getProject().file("build/wrapper/.config/gcloud/configurations/config_default" );
    //}

    @TaskAction
    public void run() {
        GCloudExecSpec execSpec = new GCloudExecSpec();
        execSpec.setCommandLine("gcloud config set project " + getProjectName());
        getExtension().exec(execSpec);
    }
}
