package com.github.rmee.application.systemd

class SystemdApplicationExtension {

	private String packageName

	private String user

	private String permissionGroup

	private String binaryName

	private String packageDir

	private String workingDir

	private String configFileName

	private String configDir

	private SystemdServiceDescriptor descriptor = new SystemdServiceDescriptor()

	protected SystemdApplicationPlugin plugin


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

	String getConfigDir() {
		if (configDir == null) {
			return "/etc/" + getPackageName() + "/"
		}
		return configDir
	}

	void setConfigDir(String configDir) {
		this.configDir = configDir
	}

	String getPackageName() {
		init()
		return packageName
	}

	void setPackageName(String packageName) {
		this.packageName = packageName
	}


	String getBinaryName() {
		if (binaryName == null) {
			return getPackageName()
		}
		return binaryName
	}

	void setBinaryName(String binaryName) {
		this.binaryName = binaryName
	}

	String getPackageDir() {
		if (packageDir == null) {
			return "/var/" + getPackageName() + "/"
		}
		return packageDir
	}

	protected String getPackageBinDir() {
		return getPackageDir() + "bin/"
	}

	protected String getPackageLibDir() {
		return getPackageDir() + "bin/"
	}

	protected String getBinaryPath() {
		return getPackageBinDir() + getBinaryName()
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
			return getBinaryName()
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


	String getConfigFileName() {
		return configFileName
	}

	void setConfigFileName(String configFileName) {
		this.configFileName = configFileName
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
