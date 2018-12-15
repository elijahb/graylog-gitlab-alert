# Gitlab Issue Alert

[![Build Status](https://travis-ci.org/elijahb/graylog-gitlab-alert.svg?branch=master)](https://travis-ci.org/elijahb/graylog-gitlab-alert)
[![Graylog Marketplace](https://img.shields.io/badge/Graylog-Marketplace-blue.svg)](https://marketplace.graylog.org/addons/ad0ebcaf-1a6f-4226-80b6-cb530875c964)

Gitlab Issue Alert allows you to create a Gitlab Issue from [Graylog](https://www.graylog.org) alert messages.

## Requirements

Requires Graylog 2.0 or later.

## Installation

[Download the plugin](https://github.com/elijahb/graylog-gitlab-alert/releases/latest)
and place the `.jar` file in your plugins folder that is configured in your `graylog.conf`
as described in the [Graylog documentation](http://docs.graylog.org/en/latest/pages/plugins.html#installing-and-loading-plugins).

Restart graylog-server: `service graylog-server restart`

## Usage

### Step 1

Create a new [private token](https://docs.gitlab.com/ee/user/profile/personal_access_tokens.html#creating-a-personal-access-token) for the user that will be used to open the issue

### Step 2

Add a new alert notification in your Graylog-interface and select `Gitlab Issue` as the notification type.

The message is a template that can be configured as described in the [Graylog Documentation](http://docs.graylog.org/en/latest/pages/streams/alerts.html#email-alert-notification)


## Build

This project is using [Maven](https://maven.apache.org) and requires Java 8 or higher.

You can build the plugin using the following command:

```bash
mvn package
```

The plugin file `gitlab-issue-x.x.x.jar` will be saved in the `target` directory

## Plugin Release

Versions are released using the [maven release plugin](https://maven.apache.org/maven-release/maven-release-plugin/):

```bash
mvn release:prepare
mvn release:perform
```

TravisCI builds and uploads the release artifacts automatically.

## Credits

Based on [TelegramAlert](https://github.com/irgendwr/TelegramAlert)

