# systemd-application

Is an opinionated Gradle plugin packaging any project making use of
the `application` plugin into Linux package that is registered and
run with systemd. The plugin covers:

- Package gets installed to `/var/<myapp>`. Binaries to `bin` and libraries to `lib`.
- Working directory is set to `/var/<myapp>/bin`.
- Configuration files are placed into `/etc/<myapp>` and are linked into
  `/var/<myapp>/bin` to facilitate their discovery through the use of the
  working directory.
- A systemd descriptor is generated.
- A dedicated user and group is created to run the app.
- pre and post install and uninstall scripts are generated to create the necessary
  user and group and register to systemd.
- The application is supposed to log to stdout and stderr to let journald collect
  the messages.
- The application binary is linked into `/usr/local/bin/` to make it easy
  accessible for users.  

# Usage

```
buildscript {
	dependencies {
	    ...
		classpath 'com.github.rmee:systemd-application:<VERSION>'
	}
}
```

Get the current version from [com.github.rmee.systemd-application](https://plugins.gradle.org/plugin/com.github.rmee.systemd-application).

An advanced usage example may look like:


```
apply plugin: 'systemd-application'

task startScripts(type: CreateStartScripts) {
	outputDir = file('build/scripts')
	mainClassName = 'ch.adnovum.fedicam.app.management.service.ManagementApplication'
	applicationName = 'demo-app'
	classpath = files(configurations.runtime, tasks.jar.outputs.files.singleFile)
}

systemd {
	startScripts = tasks.startScripts
	configFileName = 'application.yaml'
}

tasks.buildRpm.dependsOn tasks.startScripts
tasks.buildRpm.dependsOn tasks.assemble
```

You may also omit the usage of `CreateStartScripts` by making is use the
`application` plugin directly.

Internally the plugin makes use of 
[gradle-ospackage-plugin](https://github.com/nebula-plugins/gradle-ospackage-plugin)
to generate the Linux packages. So far only RPM packages have been tested, but
Debian packages should work equally well with minor effort (PRs welcomed).

## Configuration

The plugin offers a number of configuration options:

```
systemd {
	startScripts = tasks.startScripts   	//  start script and classpath to use
	configFileName = 'application.yaml' 	//  name of the configuration file
    packageName = 'demo-app'				//  name of the package, obtained from startScript by default
    user = 'demo-app' 						//  name of the user used to run the application with systemd
    permissionGroup = 'demo-app'			//  name of the group used to run
    packageDir = '/var/demo-app/'			//  directory to install the binaries 
    workingDir = '/var/demo-app/bin'		
    configFile file('src/main/rpm/application.yaml')
    configDir = /etc/demo-app/<configFileName>  //  e.g. /etc/demo-app/application.yaml 
    
    // three maps to configure the systemd service descriptor
    descriptor{
		unit
		service
		install
	}
}
```

For a detailed look of all the possible parameters have a look at
`SystemdApplicationExtension`. You may also make use of the sources as 
inspiration if you have a more advanced usage scenario going beyond the 
scope of this plugin.

## systemd descriptor

An example service descriptor may look like:

```
[Unit]
Description=demo-app service
After=syslog.target

[Service]
User=demo-app
SuccessExitStatus=143
ExecStart=/var/demo-app/bin/demo-app run
WorkingDirectory=/var/demo-app/bin/
Type=notify
NotifyAccess=all
Restart=on-failure
RestartSec=15s

[Install]
WantedBy=multi-user.target
```

Note that the default setup assumes that the application notifies systemd
about its successful start. **You have to make use of, for example,
[SDNotify](https://github.com/faljse/SDNotify)!**


