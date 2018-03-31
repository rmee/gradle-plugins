# kubernetes

Hosts a set of Gradle plugins to work with Kubernetes:

- `kubectl` command line client of Kubernetes.
- `oc` command line client as the the Openshift flavor of `kubectl`.
- `helm` to package and install Kubernetes objects.

The plugins give access to their commands out of Gradle.
The API resembles `ExecSpec` and allows to execute any command and evaluate
the results. 

A version can be specified for each client. It is used automatically download
the client with the proper platform. This means that the plugins are fully
self-contained, no Kubernetes, Openshift or Helm installation must be
present on the running machine.

The plugins are intentionally kept simple. They provide access to the 
native Kubernetes commands and not more. They do not attempt 
to wrap or establish new API. It makes the plugin easily usable for anybody
familiar with the Kubernetes tool; or the other way around, Kubernetes-related
Gradle tasks can easily be executed from the command line by the developer. 
For most to all applications the pattern should work very well.


# Setup

Add this library to the classpath to gain access to the various plugins:

```
buildscript {
	dependencies {
	    ...
		classpath 'com.github.rmee:kubernetes:<VERSION>'
	}
}
```

# Kubectl

WARNING: This plugin is still work in progress.

- `KubectlBootstrap` downloads the client.
- `KubectlExec` allow to issue a command. Alternatively, `kubectl.exec(...)` can be used.
- `KubectlUseContext` to connect to a given cluster, namespace with username/password or a token. Matches `OcLogin`. 

For an example have a look OpenShift example.

For more detailed information, have a look at the `KubectlExtension`.


# OpenShift

The `oc` plugin works exactly the same as the `kubectl` plugin. Sole exception
is the login handling. There is a `ocLogin` instead of 
`kubectlUseContext`.

- `OcBootstrap` downloads the client.
- `OcExec` allow to issue a command. Alternatively, `oc.exec(...)` can be used.
- `OcLogin` performs the login.
- `OcSetProject` sets the currently used project.
- `OcNewProject` creates a new project.

An example looks like:

```
apply plugin: 'oc'
oc {
	projectName = 'demo-app'
	url = OPENSHIFT_URL
	credentials {
		userName = 'john'
		password = 'doe'
		token = 'someToken // alternative to userName/password
	}
	client {
		// TODO setup mirror: repository = "http://..."
	}
}

task ocSetupTiller(type: OcExec) {
	group = 'provision'
	mustRunAfter ocNewProject
	commandLine "oc process -f ${file('src/main/...')} -p TILLER_NAMESPACE=${oc.projectName} | oc create -f -"
}

```

For more detailed information, have a look at the `OcExtension`.


 
# Helm

The `helm` plugin makes this tasks available:

- `HelmBootstrap` downloads the client.
- `HelmInit` to setup Helm locally on the client-side.
- `HelmExec` allow to issue a command. Alternatively, `helm.exec(...)` can be used.
- `HelmPackage` to package the Helm sources. Helm packages must be specified in `src/main/helm`. Each subdirectory will make up one helm package.
  The `helmPackage` command will then build each of the packages and put the result into
  `build/distributions`.



An example usage may look like:

```
apply plugin: 'helm'
helm {
	tillerNamespace = oc.projectName

	client {
		// repository = 'http://..' optionally set a mirror to download the helm client from
		// version = '2.8.2` // version to download
		// download = true 
	}
}

task helmStatus(type: HelmExec) {
	dependsOn ocLogin, helmPackage
	commandLine "helm status demo"
}

task helmInstall(type: HelmExec) {
	dependsOn ocLogin, helmPackage
	commandLine "helm install ${helmPackage.outputs.files.singleFile} --name demo"
}

task helmUpgrade(type: HelmExec) {
	dependsOn ocLogin, helmPackage
	commandLine "helm upgrade demo ${helmPackage.outputs.files.singleFile}"
}
```

For more detailed information, have a look at the `HelmExtension`.


# Example

For an example app have a look at 
https://github.com/rmee/gradle-plugins/tree/master/kubernetes/src/test/resources/
and `HelmSysTest`.
