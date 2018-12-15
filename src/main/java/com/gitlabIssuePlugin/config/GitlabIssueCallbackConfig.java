package com.gitlabIssuePlugin.config;

import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.configuration.ConfigurationException;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.configuration.fields.ConfigurationField;
import org.graylog2.plugin.configuration.fields.TextField;
import org.graylog2.plugin.configuration.fields.TextField.Attribute;

public class GitlabIssueCallbackConfig {
    private static final String ERROR_NOT_SET = "%s is mandatory and must not be empty.";

    public static ConfigurationRequest createRequest() {
        final ConfigurationRequest configurationRequest = new ConfigurationRequest();

        configurationRequest.addField(new TextField(
                Config.MESSAGE, "Message",
                "[${stream.title}](${stream_url}): ${alert_condition.title}\n" +
                "```\n" +
                "${foreach backlog message}\n" +
                "${message.message}\n\\n" +
                "${end}\n" +
                "```",
                "See http://docs.graylog.org/en/latest/pages/streams/alerts.html#email-alert-notification for more details.",
                ConfigurationField.Optional.NOT_OPTIONAL,
                Attribute.TEXTAREA
        ));

        configurationRequest.addField(new TextField(
                Config.PROJECT_FIELD, "Project Field", "", "Stream property associated to Gitlab's project",
                ConfigurationField.Optional.NOT_OPTIONAL
        ));

        configurationRequest.addField(new TextField(
                Config.LABELS, "Lables", null,
                "Comma separated labels list to add to the issue",
                ConfigurationField.Optional.OPTIONAL
        ));

        configurationRequest.addField(new TextField(
                Config.TOKEN, "API Token", "",
                "Private token of a user which will be used to open the issue",
                ConfigurationField.Optional.NOT_OPTIONAL,
                Attribute.IS_PASSWORD
        ));
        configurationRequest.addField(new TextField(
                Config.GRAYLOG_URL, "Graylog URL", "",
                "URL to your Graylog web interface. Used to build links in alarm notification.",
                ConfigurationField.Optional.NOT_OPTIONAL
        ));
        configurationRequest.addField(new TextField(
                Config.PROXY, "Proxy", null,
                "Proxy address in the following format: <ProxyAddress>:<Port>",
                ConfigurationField.Optional.OPTIONAL
        ));

        return configurationRequest;
    }

    public static void check(Configuration config) throws ConfigurationException {
        String[] mandatoryFields = {
            Config.MESSAGE,
            Config.PROJECT_FIELD,
            Config.TOKEN,
            Config.GRAYLOG_URL
        };

        for (String field : mandatoryFields) {
            if (!config.stringIsSet(field)) {
                throw new ConfigurationException(String.format(ERROR_NOT_SET, field));
            }
        }

        if (config.stringIsSet(Config.PROXY)) {
            String proxy = config.getString(Config.PROXY);
            assert proxy != null;
            String[] proxyArr = proxy.split(":");
            if (proxyArr.length != 2 || Integer.parseInt(proxyArr[1]) == 0) {
                throw new ConfigurationException("Invalid Proxy format.");
            }
        }
    }
}
