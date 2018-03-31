package com.github.rmee.kubernetes.oc;

import com.github.rmee.kubernetes.kubectl.KubectlExecResult;

class OcExecResult extends KubectlExecResult {

	protected OcExecResult(String text) {
		super(text);
	}
}
