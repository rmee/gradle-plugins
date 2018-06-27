package com.github.rmee.kubectl;

public class KubectlStartProxyTask extends KubectlExec {

	public KubectlStartProxyTask() {
		// by default the proxy binds to loopback which cannot be exposed with docker
		commandLine("kubectl proxy --address=\"0.0.0.0\"");

		//http://localhost:8001/api/v1/namespaces/kube-system/services/http:kubernetes-dashboard:/proxy/#!/secret/default
		// /container-registry-secrets?namespace=default
	}
}
