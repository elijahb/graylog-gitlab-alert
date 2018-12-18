package com.gitlabIssuePlugin;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;

import org.apache.http.impl.client.HttpClients;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("You need to setup a test repository and configure it in config.properties file")
public class GitlabAPITest {

  protected GitlabAPI gitlab;

  protected Properties options = new Properties();

  @Before
  public void setup() throws FileNotFoundException, IOException {
    this.options.load(getClass().getResourceAsStream("config.properties"));
    this.gitlab = new GitlabAPI(HttpClients.createDefault(), this.options.getProperty("PRIVATE_TOKEN"), "https://gitlab.com");
    System.out.print(this.options.getProperty("PRIVATE_TOKEN"));
  }

  @Test
  public void findProjectId() throws IOException, AlarmCallbackException {
    Number projectId = 0;

    projectId = this.gitlab.findId(this.options.getProperty("TEST_REPOSITORY"));

    assertEquals(this.options.getProperty("PROJECT_ID"), projectId.toString());
  }

  @Test
  public void createIssueAndCheckIfExists() throws IOException, AlarmCallbackException {
    Issue testIssue = new Issue();
    testIssue.title = "Test ".concat(Long.toHexString(Double.doubleToLongBits(Math.random())));
    testIssue.description = "Test \nTest";
    testIssue.labels = "test";
    testIssue.projectId = this.options.getProperty("PROJECT_ID") ;
    assertEquals(false, this.gitlab.issueExists(testIssue.title));
    this.gitlab.create(testIssue);
    assertEquals(true, this.gitlab.issueExists(testIssue.title));
  }

}
