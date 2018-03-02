package io.jenkins.plugins.wds;

import com.google.common.base.Strings;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.*;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkDaySyncNotifier extends Notifier {
    private static final Logger logger = Logger.getLogger(WorkDaySyncNotifier.class.getName());
    private String apiURL;
    private String token;
    private String customMessage;

    @DataBoundConstructor
    public WorkDaySyncNotifier(String apiURL, String token, String customMessage) {
        super();
        this.apiURL = apiURL;
        this.token = token;
        this.customMessage = customMessage;
    }

    public WorkDaySyncNotifier(String apiURL, String token) {
        this.apiURL = apiURL;
        this.token = token;
    }

    @DataBoundSetter
    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @DataBoundSetter
    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }

    public String getCustomMessage() {
        return customMessage;
    }

    @DataBoundSetter
    public void setApiURL(String apiURL) {
        this.apiURL = apiURL;
    }

    public String getApiURL() {
        return apiURL;
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }
    
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        return true;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    public WorkDaySyncService getWDSService(AbstractBuild r, BuildListener listener) {
        String apiURL = this.apiURL;
        if (StringUtils.isEmpty(apiURL)) {
            apiURL = getDescriptor().getApiURL();
        }

        return new WorkDaySyncService(apiURL, this.token);
    }

    /**
     * Job Level
     */
    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        private String apiURL = Messages.defaultApiURL();
        private String customMessage;
        private @Nonnull String token;

        public DescriptorImpl() {
            load();
        }

        public String getApiURL() {
            return apiURL;
        }

        @Nonnull
        public String getToken() {
            return token;
        }

        @DataBoundSetter
        public void setToken(String token) {
            this.token = token;
        }

        @DataBoundSetter
        public void setApiURL(String apiURL) {
            this.apiURL = apiURL;
        }

        @DataBoundSetter
        public void setCustomMessage(String customMessage) {
            this.customMessage = customMessage;
        }

        public String getCustomMessage() {
            return customMessage;
        }

        @Override
        public String getDisplayName() {
            return Messages.wdsSendStepDisplayName();
        }

        //WARN users that they should not use the plain/exposed token, but rather the token credential id
        public FormValidation doCheckToken(@QueryParameter String value) {
            return Strings.isNullOrEmpty(value) ?
                    FormValidation.error("Required field") : FormValidation.ok();
        }

        public FormValidation doTestConnection(
                @QueryParameter("wdsApiURL") final String apiURL,
                @QueryParameter("wdsToken") final String token) throws FormException {
            logger.log(Level.WARNING, "doTestConnection" + apiURL);
            try {
                WorkDaySyncService testService = new WorkDaySyncService(apiURL, token);
                boolean success = testService.ping();
                return success ? FormValidation.ok("Success") : FormValidation.error("Failure");
            } catch (Exception e) {
                return FormValidation.error("Client error : " + e.getMessage());
            }
        }

        @Override
        public boolean configure(StaplerRequest sr, JSONObject formData) throws FormException {
            apiURL = sr.getParameter("wdsApiURL");
            token = sr.getParameter("wdsToken");
            customMessage = sr.getParameter("wdsCustomMessage");
            save();
            return super.configure(sr, formData);
        }

        @Override
        public WorkDaySyncNotifier newInstance(StaplerRequest sr, JSONObject json) {
            String apiURL = sr.getParameter("wdsApiURL");
            String token = sr.getParameter("wdsToken");
            String customMessage = sr.getParameter("wdsCustomMessage");

            if(Strings.isNullOrEmpty(apiURL)) {
                apiURL = Messages.defaultApiURL();
            }

            return new WorkDaySyncNotifier(apiURL, token, customMessage);
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> t) {
            return true;
        }
    }
}
