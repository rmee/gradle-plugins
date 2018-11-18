package com.github.rmee.helm;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

public class HelmUpdateRepository extends DefaultTask {

    public HelmUpdateRepository() {
        setGroup("kubernetes");

        this.onlyIf(element -> {
            Project project = getProject();
            HelmExtension helm = project.getExtensions().getByType(HelmExtension.class);
            return helm.getRepository() != null;
        });
    }

    @TaskAction
    public void run() {
        Project project = getProject();
        HelmExtension helm = project.getExtensions().getByType(HelmExtension.class);
        HelmExtension extension = project.getExtensions().getByType(HelmExtension.class);

        String helmRepository = extension.getRepository();
        String helmUser = extension.getCredentials().getUser();
        String helmPass = extension.getCredentials().getPass();
        if (helmUser == null) {
            throw new IllegalArgumentException("helm.credentials.user not specified");
        }
        if (helmPass == null) {
            throw new IllegalArgumentException("helm.credentials.pass not specified");
        }

        HelmExecSpec execSpec = new HelmExecSpec();
        execSpec.setCommandLine(
                String.format("helm repo add remote %s --username %s --password %s", helmRepository, helmUser, helmPass));
        helm.exec(execSpec);

        execSpec = new HelmExecSpec();
        execSpec.setCommandLine(String.format("helm repo update remote"));
        helm.exec(execSpec);
    }
}
