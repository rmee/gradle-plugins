# build-on-change

Allows to incrementally build an application based on the Git history by comparing the current
branch to a reference branch (like master). Only projects having changed and projects depending 
on them will be built, all other projects skipped.
 
Recommended to efficiently build *pull requests* in larger multi-project setups. The plugin will only build 
what was changed or needs testing. It avoids unnecessary rebuilds of other parts.
 
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
apply plugin: 'build-on-change'
```

This will make a new task *buildDependentsOnChange* available
that behaves like *buildDependents*, but performing an additional
change check against the reference branch and skips the task if
no change is available.

```
gradlew buildDependentsOnChange
```

## Configuration

The reference branch can be set with the following configuration:

```
buildOnChange {
	referenceBranch = 'master'
}
```

By default the master branch is chosen.
