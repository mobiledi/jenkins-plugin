package io.jenkins.plugins.wds.workflow;

import hudson.AbortException;
import hudson.Extension;
import hudson.model.TaskListener;
import io.jenkins.plugins.wds.Messages;
import io.jenkins.plugins.wds.WorkDaySyncModel;
import io.jenkins.plugins.wds.WorkDaySyncNotifier;
import io.jenkins.plugins.wds.WorkDaySyncService;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * Workflow step to send a Slack channel notification.
 */
public class WDSSendStep extends AbstractStepImpl {

    private final @Nonnull String token;
    private final String apiURL;
    private boolean failOnError;

    public String getToken() {
        return token;
    }

    public String getApiURL() {
        return apiURL;
    }

    public boolean isFailOnError() {
        return failOnError;
    }

    @DataBoundSetter
    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public WDSSendStep(@Nonnull String token, @Nonnull String apiURL) {
        this.token = token;
        this.apiURL = apiURL;
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(WDSSendStepExecution.class);
        }

        @Override
        public String getFunctionName() {
            return Messages.wdsSendStepFunctionName();
        }

        @Override
        public String getDisplayName() {
            return "TEST 2" + Messages.wdsSendStepDisplayName();
        }
    }

    public static class WDSSendStepExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {

        private static final long serialVersionUID = 1L;

        @Inject
        transient WDSSendStep step;

        @StepContextParameter
        transient TaskListener listener;

        @Override
        protected Void run() throws Exception {

            //default to global config values if not set in step, but allow step to override all global settings
            Jenkins jenkins;
            //Jenkins.getInstance() may return null, no message sent in that case
            try {
                jenkins = Jenkins.getInstance();
            } catch (NullPointerException ne) {
                listener.error(Messages.notificationFailedWithException(ne));
                return null;
            }
            WorkDaySyncNotifier.DescriptorImpl wdsDesc = jenkins.getDescriptorByType(WorkDaySyncNotifier.DescriptorImpl.class);
            listener.getLogger().println("run workDaySync, step " + step.token);
            String apiURL = step.apiURL != null ? step.apiURL : wdsDesc.getApiURL();

            //placing in console log to simplify testing of retrieving values from global config or from step field; also used for tests
            listener.getLogger().println(Messages.wdsSendStepConfig(step.apiURL == null));

            WorkDaySyncService service = new WorkDaySyncService(apiURL, step.token);
            WorkDaySyncModel report = new WorkDaySyncModel();

            boolean publishSuccess = service.sendReport(report);

            if (!publishSuccess && step.failOnError) {
                throw new AbortException(Messages.notificationFailed());
            } else if (!publishSuccess) {
                listener.error(Messages.notificationFailed());
            }
            return null;
        }
    }
}
