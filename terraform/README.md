# Terraform

Provides access to `terraform`, the command line cli of Terraform to manage cloud environments.

## Classpath

Add this library to the classpath:

```
buildscript {
	dependencies {
	    ...
		classpath 'gradle.plugin.com.github.rmee:terraform:<VERSION>'
	}
}
```
 
## Setup

The subsequent example is makes use of a setup together with the `az` plugin:

```
apply plugin: 'terraform'
terraform {
	variables = [
			client_id      : { az.userName },
			client_secret  : { az.password },
			subscription_id: { az.subscriptionId },
			tenant_id      : { az.tenantId },
			ssh_key_pub    : { publicKeyFile.text }
	]
	debug = false
}
terraformInit.dependsOn azLogin
```

The example passes various parameters from the `az` plugin as variables to the `terraform` plugin.


For more detailed information, have a look at the `TerraformExtension`.


## Tasks

The tasks closely match the Terraform feature set:

- `terraformInit`
- `terraformValidate`
- `terraformRefresh`
- `terraformPlan`
- `terraformApply`
- `terraformDestroy`
- `TerraformExec` to setup custom commands.. Alternatively, `terraform.exec(...)` can be used.

## Wrapper

A wrapper `terraform` will be generated into the project root to allow easy access from the command line next to Gradle tasks:

```
TODO
```