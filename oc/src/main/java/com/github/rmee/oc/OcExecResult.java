package com.github.rmee.oc;

import com.github.rmee.kubectl.KubectlExecResult;

class OcExecResult extends KubectlExecResult {

	protected OcExecResult(String text) {
		super(text);
	}
}
