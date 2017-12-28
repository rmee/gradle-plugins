package com.github.rmee.buildonchange

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.LoggerFactory

class BuildOnChangePlugin implements Plugin<Project> {

	void apply(Project project) {
		if (project == project.rootProject) {
			project.apply plugin: 'org.ajoberstar.grgit'
			project.extensions.create('buildOnChange', BuildOnChangeExtension.class)

			def cache = new HashMap<>()
			project.gradle.taskGraph.useFilter {
				task ->
					def doExecute
					if (task.name == 'buildDependentsOnChange') {
						def path = task.project.path.substring(1).replace(':', '/')
						def hasChange = cache.get(path)
						if (hasChange == null) {
							def rootProject = project.rootProject
							def extension = rootProject.extensions.getByType(BuildOnChangeExtension)
							if (path.isEmpty()) {
								return true
							}

							def paths = [path]

							def refBranch = extension.referenceBranch
							def currentBranch = rootProject.grgit.branch.current.name

							def refLogs = project.ext.grgit.log(includes: [refBranch], maxCommits: 1, paths: paths)
							def currentLogs = project.ext.grgit.log(includes: [currentBranch], maxCommits: 1, paths: paths)

							hasChange = refLogs.isEmpty() || currentLogs.isEmpty() || refLogs[0].id != currentLogs[0].id
							def logger = LoggerFactory.getLogger(BuildOnChangePlugin)
							logger.debug("project ${task.project.path} changed in git: ${hasChange}")

							cache.put(path, hasChange)
						}
						doExecute = hasChange
					}
					else {
						doExecute = true
					}
					return doExecute
			}
		}
		else {
			project.afterEvaluate {
				def buildDependentsTask = project.tasks.findByName('buildDependents')
				if (buildDependentsTask != null) {
					def buildOnChangeTask = project.tasks.create('buildDependentsOnChange', BuildOnChangeTask)
					buildOnChangeTask.dependsOn buildDependentsTask
				}
			}
		}
	}
}




