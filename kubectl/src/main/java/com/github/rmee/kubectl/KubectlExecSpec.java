package com.github.rmee.kubectl;

import com.github.rmee.cli.base.CliExecSpec;
import com.github.rmee.cli.base.OutputFormat;
import org.gradle.api.tasks.Input;

public class KubectlExecSpec extends CliExecSpec<KubectlExecSpec> {

    protected OutputFormat outputFormat = OutputFormat.CONSOLE;

    protected String input;

    @Input
    public OutputFormat getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(OutputFormat outputFormat) {
        this.outputFormat = outputFormat;
    }

    @Override
    protected void duplicate(KubectlExecSpec duplicate) {
        duplicate.outputFormat = outputFormat;
        duplicate.input = input;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
