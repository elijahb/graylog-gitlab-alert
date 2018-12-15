package com.gitlabIssuePlugin;

import org.graylog2.plugin.PluginModule;

public class GitlabIssueModule extends PluginModule {
    @SuppressWarnings("unused")
    protected void configure() {
        addAlarmCallback(GitlabIssueCallback.class);
    }
}
