package com.gitlabIssuePlugin;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackException;
import org.json.JSONArray;

public class GitlabAPI {

  protected HttpClient client;

  protected String createIssuesUrl = "%s/api/v4/projects/%s/issues";

  protected String issuesUrl = "%s/api/v4/issues?search=%s";

  protected String projectsUrl = "%s/api/v4/projects?search=%s"; 

  protected String token;

  protected String gitlabUrl;

  GitlabAPI(HttpClient client, String token, String gitlabUrl) {
    this.client = client;
    this.token = token;
    this.gitlabUrl = gitlabUrl;
  }

  public void create(Issue issue) throws IOException, AlarmCallbackException {
    HttpPost request = new HttpPost(String.format(this.gitlabUrl, this.createIssuesUrl, issue.projectId));

    request.setHeader("PRIVATE-TOKEN", this.token);

    List<NameValuePair> params = new ArrayList<>(4);
    params.add(new BasicNameValuePair("title", issue.title));
    params.add(new BasicNameValuePair("description", issue.description));
    params.add(new BasicNameValuePair("labels", issue.labels));

    request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

    HttpResponse response = client.execute(request);
    int status = response.getStatusLine().getStatusCode();
    if (status != 201) {
        String body = new BasicResponseHandler().handleResponse(response);
        String error = String.format("API request was unsuccessful (%d): %s", status, body);
        throw new AlarmCallbackException(error);
    }

  }

  public Number findId(String projectName) throws IOException, AlarmCallbackException {
    HttpGet request = new HttpGet(String.format(this.gitlabUrl, this.projectsUrl, URLEncoder.encode(projectName)));

    request.setHeader("PRIVATE-TOKEN", this.token);

    HttpResponse response = client.execute(request);
    int status = response.getStatusLine().getStatusCode();
    String body = new BasicResponseHandler().handleResponse(response);
    if (status != 200) {
        
        String error = String.format("API request was unsuccessful (%d): %s", status, body);
        throw new AlarmCallbackException(error);
    }

    JSONArray projects = new JSONArray(body);
    return projects.getJSONObject(0).getNumber("id");
  }  

  public Boolean issueExists(String title) throws IOException, AlarmCallbackException {
    HttpGet request = new HttpGet(String.format(this.gitlabUrl, this.issuesUrl, URLEncoder.encode(title)));

    request.setHeader("PRIVATE-TOKEN", this.token);

    HttpResponse response = client.execute(request);
    int status = response.getStatusLine().getStatusCode();
    String body = new BasicResponseHandler().handleResponse(response);
    if (status != 200) {
        
        String error = String.format("API request was unsuccessful (%d): %s", status, body);
        throw new AlarmCallbackException(error);
    }

    JSONArray issues = new JSONArray(body);
    return issues.length() > 0;
  }  

}