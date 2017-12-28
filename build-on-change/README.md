# build-on-change

Allows to incrementally build a project based on the Git history compared to a reference branch (like master).
Only sub-project having changed and other sub-projects depending on them will be built.
 
Recommended to quickly build *pull requests* in larger multi-project builds. The plugin will only build 
what was changed and avoid unnecessary rebuilds of other parts of an application.
 
The plugin can complement a Gradle remote build cache. In contrast to the build cache, unchanged projects
are not restored from cache, but skipped altogether. This gives better performance, in particular for 
IO-heavy tasks like building fat JARs or NPM-based sub-projects that incur high costs, even when restoring
from cache.


## Usage

The plugin can be applied with:

```
plugins {
    id 'com.jfrog.bintray' version '<VERSION>'
}
```

or

```
buildscript {
	dependencies {
	    ...
		classpath 'com.github.rmee:build-on-change:<VERSION>'
	}
}
```

```
apply plugin: 'build-on-changes'
```

This will make a new task *buildDependentsOnChange* available
that behaves like *buildDependents*, but performing an additional
change check against the reference branch and skips the task if
no change is available.

```
gradlew buildDependentsOnChange
```



