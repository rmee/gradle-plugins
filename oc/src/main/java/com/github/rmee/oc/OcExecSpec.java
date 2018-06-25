package com.github.rmee.oc;

import com.github.rmee.kubectl.KubectlExecSpec;

public class OcExecSpec extends KubectlExecSpec {

	@Override
	protected KubectlExecSpec newSpec() {
		return new OcExecSpec();
	}

	@Override
	public void duplicate(KubectlExecSpec duplicate) {
		// nothing more to do
		super.duplicate(duplicate);
	}

}
