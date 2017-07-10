/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hudson.plugins.sidebar_link;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.model.FreeStyleProject;
import hudson.model.Job;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 * @author Lucie Votypkova
 */
public class ProjectLinksTest {
    
    @Rule
    public JenkinsRule rule = new JenkinsRule();
    
    @Test
    public void testGetJobActionsDoesNotReturnNull() throws Exception{
        Job project = rule.jenkins.createProject(FreeStyleProject.class, "test_not_null");
        ProjectLinks links = new ProjectLinks(null);
        assertNotNull("Method geJobActions should not return null.", links.getJobActions(project));
        //from gui
        HtmlPage w = rule.createWebClient().goTo(project.getUrl() + "configure");
        HtmlForm form = w.getFormByName("config");
        form.getInputByName("sidebar-links").click();
        rule.submit(form);
        assertNotNull("Method geJobActions should not return null.", links.getJobActions(project));
    }

    @Test
    public void testGetWorkflowJobActionsDoesNotReturnNull() throws Exception{
        Job project = rule.jenkins.createProject(WorkflowJob.class, "test_workflow_not_null");
        ProjectLinks links = new ProjectLinks(null);
        assertNotNull("Method geJobActions should not return null.", links.getJobActions(project));
        //from gui
        HtmlPage w = rule.createWebClient().goTo(project.getUrl() + "configure");
        HtmlForm form = w.getFormByName("config");
        form.getInputByName("sidebar-links").click();
        rule.submit(form);
        assertNotNull("Method geJobActions should not return null.", links.getJobActions(project));
    }

    @Test
    public void testLinkActionIsAddedWithJobProps () throws Exception{
        Job p1 = rule.jenkins.createProject(FreeStyleProject.class, "burt");
        p1.addProperty(new ProjectLinks(Collections.singletonList(new LinkAction("http://google.com","stop","edit-delete.png"))));
        assertEquals(1, p1.getActions(LinkAction.class).size());

        Job p2 = rule.jenkins.createProject(WorkflowJob.class, "ernie");
        p2.addProperty(new ProjectLinks(Collections.singletonList(new LinkAction("http://google.com","stop","edit-delete.png"))));
        assertEquals(1, p2.getActions(LinkAction.class).size());
    }

}
