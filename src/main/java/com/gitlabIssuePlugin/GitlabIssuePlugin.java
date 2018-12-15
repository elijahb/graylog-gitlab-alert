package com.gitlabIssuePlugin;

import org.graylog2.plugin.Plugin;
import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.PluginModule;

import java.util.Collection;
import java.util.Collections;

public class GitlabIssuePlugin implements Plugin {
    @Override
    public PluginMetaData metadata() {
        return new GitlabIssuePluginMetadata();
    }

    @Override
    public Collection<PluginModule> modules () {
        return Collections.singletonList(new GitlabIssueModule());
    }
}
