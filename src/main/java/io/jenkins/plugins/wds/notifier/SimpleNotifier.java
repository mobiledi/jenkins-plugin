package io.jenkins.plugins.wds.notifier;

import hudson.model.AbstractBuild;

public interface SimpleNotifier {

    void completed(AbstractBuild r);
}
