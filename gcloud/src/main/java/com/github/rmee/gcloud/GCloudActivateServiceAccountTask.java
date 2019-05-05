package com.github.rmee.gcloud;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class GCloudActivateServiceAccountTask extends DefaultTask {

    public GCloudActivateServiceAccountTask() {
        getOutputs().upToDateWhen(element -> {
            GCloudExtension extension = getExtension();
            File credentialsFile = extension.getClient().getHome(".config/gcloud/credentials");
            if (!credentialsFile.exists()) {
                return false;
            }
            // looks like:
            //{
            //	"data": [
            //	{
            //		"credential": { ...
            //			"token_expiry": "2018-09-25T14:33:35Z", ...
            Map<String, List<Map<String, Map<String, String>>>> config;
            try (FileInputStream fis = new FileInputStream(credentialsFile)) {
                Yaml yaml = new Yaml();
                config = yaml.load(fis); // not loadAs (we're just feeling lucky)
            } catch (IOException e) {
                getProject().getLogger().warn("failed reading " + credentialsFile, e);
                return false;
            }
            Date expiryDate;
            try {
                String expiryStr = config.get("data").get(0).get("credential").get("token_expiry");
                expiryDate = Date.from(Instant.parse(expiryStr));
            } catch (Exception e) {
                // if this this file is ill formatted you end up in here
                getProject().getLogger().info("failed parsing " + credentialsFile, e);
                return false;
            }
            if (expiryDate.before(new Date())) {
                return false;
            }
            return true;
        });
    }

    private GCloudExtension getExtension() {
        return getProject().getExtensions().getByType(GCloudExtension.class);
    }

    @InputFile
    public File getKeyFile() {
        return getExtension().getKeyFile();
    }

    @TaskAction
    public void run() {
        File keyFile = getKeyFile();
        StringBuilder command = new StringBuilder();
        command.append("gcloud auth activate-service-account");

        command.append(" --key-file ");
        command.append(keyFile);

		/*
		if (servicePrincipal) {
			command.append(" --service-principal");
		}
		if (userName != null) {
			command.append(" -u " + userName);
		}
		if (password != null) {
			command.append(" -p " + password);
		}
		if (tenantId != null) {
			command.append(" --tenant " + tenantId);
		}
		*/

        // 	command = new StringBuilder("gcloud info --format=json");


        File tempFile;
        try {
            tempFile = File.createTempFile("gcloud-activate-service-account", "txt");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        GCloudExecSpec execSpec = new GCloudExecSpec();
        //		execSpec.setVolumesFrom(null);
        //	execSpec.setContainerName(extension.getContainerName());

        execSpec.setCommandLine(command.toString());
        //execSpec.setStdoutFile(tempFile);
        getExtension().exec(execSpec);

/*
		if (GCloudExtension.getSubscriptionId() == null || GCloudExtension.getTenantId() == null) {
			List logins = (List) new JsonSlurper().parse(tempFile, "utf8");
			if (logins.size() != 1) {
				throw new IllegalStateException("only single subscription implemented, got " + logins);
			}
			Map subscription = (Map) logins.get(0);
			String subscriptionId = Objects.requireNonNull((String) subscription.get("id"));
			String subTenantId = Objects.requireNonNull((String) subscription.get("tenantId"));

			if (GCloudExtension.getTenantId() == null) {
				GCloudExtension.setTenantId(subTenantId);
			}
			if (GCloudExtension.getSubscriptionId() == null) {
				GCloudExtension.setSubscriptionId(subscriptionId);
			}
		}
		*/
    }
}
