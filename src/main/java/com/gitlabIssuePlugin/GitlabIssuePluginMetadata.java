package com.gitlabIssuePlugin;

import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.Version;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

public class GitlabIssuePluginMetadata implements PluginMetaData {
    @Override
    public String getUniqueId() {
        return GitlabIssuePlugin.class.getPackage().getName();
    }

    @Override
    public String getName() {
        return "Gitlab Issue";
    }

    @Override
    public String getAuthor() {
        return "Elijah Baley";
    }

    @Override
    public URI getURL() {
        return URI.create("https://elijahb.github.io/graylog-gitlab-alert/");
    }

    @Override
    public Version getVersion() {
        return new Version(2, 1, 1);
    }

    @Override
    public String getDescription() {
        return "Creates Gitlab issues from Graylog alerts";
    }

    @Override
    public Version getRequiredVersion() {
        return new Version(2, 0, 0);
    }

    @Override
    public Set<ServerStatus.Capability> getRequiredCapabilities() {
        return Collections.emptySet();
    }
}
