package com.github.rmee.application.systemd

import org.gradle.api.tasks.application.CreateStartScripts

class SystemdApplicationExtension {

	private CreateStartScripts startScripts

	private String packageName

	private String user

	private String permissionGroup

	private String packageDir

	private String workingDir

	private Collection<File> configFiles = new HashSet<>()

	private String configDir

	private SystemdServiceDescriptor descriptor = new SystemdServiceDescriptor()

	private boolean linkBinaryToUserLocalBin = true

	private boolean linkConfigToWorkingDir = true

	protected SystemdApplicationPlugin plugin

	boolean getLinkBinaryToUserLocalBin() {
		return linkBinaryToUserLocalBin
	}

	void setLinkBinaryToUserLocalBin(boolean linkBinaryToUserLocalBin) {
		this.linkBinaryToUserLocalBin = linkBinaryToUserLocalBin
	}

	boolean getLinkConfigToWorkingDir() {
		return linkConfigToWorkingDir
	}

	void setLinkConfigToWorkingDir(boolean linkConfigToWorkingDir) {
		this.linkConfigToWorkingDir = linkConfigToWorkingDir
	}

	SystemdServiceDescriptor getDescriptor() {
		return descriptor
	}

	void descriptor(Closure closure) {
		plugin.project.configure(descriptor, closure)
	}

	protected File getServiceFile() {
		def servicesDir = new File(plugin.project.buildDir, 'systemd/services')
		return new File(servicesDir, getPackageName() + '.service')
	}

	protected File getScriptsDir() {
		return new File(plugin.project.buildDir, 'systemd/scripts')
	}

	CreateStartScripts getStartScripts() {
		if (startScripts == null) {
			return plugin.project.tasks.getByName('startScripts')
		}
		return startScripts
	}

	void setStartScripts(CreateStartScripts startScripts) {
		this.startScripts = startScripts
	}

	String getConfigDir() {
		if (configDir == null) {
			return "/etc/" + getPackageName()
		}
		return configDir
	}

	void setConfigDir(String configDir) {
		this.configDir = configDir
	}

	String getPackageName() {
		init()

		if (packageName == null && startScripts != null) {
			return startScripts.applicationName
		}

		return packageName
	}

	void setPackageName(String packageName) {
		this.packageName = packageName
	}

	String getPackageDir() {
		if (packageDir == null) {
			return "/var/" + getPackageName()
		}
		return packageDir
	}

	protected String getPackageBinDir() {
		return getPackageDir() + "/bin"
	}

	protected String getPackageLibDir() {
		return getPackageDir() + "/lib"
	}

	protected String getBinaryPath() {
		return getPackageBinDir() + getStartScripts().applicationName
	}

	void setPackageDir(String binaryDir) {
		this.packageDir = binaryDir
	}

	String getWorkingDir() {
		if (workingDir == null) {
			return getPackageBinDir()
		}
		return workingDir
	}

	void setWorkingDir(String workingDir) {
		this.workingDir = workingDir
	}

	String getUser() {
		if (user == null) {
			return getPackageName()
		}
		return user
	}

	void setUser(String userName) {
		this.user = userName
	}

	String getPermissionGroup() {
		if (permissionGroup == null) {
			return getUser()
		}
		return permissionGroup
	}

	void setPermissionGroup(String groupName) {
		this.permissionGroup = groupName
	}

	void configFile(File file) {
		configFiles.add(file)
	}

	Collection<File> getConfigFiles() {
		return configFiles
	}

	void setConfigFiles(Collection<File> configFiles) {
		this.configFiles = configFiles
	}

	SystemdApplicationPlugin getPlugin() {
		return plugin
	}

	void setPlugin(SystemdApplicationPlugin plugin) {
		this.plugin = plugin
	}

	void init() {
		plugin.init()
	}
}
