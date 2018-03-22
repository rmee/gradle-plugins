package com.github.rmee.application.systemd

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SystemdApplicationPlugin implements Plugin<Project> {

	private Project project

	private Logger logger

	private SystemdApplicationExtension systemdExtension

	private boolean initialized

	void apply(Project project) {
		project.getExtensions().create("systemd", SystemdApplicationExtension)
		this.project = project
		this.logger = LoggerFactory.getLogger(SystemdApplicationPlugin)
		this.systemdExtension = project.getExtensions().getByType(SystemdApplicationExtension)
		this.systemdExtension.plugin = this

		project.with {
			def descriptorTasks = tasks.create('systemdDescriptors')
			descriptorTasks.doFirst {
				init()
				def desc = systemdExtension.getDescriptor()
				systemdExtension.serviceFile.getParentFile().mkdirs()
				systemdExtension.serviceFile.write(desc.toText())


				def scriptsDir = systemdExtension.getScriptsDir()
				scriptsDir.mkdirs()

				StringBuilder builder = new StringBuilder()
				builder.append("#!/bin/sh\n")
				builder.append("echo \\\"Creating group: ${systemdExtension.permissionGroup}\\\"\n")
				builder.append("/usr/sbin/groupadd -f -r ${systemdExtension.permissionGroup} 2> /dev/null || :\n")
				builder.append("echo \\\"Creating user: ${systemdExtension.user}\\\"\n")
				builder.append("/usr/sbin/useradd -r -m ${systemdExtension.user} -g ${systemdExtension.permissionGroup} " +
						"2> /dev/null || :\n")
				File preInstallFile = new File(scriptsDir, "preInstall.sh")
				preInstallFile.write(builder.toString())

				builder = new StringBuilder()
				builder.append("#!/bin/sh\n")
				builder.append("systemctl preset ${systemdExtension.packageName}\n")
				File postInstallFile = new File(scriptsDir, "postInstall.sh")
				postInstallFile.write(builder.toString())

				builder = new StringBuilder()
				builder.append("#!/bin/sh\n")
				builder.append("systemctl disable ${systemdExtension.packageName}\n")
				builder.append("systemctl stop ${systemdExtension.packageName} \n")
				File preUninstallFile = new File(scriptsDir, "preUninstall.sh")
				preUninstallFile.write(builder.toString())

				builder = new StringBuilder()
				builder.append("#!/bin/sh\n")
				builder.append("systemctl daemon-reload\n")
				File postUninstallFile = new File(scriptsDir, "postUninstall.sh")
				postUninstallFile.write(builder.toString())
			}

			apply plugin: "nebula.ospackage"
			tasks.buildRpm.dependsOn(descriptorTasks)
		}
	}

	protected void init() {
		if (initialized) {
			return
		}
		initialized = true

		systemdExtension.descriptor.unit = [
				'Description': "${systemdExtension.packageName} service",
				'After'      : "syslog.target"
		]

		systemdExtension.descriptor.service = [
				'User'             : systemdExtension.user,
				'SuccessExitStatus': '143',
				'ExecStart'        : "${systemdExtension.getBinaryPath()} run",
				'WorkingDirectory' : systemdExtension.workingDir,
				'Type'             : 'notify',
				'NotifyAccess'     : 'all',
				'Restart'          : 'on-failure',
				'RestartSec'       : '15s'
		]

		systemdExtension.descriptor.install = [
				'WantedBy': "multi-user.target"
		]
		project.with {

			// OS Package plugin configuration
			// https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#deployment-systemd-service
			// https://github.com/gilday/how-to-microservice/blob/master/build.gradle#L82
			// https://www.ccampo.me/java/spring/linux/2016/02/15/boot-service-package.html
			ospackage {
				version = project.version
				if (release == null) {
					release = '0'
				}
			}

			buildRpm {
				packageName = systemdExtension.packageName

				os = LINUX
				type = BINARY
				arch = NOARCH

				directory(systemdExtension.packageDir, 644, systemdExtension.user, systemdExtension.permissionGroup)
				directory(systemdExtension.packageBinDir, 644, systemdExtension.user, systemdExtension.permissionGroup)
				directory(systemdExtension.packageLibDir, 644, systemdExtension.user, systemdExtension.permissionGroup)

				def scriptsDir = systemdExtension.getScriptsDir()
				preInstall file("${scriptsDir}/preInstall.sh")
				postInstall file("${scriptsDir}/postInstall.sh")
				preUninstall file("${scriptsDir}/preUninstall.sh")
				postUninstall file("${scriptsDir}/postUninstall.sh")

				into systemdExtension.packageDir
				user systemdExtension.user
				permissionGroup systemdExtension.permissionGroup

				from(systemdExtension.startScripts.classpath) {
					into systemdExtension.packageLibDir
					user systemdExtension.user
					permissionGroup systemdExtension.permissionGroup
					fileMode = 0644
					addParentDirs false
				}

				from(systemdExtension.startScripts.unixScript) {
					into systemdExtension.packageBinDir
					user systemdExtension.user
					permissionGroup systemdExtension.permissionGroup
					fileMode = 0755
					addParentDirs false
				}

				from(systemdExtension.serviceFile) {
					into '/etc/systemd/system'
					expand project.properties
					user 'root'
					permissionGroup 'root'
					fileMode = 0644
					addParentDirs false
				}

				if (!systemdExtension.configFiles.isEmpty()) {
					directory(systemdExtension.configDir, 644, systemdExtension.user, systemdExtension.permissionGroup)
				}

				for (def configFile : systemdExtension.configFiles) {
					from(configFile) {
						fileType CONFIG | NOREPLACE
						into systemdExtension.configDir
						user systemdExtension.user
						permissionGroup systemdExtension.permissionGroup
						fileMode = 0644
						addParentDirs false
					}

					// According to Spring Boot, the conf file needs to sit next to the jar, so we just create a symlink
					link("${systemdExtension.packageBinDir}${configFile.getName()}",
							"${systemdExtension.configDir}/${configFile.getName()}")

				}

				if (systemdExtension.linkBinaryToUserLocalBin) {
					// Adding a symlink from /usr/local/bin to the app
					link("/usr/local/bin/${systemdExtension.startScripts.applicationName}",
							"${systemdExtension.workingDir}/${systemdExtension.startScripts.applicationName}")
				}
			}
		}
	}
}




