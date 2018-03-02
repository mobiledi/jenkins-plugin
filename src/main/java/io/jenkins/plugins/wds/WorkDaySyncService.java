package io.jenkins.plugins.wds;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkDaySyncService {
    private static final Logger logger = Logger.getLogger(WorkDaySyncService.class.getName());
    private static final String URL = "/plugins/jenkins";
    private final String wdsAPI;
    private final String token;

    public WorkDaySyncService(String wdsAPI, String token) {
        this.wdsAPI = wdsAPI;
        this.token = token;
    }

    public String getWdsAPI() {
        return wdsAPI;
    }

    public boolean ping() {
        return SimpleHttpRequest.get(this.wdsAPI + URL);
    }

    public boolean sendReport(WorkDaySyncModel data) {
        logger.log(Level.WARNING, data.toString());
        logger.log(Level.WARNING, data.getStatus());

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("token", token));
        params.add(new BasicNameValuePair("data", data.toString()));

        return SimpleHttpRequest.post(this.wdsAPI + URL, params);
    }
}
