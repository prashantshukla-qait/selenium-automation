package com.qait.automation.coach.behavedemo.getstory;

import static com.qait.automation.SAM.assessment.behavedemo.utils.YamlReader.getData;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.domain.Comment;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.Transition;
import com.atlassian.jira.rest.client.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;
public class JIRAScenarioResultPublisher {

	public  static  String completeScenario="";
	public  static  String completedScenario="";
	public static String jiraCommentUrl="";
	public static StringBuffer sBuffer = new StringBuffer(15);

	public static void addScenarioStatusInJIRAComment(String storyID, String comment){
		final JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory();
		URI jiraServerUri;
		try {
			jiraServerUri = new URI(Constants.JIRA_URL);
			final com.atlassian.jira.rest.client.JiraRestClient restClient = factory
					.createWithBasicHttpAuthentication(jiraServerUri,
							Constants.JIRA_USERNAME, Constants.JIRA_PASSWORD);
			final NullProgressMonitor pm = new NullProgressMonitor();
			Issue issue = restClient.getIssueClient().getIssue(storyID, pm);
			issue.getCommentsUri(); 
			final IssueRestClient client = restClient.getIssueClient();
			jiraCommentUrl=Constants.JIRA_URL+ "/rest/api/latest/issue/"+storyID+"/comment";
			System.out.println("jiraCommentUrl=="+ jiraCommentUrl);
			client.addComment(pm, new URI(jiraCommentUrl), Comment.valueOf(comment));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public static void jiraFeatureFileDownloader() {
		final JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory();
		URI jiraServerUri;
		try {
			jiraServerUri = new URI(Constants.JIRA_URL);
			final com.atlassian.jira.rest.client.JiraRestClient restClient = factory
					.createWithBasicHttpAuthentication(jiraServerUri,
							Constants.JIRA_USERNAME, Constants.JIRA_PASSWORD);
			final NullProgressMonitor pm = new NullProgressMonitor();
			Issue issue = restClient.getIssueClient().getIssue(getData("storyID"), pm);
			System.out.println(issue.getAttachmentsUri());
			System.out.println(issue.getCommentsUri());
			// restClient.getIssueClient().addAttachments(pm, new
			// URI("http://10.0.20.227:8080/rest/api/latest/issue/10001/attachments"),new
			// File("./result_upload_on_jira.txt") );

			final IssueRestClient client = restClient.getIssueClient();
			// client.addComment(pm, new
			// URI("http://10.0.20.227:8080/rest/api/latest/issue/10001/comment"),
			// Comment.valueOf("***********PASSED###############"));

			Iterator<Transition> iter = client.getTransitions(
					client.getIssue(getData("storyID"), pm), pm).iterator();
			System.out.println(issue.getStatus().getName());
			while (iter.hasNext()) {
				Transition transition = iter.next();
				System.out.println(transition.getName() + "-ID-"
						+ transition.getId());
				if (transition.getName().equals("Close Issue")) {
					client.transition(issue, new TransitionInput(transition.getId()), pm);
				}
			}
			System.out.println(issue.getStatus().getName());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}


}