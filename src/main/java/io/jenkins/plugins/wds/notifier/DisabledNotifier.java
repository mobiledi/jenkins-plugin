package io.jenkins.plugins.wds.notifier;

import hudson.model.AbstractBuild;

public class DisabledNotifier implements SimpleNotifier {

    public void completed(AbstractBuild r) {
    }
}
