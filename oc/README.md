# oc

The `oc` plugin works exactly the same as the `kubectl` plugin. Sole exception
is the login handling. There is a `ocLogin` instead of 
`kubectlUseContext`.


## Classpath

Add this library to the classpath:

```
buildscript {
	dependencies {
	    ...
		classpath 'gradle.plugin.com.github.rmee:oc:<VERSION>'
	}
}
```


## Setup

An example looks like:

```
apply plugin: 'oc'
oc {
	projectName = 'demo-app'
	credentials {
		userName = 'john'
		password = 'doe'
		token = 'someToken // alternative to userName/password
	}
	cli {
	  version = '3.7.2-282e43f' 
	  imageName = 
	}
}

task ocSetupTiller(type: OcExec) {
	group = 'provision'
	mustRunAfter ocNewProject
	commandLine "oc process -f ${file('src/main/...')} -p TILLER_NAMESPACE=${oc.projectName} | oc create -f -"
}

```

For more detailed information, have a look at the `OcExtension`.

The plugin makes use of dockerized binarizes, for more information have a look at the root page. 
 
 
## Tasks

- `OcBootstrap` downloads the cli.
- `OcExec` allow to issue a command. Alternatively, `oc.exec(...)` can be used.
- `OcLogin` performs the login.
- `OcSetProject` sets the currently used project.
- `OcNewProject` creates a new project.


 
# Example

For an example app have a look at 
https://github.com/rmee/gradle-plugins/tree/master/helm/src/test/resources/
and `HelmSysTest`.


