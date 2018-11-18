package com.github.rmee.helm;

public class HelmCredentials {

    private String user;

    private String pass;

    private HelmExtension extension;

    protected HelmCredentials(HelmExtension extension){
        this.extension = extension;
    }

    public String getUser() {
        extension.init();
        return user;
    }

    public void setUser(String user) {
        extension.checkNotInitialized();
        this.user = user;
    }

    public String getPass() {
        extension.init();
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
