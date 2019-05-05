package com.github.rmee.gcloud.gke;

import com.github.rmee.gcloud.GCloudExecSpec;
import com.github.rmee.gcloud.GCloudExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;

/**
 *
 */
public class GCloudGetKubernetesCredentialsTask extends DefaultTask {

    private GCloudExtension getExtension() {
        return getProject().getExtensions().getByType(GCloudExtension.class);
    }

    @Optional
    @Input
    public String getRegion() {
        return getExtension().getRegion();
    }

    @Optional
    @Input
    public String getZone() {
        return getExtension().getZone();
    }

    @Optional
    @Input
    public String getGkeProject() {
        return getExtension().getProject();
    }

    @Input
    public String getClusterName() {
        return getExtension().getGke().getClusterName();
    }

    @OutputFile
    public File getConfigFile() {
        return getProject().file("build/wrapper/.kube/config");
    }

    @TaskAction
    public void run() {
        StringBuilder builder = new StringBuilder();
        builder.append("gcloud container clusters get-credentials ");
        builder.append(getClusterName());

        String region = getRegion();
        if (region != null) {
            builder.append(" --region=");
            builder.append(region);
        }

        String zone = getZone();
        if (zone != null) {
            builder.append(" --zone=");
            builder.append(zone);
        }

        GCloudExecSpec execSpec = new GCloudExecSpec();
        execSpec.setCommandLine(builder.toString());
        getExtension().exec(execSpec);
    }
}
