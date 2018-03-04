package com.github.rmee.application.systemd

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.platform.base.Binary
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SystemdApplicationPlugin implements Plugin<Project> {

	private Project project

	private Logger logger

	private SystemdApplicationExtension extension

	private boolean initialized

	void apply(Project project) {
		this.project = project
		this.logger = LoggerFactory.getLogger(SystemdApplicationPlugin)
		this.extension = project.getExtensions().create("systemd", SystemdApplicationExtension)
		this.extension.packageName = project.name


		project.with {
			apply plugin: "nebula.ospackage"

		}
	}

	protected void init() {
		if (initialized) {
			return
		}
		initialized = true

		extension.descriptor.unit = [
				'Description': "${extension.packageName} service",
				'After'      : "syslog.target"
		]

		extension.descriptor.service = [
				'User'             : extension.user,
				'SuccessExitStatus': '143',
				'ExecStart'        : "${extension.binaryPath} run",
				'WorkingDirectory' : extension.workingDir,
				'Type'             : 'notify',
				'NotifyAccess'     : 'all',
				'Restart'          : 'on-failure',
				'RestartSec'       : '15s'
		]

		extension.descriptor.install = [
				'WantedBy': "multi-user.target"
		]

		project.with {
			tasks.buildRpm.doFirst {
				doLast {
					extension.serviceFile.write(extension.descriptor.toString())
				}
			}

			// OS Package plugin configuration
			// https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#deployment-systemd-service
			// https://github.com/gilday/how-to-microservice/blob/master/build.gradle#L82
			// https://www.ccampo.me/java/spring/linux/2016/02/15/boot-service-package.html
			ospackage {
				packageName = extension.packageName
				version = project.version
				release = 1
				os = LINUX
				type = Binary
				arch = NOARCH

				preInstall '#!/bin/sh'
				preInstall "echo \"Creating group: ${extension.permissionGroup}\""
				preInstall "/usr/sbin/groupadd -f -r ${extension.permissionGroup} 2> /dev/null || :"

				preInstall "echo \"Creating user: ${extension.user}\""
				preInstall "/usr/sbin/useradd -r -m ${extension.user} -g ${extension.permissionGroup} 2> /dev/null || :"

				preUninstall '#!/bin/sh'
				preUninstall "systemctl disable ${extension.packageName} > /dev/null 2>&1"
				preUninstall "systemctl stop ${extension.packageName} > /dev/null 2>&1"

				postInstall '#!/bin/sh'
				postInstall "systemctl preset ${extension.packageName} > /dev/null 2>&1"

				postUninstall '#!/bin/sh'
				postUninstall "systemctl daemon-reload > /dev/null 2>&1"

				into "/var/${extension.packageName}"
				user extension.user
				permissionGroup extension.permissionGroup

				from(configurations.runtime) {
					into extension.packageLibDir
					user extension.user
					permissionGroup extension.permissionGroup
					fileMode = 0644
				}

				from(tasks.jar.outputs.files.singleFile) {
					into extension.packageLibDir
					user extension.user
					permissionGroup extension.permissionGroup
					fileMode = 0644
				}

				from(tasks.startScripts.outputs.files.singleFile) {
					into extension.packageBinDir
					user extension.user
					permissionGroup extension.permissionGroup
					fileMode = 0755
				}

				from(extension.serviceFile) {
					into '/etc/service/system'
					addParentDirs false
					expand project.properties
					user 'root'
					permissionGroup 'root'
					fileMode = 0644
				}

				from('src/main/rpm/conf') {
					into extension.configDir
					user extension.user
					permissionGroup extension.permissionGroup
					fileMode = 0644
				}

				if (extension.configFileName != null) {
					// According to Spring Boot, the conf file needs to sit next to the jar, so we just create a symlink
					link("${extension.packageBinDir}${extension.configFileName}",
							"${extension.configDir}/${extension.configFileName}")
				}

				// Creates a symlink to the jar file as an init.d script (this functionality is provided by Spring Boot)
				link("/etc/init.d/${extension.packageName}", "${extension.packageBinDir}${extension.packageName}-service")

				// add binary to user/bin
				link("/user/bin/${extension.binaryName}", "${extension.packageBinDir}${extension.binaryName}")

				// Copy the config files
				from("src/main/rpm/conf") {
					fileType CONFIG | NOREPLACE
					fileMode 0754
					into "conf"
				}
			}
		}
	}
}




