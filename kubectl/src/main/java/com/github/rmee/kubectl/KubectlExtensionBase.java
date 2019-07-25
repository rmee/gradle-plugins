package com.github.rmee.kubectl;

import com.github.rmee.cli.base.Cli;
import com.github.rmee.cli.base.ClientExtensionBase;
import com.github.rmee.cli.base.Credentials;
import com.github.rmee.cli.base.OutputFormat;
import groovy.lang.Closure;
import org.gradle.api.Project;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public abstract class KubectlExtensionBase extends ClientExtensionBase {

    private String url;

    protected Credentials credentials;

    private String namespace;

    private boolean insecureSkipTlsVerify = false;


    public KubectlExtensionBase() {
        credentials = new Credentials(this);
        cli = createClient();
    }

    protected abstract Cli createClient();

    public boolean isInsecureSkipTlsVerify() {
        return insecureSkipTlsVerify;
    }

    public void setInsecureSkipTlsVerify(boolean insecureSkipTlsVerify) {
        this.insecureSkipTlsVerify = insecureSkipTlsVerify;
    }


    public Credentials credentials(Closure closure) {
        return (Credentials) project.configure(credentials, closure);
    }

    public String getUrl() {
        init();
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Credentials getCredentials() {
        init();
        return credentials;
    }

    public String getNamespace() {
        init();
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getToken(String serviceAccount) {
        KubectlExecSpec spec = new KubectlExecSpec();
        spec.setCommandLine(cli.getBinName() + " describe serviceaccount " + serviceAccount);
        KubectlExecResult result = exec(spec);
        String tokenName = result.getProperty("tokens");

        spec.setCommandLine(cli.getBinName() + " describe secret " + tokenName);
        result = exec(spec);
        return result.getProperty("token");
    }

    public KubectlExecResult exec(String command) {
        KubectlExecSpec spec = new KubectlExecSpec();
        spec.setCommandLine(command);
        return exec(spec);
    }

    protected KubectlExecResult exec(KubectlExecSpec execSpec1) {
        // prevent from giving changes back to caller
        final KubectlExecSpec spec = execSpec1.duplicate();

        List<String> commandLine = spec.getCommandLine();
        int pipeIndex = commandLine.indexOf("|");
        if (pipeIndex == -1) {
            if (!commandLine.contains("token") && !commandLine.contains("pass")) {
                project.getLogger().warn("Executing: " + commandLine.stream().collect(Collectors.joining(" ")));
            } else {
                project.getLogger().debug("Executing: " + commandLine.stream().collect(Collectors.joining(" ")));
            }

            if (spec.getOutputFormat() == OutputFormat.JSON) {
                commandLine.add("--output=json");
            }

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                project.exec(execSpec -> {
                    cli.configureExec(execSpec, spec);
                    if (spec.getInput() != null) {
                        execSpec.setStandardInput(new ByteArrayInputStream(spec.getInput().getBytes()));
                    }
                    if (spec.getOutputFormat() != OutputFormat.CONSOLE) {
                        execSpec.setStandardOutput(outputStream);
                    }
                });
                String output = outputStream.toString();
                return createResult(output);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        } else {
            KubectlExecSpec leftSpec = spec.duplicate();
            leftSpec.setCommandLine(commandLine.subList(0, pipeIndex));
            leftSpec.setOutputFormat(OutputFormat.TEXT);
            KubectlExecResult leftResult = exec(leftSpec);

            KubectlExecSpec rightSpec = spec.duplicate();
            rightSpec.setCommandLine(commandLine.subList(pipeIndex + 1, commandLine.size()));
            rightSpec.setInput(leftResult.getText());
            return exec(rightSpec);
        }
    }

    protected KubectlExecResult createResult(String output) {
        return new KubectlExecResult(output);
    }

    @Override
    public void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        cli.init(project);
    }

    protected void setProject(Project project) {
        this.project = project;
    }
}
