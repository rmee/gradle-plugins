package com.github.rmee.buildonchange

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BuildOnChangePlugin implements Plugin<Project> {

	private Project project

	private Logger logger

	boolean hasChange(paths) {
		def rootProject = project.rootProject
		def extension = rootProject.extensions.getByType(BuildOnChangeExtension)

		def refBranch = extension.referenceBranch
		def currentBranch = rootProject.grgit.branch.current.name

		def refLogs = project.ext.grgit.log(includes: [refBranch], maxCommits: 1, paths: paths)
		def currentLogs = project.ext.grgit.log(includes: [currentBranch], maxCommits: 1, paths: paths)

		return refLogs.isEmpty() || currentLogs.isEmpty() || refLogs[0].id != currentLogs[0].id
	}

	Boolean triggerFullRebuild = null

	boolean doFullRebuild() {
		if (triggerFullRebuild == null) {
			def rootProject = project.rootProject
			def extension = rootProject.extensions.getByType(BuildOnChangeExtension)
			triggerFullRebuild = hasChange(extension.rebuildPaths)
			logger.debug("do full rebuild: {}", triggerFullRebuild)
		}
		return triggerFullRebuild
	}

	void apply(Project project) {
		this.project = project
		this.logger = LoggerFactory.getLogger(BuildOnChangePlugin)

		if (project == project.rootProject) {
			project.apply plugin: 'org.ajoberstar.grgit'
			project.extensions.create('buildOnChange', BuildOnChangeExtension.class)

			def cache = new HashMap<>()
			project.gradle.taskGraph.useFilter {
				task ->
					if (task.name == 'buildDependentsOnChange' && !doFullRebuild()) {
						def path = task.project.path.substring(1).replace(':', '/')
						def changed = cache.get(path)
						if (changed == null) {
							if (path.isEmpty()) {
								return true
							}
							def paths = [path]
							changed = hasChange(paths)
							logger.debug("project ${task.project.path} changed in git: ${changed}")
							cache.put(path, changed)
						}
						return changed
					}
					else {
						return true
					}
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




