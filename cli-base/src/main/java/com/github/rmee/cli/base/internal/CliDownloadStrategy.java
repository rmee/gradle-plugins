package com.github.rmee.cli.base.internal;

import com.github.rmee.cli.base.Cli;

public interface CliDownloadStrategy {

    String computeDownloadFileName(Cli cli);

    String computeDownloadUrl(Cli cli, String repository, String downloadFileName);
}
