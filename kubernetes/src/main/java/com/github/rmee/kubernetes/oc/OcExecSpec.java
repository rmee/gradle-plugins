package com.github.rmee.kubernetes.oc;

import com.github.rmee.kubernetes.kubectl.KubectlExecSpec;

public class OcExecSpec extends KubectlExecSpec {

	@Override
	public OcExecSpec duplicate() {
		OcExecSpec duplicate = new OcExecSpec();
		duplicate.commandLine = commandLine;
		duplicate.ignoreExitValue = ignoreExitValue;
		duplicate.outputFormat = outputFormat;
		return duplicate;
	}

}
