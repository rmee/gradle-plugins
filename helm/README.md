# Helm

Provides access to `helm`, a package manager for Kubernetes.

The plugin works together with the `kubectl` plugin and reuses its context configuration.


## Classpath

Add this library to the classpath:

```
buildscript {
	dependencies {
	    ...
		classpath 'gradle.plugin.com.github.rmee:helm:<VERSION>'
	}
}
```


## Setup

An example usage may look like:

```
apply plugin: 'helm'
helm {
	tillerNamespace = oc.projectName

	cli {
		version = '2.9.1` // version to use
		imageName = 'dtzar/helm-kubectl'
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


## Tasks

The `helm` plugin makes this tasks available:

- `HelmBootstrap` downloads the cli.
- `HelmInit` to setup Helm locally on the cli-side.
- `HelmExec` allow to issue a command. Alternatively, `helm.exec(...)` can be used.
- `HelmPackage` to package the Helm sources. Helm packages must be specified in `src/main/helm`. Each subdirectory will make up one helm package.
  The `helmPackage` command will then build each of the packages and put the result into
  `build/distributions`.
- `HelmExec` to setup custom commands. Alternatively, `helm.exec(...)` can be used.


# Example

For an example app have a look at https://github.com/crnk-project/crnk-example[crnk-example] how to deploy
to Google Cloud with Kubernetes and Helm.


## Wrapper

A wrapper `helm` will be generated into the project root to allow easy access from the command line next to Gradle tasks.







