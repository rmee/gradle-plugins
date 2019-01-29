# jdk-bootstrap

The plugin includes a small script into `gradlew` to fetch the JDK if not already done.
This allows for a Gradle build to become truly self-contained without needing any kind
of installation.

The currently only limitation is the use of a decent console. Which means on Windows the use of,
for example, Git CMD. `gradlew.bat` is not yet patched, PRs welcomed.



## Setup

Add this library to the classpath:

```
buildscript {
	dependencies {
	    ...
		classpath 'com.github.rmee:jdk-bootstrap:<VERSION>'
	}
}
```

## Tasks

No new tasks are available. The plugin rather integrates with the default Gradle `wrapper` task.


## Example


Get the download URL of the desired JDK. For this purpose go to http://jdk.java.net/10/
and get one of the download links that may look like:
 
```
https://download.java.net/java/GA/jdk10/10.0.2/19aef61b38124481863b1413dce1855f/13/openjdk-10.0.2_windows-x64_bin.tar.gz
https://download.java.net/java/GA/jdk10/10.0.2/19aef61b38124481863b1413dce1855f/13/openjdk-10.0.2_osx-x64_bin.tar.gz
https://download.java.net/java/GA/jdk10/10.0.2/19aef61b38124481863b1413dce1855f/13/openjdk-10.0.2_linux-x64_bin.tar.gz 
```
 
The setup the plugin accordingly:

```
apply plugin: 'jdk-bootstrap'
jdk {
    version = ...
	urlTemplate = 'https://download.java.net/java/GA/jdk10/${version}/19aef61b38124481863b1413dce1855f/13/openjdk-${version}_${os}_bin.tar.gz'
}
```

Here `os` is a provided placeholder translating to `windows`, `mac` or `linux` at runtime.
The URLs are rather complicated and not predicable and for this reason cannot be automated by this plugin
for the time being.

Or just make use a built-in default to download a JDK from https://github.com/AdoptOpenJDK/:

```
apply plugin: 'jdk-bootstrap'
jdk {
	useAdoptOpenJdk8('jdk8u202-b08')
}
```


## IDE setup

PRs welcomed to directly setup the IDE with the specified JDK.







