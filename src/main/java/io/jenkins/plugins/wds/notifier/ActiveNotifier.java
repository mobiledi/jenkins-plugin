package io.jenkins.plugins.wds.notifier;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import io.jenkins.plugins.wds.WorkDaySyncModel;
import io.jenkins.plugins.wds.WorkDaySyncNotifier;
import io.jenkins.plugins.wds.WorkDaySyncService;
import org.jenkinsci.plugins.displayurlapi.DisplayURLProvider;

public class ActiveNotifier implements SimpleNotifier{
    private final WorkDaySyncNotifier notifier;
    private final BuildListener listener;

    public ActiveNotifier(WorkDaySyncNotifier notifier, BuildListener listener) {
        this.notifier = notifier;
        this.listener = listener;
    }

    public void completed(AbstractBuild build) {
        WorkDaySyncModel report = new WorkDaySyncModel();
        report.setBuildNumber(build.getNumber());
        report.setJenkinsURL(DisplayURLProvider.get().getRunURL(build));
        report.setProject(build.getProject().getName());
        report.setStatus(build.getResult().toString());
        report.setMessage(this.notifier.getCustomMessage());

        getWDSService(build).sendReport(report);
    }

    private WorkDaySyncService getWDSService(AbstractBuild r) {
        return this.notifier.getWDSService(r, listener);
    }
}
