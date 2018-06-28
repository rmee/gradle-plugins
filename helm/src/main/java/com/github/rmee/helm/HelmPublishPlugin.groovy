package com.github.rmee.helm

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.ivy.IvyPublication

class HelmPublishPlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {
		project.with {
			// artifactory plugin is used as it allows to suppress publishing the ivy descriptor, not possible with gradle default plugin
			apply plugin: 'com.jfrog.artifactory'
			apply plugin: 'ivy-publish'

			publishing {
				publications {
					helmPublication(IvyPublication) {
						for (File file : tasks.helmPackage.outputs.files) {
							artifact file
						}
					}
				}
			}

			artifactory {
				publish {
					contextUrl = BINREPO_PUBLISH_URL

					repository {
						repoKey = BINREPO_HELM
						username = brUser
						password = brPass
						ivy {
							artifactLayout = "[artifact]-[revision].tgz"
						}
					}

					defaults {
						publications('helmPublication')
						publishBuildInfo = true
						publishArtifacts = true
						publishPom = false
						publishIvy = false
					}
				}
			}
			artifactoryPublish.dependsOn HelmPackage
			publish.dependsOn artifactoryPublish
		}

	}
}
