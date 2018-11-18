package com.github.rmee.helm;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;

public class HelmPackage extends DefaultTask {

    private String packageName;

    public HelmPackage() {
        setGroup("kubernetes");
    }

    @TaskAction
    public void exec() {
        HelmExtension extension = getProject().getExtensions().getByType(HelmExtension.class);

        String outputDir;
        if (extension.getClient().isDockerized()) {
            outputDir = HelmPlugin.HELM_OUTPUT_DIR;
        } else {
            File fileOutputDir = extension.getOutputDir();
            fileOutputDir.mkdirs();
            outputDir = fileOutputDir.getAbsolutePath();
        }

        Project project = getProject();

        File sourceDir = getSourceDir();

        // consider start supporting dependencies
        //File requirementsFile = new File(sourceDir, "requirements.yaml");
        //if (requirementsFile.exists() && requirementsFile.length() > 0) {
        //    HelmExecSpec updateSpec = new HelmExecSpec();
        //    updateSpec.setCommandLine("helm dependency update --skip-refresh --debug " + sourceDir.getAbsolutePath());
        //    extension.exec(updateSpec);
        //}

        HelmExecSpec packageSpec = new HelmExecSpec();
        packageSpec.setCommandLine("helm package " + sourceDir.getAbsolutePath() + " --destination " + outputDir
                + " --version " + project.getVersion());
        extension.exec(packageSpec);
    }


    @InputDirectory
    public File getSourceDir() {
        HelmExtension extension = getProject().getExtensions().getByType(HelmExtension.class);
        return new File(extension.getSourceDir(), packageName);
    }

    @OutputFile
    public File getOutputFile() {
        HelmExtension extension = getProject().getExtensions().getByType(HelmExtension.class);
        return extension.getOutputFile(packageName);
    }

    @Input
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
