# oc

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
	version = ...
	credentials {
		userName = 'john'
		password = 'doe'
		token = 'someToken // alternative to userName/password
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
 
# Example

For an example app have a look at 
https://github.com/rmee/gradle-plugins/tree/master/helm/src/test/resources/
and `HelmSysTest`.
