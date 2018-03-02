package io.jenkins.plugins.wds;

import net.sf.json.JSONObject;

public class WorkDaySyncModel {
    private String jenkinsURL;
    private Integer buildNumber;
    private String status;
    private String message;
    private String project;

    public String toString() {
        return JSONObject.fromObject(this).toString();
    }

    public void setJenkinsURL(String jenkinsURL) {
        this.jenkinsURL = jenkinsURL;
    }

    public void setBuildNumber(Integer buildNumber) {
        this.buildNumber = buildNumber;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getJenkinsURL() {
        return jenkinsURL;
    }

    public Integer getBuildNumber() {
        return buildNumber;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getProject() {
        return project;
    }
}
