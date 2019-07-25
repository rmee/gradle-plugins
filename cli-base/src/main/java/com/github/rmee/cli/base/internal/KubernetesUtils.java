package com.github.rmee.cli.base.internal;

import com.github.rmee.cli.base.Cli;

import java.io.File;

public class KubernetesUtils {

    public static void setKubeConfig(Cli cli, File kubeConfig) {
        if (cli.isDockerized()) {
            if (!kubeConfig.getName().equals("config")) {
                throw new IllegalStateException("kubeConfig must be named 'config', got " + kubeConfig.getAbsolutePath());
            }
            //	cli.getVolumeMappings().put(KUBE_DIR, kubeConfig.getParentFile());

        } else {
            cli.getEnvironment().put("KUBECONFIG", kubeConfig.getAbsolutePath());
        }
    }
}
