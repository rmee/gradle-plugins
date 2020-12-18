# Helm

Provides access to `helm`, a package manager for Kubernetes.

The plugin works together with the `kubectl` plugin and reuses its context configuration.


## Classpath

Add this library to the classpath:

```
buildscript {
	dependencies {
	    ...
		classpath 'gradle.plugin.com.github.rmee:cli-base:<VERSION>'
	}
}
```


## Setup

An example that make use of existing `kubectl` and `helm` support and further adds `doctl` to deploy
to digital ocean may look like:

```
apply plugin: 'cli-exec'
apply plugin: 'kubectl'
apply plugin: 'helm'
cliExec {
	register 'doctl'
	imageName = 'cord3c/cord3c-ci-base'
	imageTag = BASE_IMAGE_VERSION
}


helmPackageCord3cExample {
	def nodeImageIdFile = file("../cord3c-example-node/build/image-input.id")
	def resolverImageIdFile = file("../cord3c-ssi-networkmap-resolver/build/image-input.id")

	inputs.file nodeImageIdFile
	inputs.file resolverImageIdFile

	dependsOn ':cord3c-example-node:jib', ':cord3c-ssi-networkmap-resolver:jib'
	doFirst {
		values.put('node.image.tag', nodeImageIdFile.text.substring("sha256:".length()).trim())
		values.put('resolver.image.tag', resolverImageIdFile.text.substring("sha256:".length()).trim())
	}
}

task deploy() {
	dependsOn helmPackage
	doFirst {
		cliExec.exec({ commandLine = "doctl auth init --access-token 57b5cf7bf91bb3c26562a5664d504e747694edf2ed1d406649698c562e63dcd6" })
		cliExec.exec({ commandLine = "doctl kubernetes cluster kubeconfig save cord3c-example" })
		cliExec.exec({ commandLine = "kubectl apply -f=${file('src/main/kubernetes/example-namespace.yaml')}" })
		helm.exec({
			commandLine = "helm upgrade -i cord3c ${helmPackageCord3cExample.outputs.files.singleFile} --namespace=cord3c-example"
		})
	}
}
```

For more detailed information, have a look at the `CliExecExtension`. A full example
can be found at https://github.com/cord3c/cord3c-project/tree/master/cord3c-example-deployment.


## Tasks

No tasks defined by this plugin. However, the various specializations like `kubectl` and `helm` plugin come
with dedicated tasks to short-cut the implementation of some of the most common commands.

# Example

For an example app have a look at https://github.com/crnk-project/crnk-example[crnk-example] how to deploy
to Google Cloud with Kubernetes and Helm. Or https://github.com/cord3c/cord3c-project/tree/master/cord3c-example-deployment[cord3c-example]
for how to deploy to DigitalOcean.


## Wrapper

A wrapper `helm` will be generated into the project root to allow easy access from the command line next to Gradle tasks.







