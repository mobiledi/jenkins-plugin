package io.jenkins.plugins.wds;

import hudson.Extension;
import hudson.model.*;
import hudson.model.listeners.RunListener;
import hudson.tasks.Publisher;
import io.jenkins.plugins.wds.notifier.ActiveNotifier;
import io.jenkins.plugins.wds.notifier.DisabledNotifier;
import io.jenkins.plugins.wds.notifier.SimpleNotifier;

import java.util.Map;
import java.util.logging.Logger;

@Extension
@SuppressWarnings("rawtypes")
public class WorkDaySyncListener extends RunListener<AbstractBuild> {

    private static final Logger logger = Logger.getLogger(WorkDaySyncListener.class.getName());

    public WorkDaySyncListener() {
        super(AbstractBuild.class);
    }

    @Override
    public void onCompleted(AbstractBuild r, TaskListener listener) {
        getNotifier(r.getProject(), listener).completed(r);
        super.onCompleted(r, listener);
    }

    @Override
    public void onStarted(AbstractBuild r, TaskListener listener) {
    }

    @Override
    public void onDeleted(AbstractBuild r) {
    }

    @Override
    public void onFinalized(AbstractBuild r) {
//         getNotifier(r.getProject(), null).finalized(r);
//         super.onFinalized(r);
    }

    @SuppressWarnings("unchecked")
    SimpleNotifier getNotifier(AbstractProject project, TaskListener listener) {
        Map<Descriptor<Publisher>, Publisher> map = project.getPublishersList().toMap();
        for (Publisher publisher : map.values()) {
            if (publisher instanceof WorkDaySyncNotifier) {
                return new ActiveNotifier((WorkDaySyncNotifier) publisher, (BuildListener)listener);
            }
        }
        return new DisabledNotifier();
    }

}
