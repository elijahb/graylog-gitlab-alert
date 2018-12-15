package com.gitlabIssuePlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.floreysoft.jmte.Engine;
import com.google.inject.Inject;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.MessageSummary;
import org.graylog2.plugin.alarms.AlertCondition;
import org.graylog2.plugin.alarms.callbacks.*;
import org.graylog2.plugin.configuration.*;
import org.graylog2.plugin.streams.Stream;

import com.gitlabIssuePlugin.config.Config;
import com.gitlabIssuePlugin.config.GitlabIssueCallbackConfig;

public class GitlabIssueCallback implements AlarmCallback {
    protected Configuration config;
    protected GitlabBot bot;
    protected Engine templateEngine;
    protected Logger logger;  
    
    @Inject
    public GitlabIssueCallback(Engine templateEngine) {
        this.templateEngine = templateEngine;
        this.logger = Logger.getLogger("GitlabIssue");
    }

    @Override
    public void initialize(Configuration config) throws AlarmCallbackConfigurationException {
        this.config = config;
        String proxyUrl = config.getString(Config.PROXY);
        HttpClient client;

        try {
            checkConfiguration();
        } catch (ConfigurationException e) {
            throw new AlarmCallbackConfigurationException("Configuration error: " + e.getMessage());
        }

        if (!proxyUrl.isEmpty()) {
            String[] proxyArr = proxyUrl.split(":");
            HttpHost proxy = new HttpHost(proxyArr[0], Integer.parseInt(proxyArr[1]));
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            client = HttpClients.custom()
                    .setRoutePlanner(routePlanner)
                    .build();
        } else {
            client = HttpClients.createDefault();
        }
        
        this.bot = new GitlabBot(new GitlabAPI(client, config.getString(Config.TOKEN)));
    }

    @Override
    public void call(Stream stream, AlertCondition.CheckResult result) throws AlarmCallbackException {
        List<MessageSummary> matchingMessages = result.getMatchingMessages();
        MessageSummary message = matchingMessages.get(0);

        if (message.getField(this.config.getString(Config.PROJECT_FIELD)) == null) {
            logger.warning("Can't open issue, message doesn't include project field property");
            return ;
        }

        try {
            this.bot.sendMessage(
                message.getField(this.config.getString(Config.PROJECT_FIELD)).toString(),
                message.getMessage(),
                buildMessage(stream, result),
                this.config.getString(Config.LABELS)
            );
        } catch (IOException e) {
            String error = "API request failed: " + e.getMessage();
            logger.warning(error);
            e.printStackTrace();
            throw new AlarmCallbackException(error);
        }
    }
    
    private String buildMessage(Stream stream, AlertCondition.CheckResult result) {

        List<Message> backlog = getAlarmBacklog(result);
        Map<String, Object> model = getModel(stream, result, backlog);
        try {
            return templateEngine.transform(config.getString(Config.MESSAGE), model);
        } catch (Exception ex) {
            return ex.toString();
        }
    }
    
    private Map<String, Object> getModel(Stream stream, AlertCondition.CheckResult result, List<Message> backlog) {
        Map<String, Object> model = new HashMap<>();
        model.put("stream", stream);
        model.put("check_result", result);
        model.put("alert_condition", result.getTriggeredCondition());
        model.put("backlog", backlog);
        model.put("backlog_size", backlog.size());
        model.put("stream_url", buildStreamLink(stream));

        return model;
    }

    private List<Message> getAlarmBacklog(AlertCondition.CheckResult result) {
        final AlertCondition alertCondition = result.getTriggeredCondition();
        final List<MessageSummary> matchingMessages = result.getMatchingMessages();
        final int effectiveBacklogSize = Math.min(alertCondition.getBacklog(), matchingMessages.size());

        if (effectiveBacklogSize == 0) return Collections.emptyList();
        final List<MessageSummary> backlogSummaries = matchingMessages.subList(0, effectiveBacklogSize);
        final List<Message> backlog = new ArrayList<>(effectiveBacklogSize);
        for (MessageSummary messageSummary : backlogSummaries) {
            backlog.add(messageSummary.getRawMessage());
        }

        return backlog;
    }

    private String buildStreamLink(Stream stream) {
        return getGraylogURL() + "streams/" + stream.getId() + "/messages?q=%2A&rangetype=relative&relative=3600";
    }
   
    private String getGraylogURL() {
        String url = config.getString(Config.GRAYLOG_URL);
        
        if (url != null && !url.endsWith("/")) {
            url += "/";
        }
        
        return url;
    }

    @Override
    public ConfigurationRequest getRequestedConfiguration() {
        return GitlabIssueCallbackConfig.createRequest();
    }

    @Override
    public String getName() {
        return "Gitlab Issue";
    }

    @Override
    public Map<String, Object> getAttributes() {
        return config.getSource();
    }

    @Override
    public void checkConfiguration() throws ConfigurationException {
        GitlabIssueCallbackConfig.check(config);
    }
}
