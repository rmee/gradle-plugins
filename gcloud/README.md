# gcloud

Provides access to `gcloud`, the command line client of Google Cloud to manage cloud resources.

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

```
apply plugin: 'gcloud'
az {
	resourceGroup = '...'
	aks {
		clusterName = '...'
	}
	client {
		version = `2.0.38`
		imageName = 'microsoft/azure-cli'
	}
}
```

The following environment variables must be passed to perform a login:

- `AZ_USER`
- `AZ_PASS`
- `AZ_TENANT_ID`
- `AZ_SERVICE_PRINCIPLE=true|false` whether a service principal is used.
- `AZ_SUBSCRIPTION_ID`
  		
Typically it is undesired to manage those (sensitive) properties in Gradle. But instead of environment variable, the
`AzExtension` also allows to set the values directly. 		

## Tasks

The tasks closely match the Terraform feature set:

- `azLogin` to perform the login. 
- `azGetKubernetesCredentials` to fetch the Kubernetes context configuration that can then be used by the `helm` and `kubectl` plugin.
- `azKubernetesDashboard` TODO to follow in the near future
- `AzExec` as base class for custom Azure tasks. Alternatively, `az.exec` can be called directly.


## Wrapper

A wrapper `az` will be generated into the project root to allow easy access from the command line next to Gradle tasks:

```
TODO
```