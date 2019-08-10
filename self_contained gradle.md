# Tooling as Code: Having Truly Self-Contained Gradle Builds

"Works on My Machine" is a classic among software engineering errors. Tiny differences in local project setups can
continuously give rise to new glitches. Setting up and maintaining a project and isolating it 
from other projects can be challenging and time-consuming. The same holds for
differences between the setup for development, CI/CD and production deployments. For the later, issues may be
detected only late in the delivery chain and, subsequently, are time-consuming to test and fix. In this tutorial,
we present a number
of tools to address such issues and, in the progress, gain truly self-contained builds that are
well shielded from any external influences.

## Introduction

There is already a wide range of tooling to remedy setup-related issues. For example,
`nvm` lets users quickly switch between multiple Node.js installations in the Javascript world. Still, developers
must select the appropriate version at all time. On the Java side, developers
benefit from a JVM that abstracts away most of the underlying operating system. Hence, Java applications typically
work equally well on Linux, Windows and OS X. Gradle further brings the Gradle wrapper to the table.
A Gradle build will always use the declared project-specific Gradle version to execute build operations. Gradle
downloads and caches that version if necessary. With this Gradle addresses one of the main issues
of `nvm`: everything happens transparently for the developer and the project itself is in charge of
the local setup. One may call this *Tooling as Code* in line with the techniques
applied for *Infrastructure as Code*. The subsequent sections outline how to apply those principles
not just to Gradle, but to everything related to Java-centric build tooling. Most notably:

- JDK installation.
- `kubectl` to interact with Kubernetes clusters.
- `helm` to package Kubernetes applications.
- `terraform` to provision infrastructure.
- `gcloud`, `az`, `oc` to work with Google Cloud, Azure and OpenShift.
- Node.js installation.
- Local databases.
- Execution of UI tests.

There are projects that help with this:

- https://github.com/rmee/gradle-plugins/ hosts several Gradle plugins.
- https://www.testcontainers.org/ to make use of Docker images in Unit testing.
- https://github.com/srs/gradle-node-plugin to integrate Node.js into Gradle.
 
Have a look at https://github.com/crnk-project/crnk-example for an
example application making use of these techniques.


## Install a JVM

The `jdk-bootstrap` plugin from https://github.com/rmee/gradle-plugins/ achieves for the JVM  the 
same comfort as the Gradle wrapper for Gradle. It can
be added as a dependency to the Gradle classpath:

```
plugins {
    id "com.github.rmee.jdk-bootstrap" version "1.0.20190725142159"
}
```

and be used together with the Gradle wrapper:

```
wrapper {
    gradleVersion = '5.5'
}

apply plugin: 'jdk-bootstrap'
jdk {
    useAdoptOpenJdk8('8u202-b08')
}
```

By calling:

```
./gradlew wrapper
```

the `gradlew` script is enriched with logic to download and cache the selected JDK and setup the `JAVA_HOME`
environment variable accordingly:
 
```
...
JDK_DOWNLOAD_URL="https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/..."
JDK_VERSION="8u202-b08"
JDK_CACHE_DIR="${APP_HOME}/.gradle/jdk"
if [ -z "${JAVA_HOME}" ]; then
	JAVA_HOME="${JDK_CACHE_DIR}/jdk-${JDK_VERSION}";
fi
if ! [ -d "${JAVA_HOME}" ]; then
  mkdir -p "${JDK_CACHE_DIR}" || die "java: Fatal error while creating local cache directory: ${JDK_CACHE_DIR}"
  ...
fi
...
``` 
 
 
From this point forward, every developer will use the same JVM! The
JDK is available from the project-local `.gradle` directory as is Gradle itself. 


## Self-contained Tooling From Development to Production

Having a stable, consistent JVM and Gradle setup is a good start, but usually, many more tools are involved
across the project lifecycle; most notably for testing, provisioning, and deployment. For example, tools like
Terraform, Helm and `kubectl` are in frequent use to deploy applications to a managed
Kubernetes service such as GKE on Google Cloud. All these tools are available as binaries for 
download and installation. But then again one faces the challenges brought up earlier and more:

- The local installation must be maintained by the developer.
- Binaries frequently pollute the developers home directory. When working on multiple projects, things
  can get mixed up quickly. For example, it is not hard to deploy to the wrong Kubernetes cluster with `kubectl`.
  While one can specify a custom home directory one way or
  the other, it is typically too cumbersome to drag along such a setting across a developer`s workflow
  from within Gradle and outside.
- Binaries must be fetched from one of the download mirrors. While being simple for single developers, it
  can become a major pain point in corporate environments. There is no unique, established mechanism of
  how to deal with such binaries: how to download, cache
  and update. URLs can sometimes be cryptic and render automation unnecessarily hard.
  Package managers like RPM work globally on the machine in use.
- Developer, CI/CD servers and operators may use different toolsets.
  Puppet and Ansible are often found in production rollouts, whereas rarely used by developers for local
  development. With Gradle there is already a task execution engine that must be used by the project
  during the development process, why not make use of it at other places? Thereby,
  address the challenges from above and gain a unified toolset
  from development to production?

The elephant in the room is Docker. Its registry allows to download, cache, and update images.
Images are executed in isolated runtime environments. However, it is far from clear how to best benefit
from Docker in a typical Java (or other) project. The brought-up Terraform, Helm, kubectl and gcloud tools
are all available as Docker images, but rarely used this way.

The most straightforward approach to work with Docker is to put the entire build into a Docker image.
A popular project in this area is https://github.com/openshift/source-to-image[Source-to-Image] from RedHat.
But is this the desired way to go when checking against the issues from above? Developers are unlikely to
adopt this model for local development. There is neither support from Gradle nor IDEs
like IntellijJ and Eclipse to execute builds in Docker images. The more pressing question is, however, whether
it is desirable or even possible to have everything in that one image?
Larger projects will be composed of many parts. If projects chose to go with a mono-repository
to gain simplicity, then
all parts are hosted in the same repository, built together, and released together through various artifacts.
Different tools may have different requirements for the underlying operation system. Or 
maybe multiple versions of the same component or tool are involved for compatibility testing.

What one may rather desires is an *orchestration of tooling-related Docker images*, making everything work together
without interrupting the workflow of developers and operators. Help comes here from:

- `cli-base`, `kubectl`, `oc`, `terraform`, `az`, `glcoud` plugins
   from https://github.com/rmee/gradle-plugins/
- https://www.testcontainers.org/

Both projects follow the same goal in different settings. Testcontainers simplifies the usage of Docker
images in unit testing, whereas the Gradle plugins ease the use of Docker images within builds. Together they 
can achieve the desired goals from above. An example is given in the next section.


## Deploy with Helm to Google Cloud

The `kubectl`, `oc`, `terraform`, `az`, `glcoud` plugins of
https://github.com/rmee/gradle-plugins/ are designed to provide a
minimal Gradle-integration layer above their native counterparts. It is explicitly not the goal to make
use of REST and other APIs the tools may also offer: introducing a new access layer would need continuous maintenance
effort to keep up with new features and, worse, require developers to learn a new API that is only applicable
within the scope of Gradle. Instead, the plugins provides something similar to the Gradle `Exec` task while
hiding the complexity of invoking Docker. 

To apply the plugins make use of:

```
plugins {
    id "com.github.rmee.jdk-bootstrap" version "1.0.20190725142159"
    id "com.github.rmee.kubectl" version "1.0.20190725142159"
    id "com.github.rmee.helm" version "1.0.20190725142159"
    id "com.github.rmee.gcloud" version "1.0.20190725142159"
}
```

Then access to Google Cloud can be configured with:

```
gcloud {
    keyFile = file("$projectDir/secrets/gcloud.key")
    region = 'my-region'
    project = 'my-project'
    gke {
        clusterName = 'my-cluster'
    }
    cli {
        imageName = 'google/cloud-sdk'
        version = '224.0.0'
    }
}
gcloudSetProject.dependsOn gcloudActivateServiceAccount
gcloudGetKubernetesCredentials.dependsOn gcloudSetProject
```

In this case a key file provides technical user access to the cluster for the specified project, cluster, and region. 
Notice the image configuration of glcoud. In contrast, the Helm image is not configured and the built-in default is used.
To then issue `helm` and `kubectl` commands use:

```
task deploy() {
    dependsOn gcloudGetKubernetesCredentials, helmPackage, tasks.jib
    doFirst {
        File yamlFile = file("build/helm/crnk-example.yaml")
        String imageId = file("build/jib-image.id").text
        helm.exec({
            commandLine = "helm template --name=crnk --set image.tag=${imageId} ${helmPackageCrnkExample.outputs.files.singleFile} --namespace=default"
            stdoutFile = yamlFile
        })
        kubectl.exec({
            commandLine = "kubectl apply -f=${yamlFile} -n=default"
        })
    }
}
```

The example showcases several important properties:

- Gradle code declares the used tooling. No local installation is necessary.
- The commands match 1:1 with what developers execute in the command line.
  Nothing new needs to be learned. It is easy to experiment manually outside Gradle when facing new tasks and issues.
- Upgrading the Docker version of a tool gives instant access to the most current features.
  The plugins are minimalistic and are to a large degree agnostic to the used version.
- Under the hood, *the plugins shield the user's home directory*! A new home directory is allocated
  in the `build` directory. It underlies the same Gradle lifecycle as any other project file. A `gradlew clean`
  will remove the home directory. Other commands will subsequently build it up again if necessary.
- The `exec` commands will transparently map absolute paths to appropriate Docker paths.
- The plugins are not limited to Docker images. They may also  make use of already existing binaries
  on the local machine if desired for one or the other use case. 
- There is a `cli-base` project that provides an early abstraction layer to implement further
  plugins next to the already implemented one.
- The `exec` methods give access to the output. For example, for Helm the evaluated templates are written into `yamlFile`.


## Giving control back to developers

Similar to `jdk-bootstrap` the Gradle plugins from the previous section hook into the `gradlew wrapper` task.
A small shell script is generated for each applied plugin that provides a replacement of its native counterpart.
The shell scripts are similar in spirit to `gradlew` and in case of `kubectl` looks like:

```
#!/usr/bin/env bash
WORK_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null && pwd)"
[ -x /bin/cygpath ] && WORK_DIR=$(/bin/cygpath -m "$WORK_DIR")

...

exec docker run -i $TTY_PARAM $USER_PARAM --rm \
    "${HTTP_PROXY_PARAM[@]}" \
    ...
    -e HOME=/workdir/build/home \
    --workdir /workdir/build/home \
    -v $WORK_DIR:/workdir \
    google/cloud-sdk:224.0.0 kubectl "$@"
```

Its use is almost identical to the original binary. Limitations involve port mappings and
volume mounting beyond the project directory which require further attention. An 
invocation looks like:

```
$ ./kubectl get pods -n=kube-system
NAME                                                         READY   STATUS    RESTARTS   AGE
heapster-v1.6.0-beta.1-869f77bc95-n8h4c                      3/3     Running   0          35d
kube-dns-76dbb796c5-2xhdw                                    4/4     Running   0          35d
kube-dns-76dbb796c5-cr8wl                                    4/4     Running   0          35d
kube-dns-autoscaler-67c97c87fb-nl9fv                         1/1     Running   0          35d
kube-proxy-gke-sb4b-test-europe-west6-pool-1-0087b845-38fc   1/1     Running   0          35d
kube-proxy-gke-sb4b-test-europe-west6-pool-1-0087b845-s21t   1/1     Running   0          35d
```


## Outlook

The provided example showcases how to set up a basic Gradle plugin to deploy an application to a Kubernetes cluster
on Google Cloud. The setup is fully self-contained and reproducible.
The used tools are determined by the project. Developers do not have to maintain their installation. Artifacts do not
leave the boundary of the project directory. And tools and versions can be combined arbitrarily to
cover the most complex scenarios. For the Gradle plugins, work is still ongoing to simplify usage
and extendability. More information can be found on the websites of the respective
projects.


