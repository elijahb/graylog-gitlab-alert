package com.gitlabIssuePlugin;

import java.io.IOException;

import org.graylog2.plugin.alarms.callbacks.AlarmCallbackException;

class GitlabBot {

    protected GitlabAPI gitlab;
    

    GitlabBot(GitlabAPI gitlab) {
        this.gitlab = gitlab;
    }

    void sendMessage(String projectName, String title, String description, String labels) throws IOException, AlarmCallbackException {

        Number projectId = this.gitlab.findId(projectName);
        
        Issue issue = new Issue();
        issue.title = title;
        issue.description = description;
        issue.projectId = projectId.toString();
        issue.labels = labels;

        if (!this.gitlab.issueExists(title)) {
            this.gitlab.create(issue);
        }
        
    }
}
