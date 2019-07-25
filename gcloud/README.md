# gcloud

Provides access to `gcloud`, the command line cli of Google Cloud to manage cloud resources.

## Setup

Add this library to the classpath:

```
buildscript {
	dependencies {
	    ...
		classpath 'gradle.plugin.com.github.rmee:gcloud:<VERSION>'
	}
}
```
 
## Example

A setup of Kubernetes with Google Cloud can looks as follows:

```
apply plugin: 'kubectl'
apply plugin: 'gcloud'

kubectl {
	url = 'https://35.198.168.245'
	namespace = 'default'
	insecureSkipTlsVerify = false
}

gcloud {
	keyFile = file("$projectDir/src/main/config/gcloud.key")
	zone = 'europe-west3-c'
	project = '...'
	gke {
		clusterName = 'sb4bdemo'
	}
	cli{
		imageName = 'google/cloud-sdk'
		version = '159.0.0'
	}
}
```

- `gcloud.key` stores the service account credentials of Google Cloud.
- the `gcloud` plugin will reconfigure the `kubectl` plugin to share the same docker image in order to work
  together to perform authentication with `glcoud`.


To setup a service account use: 

 https://medium.com/google-cloud/using-googles-private-container-registry-with-docker-1b470cf3f50a
 
Make sure the service account also has the proper Kubernetes credentials, like:

```
./kubectl create clusterrolebinding your-user-cluster-admin-binding --clusterrole=cluster-admin --user=your.google.cloud.email@example.org
```

## Tasks

`gcloud` offers three Gradle tasks:

- `gcloudActivateServiceAccount` to perform the login.
- `gcloudGetKubernetesCredentials` to get the .kubeconfig to start working the `kubectl` plugin.
- `gcloudSetProject` called implicitly as dependency to set the current project.



## Wrapper

A wrapper `cloud` will be generated into the project root to allow easy access from the command line next to Gradle tasks.
