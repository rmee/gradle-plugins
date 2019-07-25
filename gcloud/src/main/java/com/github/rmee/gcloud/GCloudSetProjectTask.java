package com.github.rmee.gcloud;

import com.github.rmee.cli.base.internal.IOUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileInputStream;

public class GCloudSetProjectTask extends DefaultTask {

    public GCloudSetProjectTask() {
        getOutputs().upToDateWhen(task -> {
            GCloudExtension extension = getExtension();
            File file = extension.getCli().getHome(".config/gcloud/configurations/config_default");
            if (file.exists()) {
                try {
                    try (FileInputStream in = new FileInputStream((file))) {
                        String text = IOUtils.toString(in);
                        return text.contains("project = " + extension.getProject());
                    }
                } catch (Exception e) {
                    throw new IllegalStateException("failed to perform up-to-date check", e);
                }
            }
            return false;
        });
    }

    private GCloudExtension getExtension() {
        return getProject().getExtensions().getByType(GCloudExtension.class);
    }

    @Input
    public String getProjectName() {
        return getExtension().getProject();
    }

    @TaskAction
    public void run() {
        GCloudExecSpec execSpec = new GCloudExecSpec();
        execSpec.setCommandLine("gcloud config set project " + getProjectName());
        getExtension().exec(execSpec);
    }
}
