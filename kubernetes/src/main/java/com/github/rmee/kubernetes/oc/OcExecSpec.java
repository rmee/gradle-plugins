package com.github.rmee.kubernetes.oc;

import com.github.rmee.kubernetes.kubectl.KubectlExecSpec;

public class OcExecSpec extends KubectlExecSpec {

	@Override
	public OcExecSpec duplicate() {
		OcExecSpec duplicate = new OcExecSpec();
		duplicate.setCommandLine(getCommandLine());
		duplicate.setIgnoreExitValue(isIgnoreExitValue());
		duplicate.setStdoutFile(getStdoutFile());
		duplicate.outputFormat = outputFormat;
		return duplicate;
	}

}
