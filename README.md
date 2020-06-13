# gradle plugins

[![Build Status](https://travis-ci.org/rmee/gradle-plugins.svg?branch=master)](https://travis-ci.org/rmee/gradle-plugins)
[![License](https://img.shields.io/badge/License-Apache%202.0-yellowgreen.svg)](https://github.com/rmee/gradle-plugins/blob/master/LICENSE)

Hosts a collection of Gradle plugins:

- [jdk-bootstrap](../../blob/master/jdk-bootstrap): to automatically download a JDK to run Gradle.
- [systemd-application](../../blob/master/systemd-application): builds a RPM/systemd package out of any Java main
  application, such as Spring Boot or Dropwizard.
- [oc](../../blob/master/oc): gives access to the OpenShift command line tool &quot;oc&quot; to interface
  with an OpenShift cluster.
- [kubectl](../../blob/master/kubectl): gives access to the Kubernetes command line tool &quot;kubectl&quot; to interface
  with an OpenShift cluster.
- [terraform](../../blob/master/terraform): gives access to the Terraform command line tool &quot;terraform&quot;.
- [helm](../../blob/master/helm): gives access to the Helm command line tool &quot;helm&quot; to perform packaging
  and deployment of Kubernetes applications.
- [az](../../blob/master/az): gives access to the Azure command line tool &quot;az&quot; to interface
  with Azure.
- [gcloud](../../blob/master/gcloud): gives access to the Google Cloud command line tool &quot;gcloud&quot;.
- [jpa-schema-gen](../../blob/master/jpa-schema-gen): to generate Flyway and Liquibase-compatible schema setup and migration scripts.
- [assemble-needed](../../blob/master/assemble-needed): assembles a project and all its project dependencies.
- [build-on-change](../../blob/master/build-on-change):(experimental!) perform incremental builds of PRs by only building what has
  changed compared to a reference branch (like master).

Have a look at https://github.com/crnk-project/crnk-example to see some of the plugins applied to a larger example application.
For a general introduction into the architecture of most plugins see
https://jaxenter.com/tooling-as-code-truly-self-contained-gradle-builds-160998.html.

The `oc`, `kubernetes`, `helm`, `az`, 'gcloud' and `terraform` plugins make use of:

- the native command line tools of the various technology stacks. They deliberately do not want to establish
  a new Java-based API and instead focuses on what developer already know and bring it to Gradle. It further helps
  to stay up-to-date with those technologies and keep the plugins simple.
- dockerizization of the command line tools to gain isolation of their configuration to the scope of the
   applied Gradle project and have platform independence and caching of the tools.
- Volume-mappings into the project to let different plugins work together, such as sharing the Kubernetes configuration.
- generated wrapper scripts in the project root to give access to the tools from the command line
  just like if they were locally installed while hiding volume-mapping and environment logic.

Note that early versions of plugins did not yet make use of dockerization. The extensions can still be configured to fallback
to that behavior. This functionality may or may not be removed in the future depending on the need.

## Examples

For an example app making use of the plugin have a look at
https://github.com/cord3c/cord3c-project/tree/master/cord3c-example-deployment[cord3c-example]
or https://github.com/crnk-project/crnk-example[crnk-example].


## Licensing

All plugins are licensed under the Apache License, Version 2.0.
You can grab a copy of the license at http://www.apache.org/licenses/LICENSE-2.0.


## Building from Source

Crnk make use of Gradle for its build. To build the complete project run

    gradlew clean build


## Links

* [Source code](https://github.com/contraxia/contraxia-plugins/)
* [Issue tracker](https://github.com/contraxia/contraxia-plugins/issues)
* [Build](https://travis-ci.org/rmee/gradle-plugins/)