/*
 * The MIT License
 *
 * Copyright (c) 2017, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.plugins.sidebar_link.steps;

import com.google.common.collect.ImmutableSet;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.Actionable;
import hudson.model.Result;
import hudson.model.Run;
import hudson.plugins.sidebar_link.LinkAction;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Adds a Sidebar link to the build page
 *
 * Steps
 *      add 1 link to build/job page:   sidebarLink(url, name, icon, jobPage, buildPage)
 *                     rm 0..* links:   sidebarLinkRemove(url: regex, name:regex)
 *
 * @author Alex Johnson
 */
public class SidebarLinkRemoveStep extends Step {

    private String urlRegex;
    private String displayNameRegex;
    private String iconFileNameRegex;

    private boolean jobPage = false;
    private boolean buildPage = true;

    @DataBoundConstructor
    public SidebarLinkRemoveStep(String urlRegex) {
        this.urlRegex = urlRegex;
    }

    @DataBoundSetter
    public void setDisplayNameRegex (String displayNameRegex) {
        this.displayNameRegex = displayNameRegex;
    }

    public String getDisplayNameRegex () {
        return displayNameRegex;
    }

    @DataBoundSetter
    public void setIconFileNameRegex (String iconFileNameRegex) {
        this.iconFileNameRegex = iconFileNameRegex;
    }

    public String getIconFileName () {
        return iconFileNameRegex;
    }

    @DataBoundSetter
    public void setJobPage (boolean jobPage) {
        this.jobPage = jobPage;
    }

    public boolean isJobPage () {
        return jobPage;
    }

    @DataBoundSetter
    public void setBuildPage (boolean buildPage) {
        this.buildPage = buildPage;
    }

    public boolean isbuildPage () {
        return buildPage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new SidebarLinkStepExecution(context, urlRegex, displayNameRegex, iconFileNameRegex, jobPage, buildPage);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public Set<Class<?>> getRequiredContext() {
            return ImmutableSet.of(Run.class, FilePath.class);
        }

        @Override
        public String getFunctionName() {
            return "sidebarLinkRm";
        }

        @Override
        public String getDisplayName() {
            return "Remove Sidebar Links";
        }
    }

    public static class SidebarLinkStepExecution extends StepExecution {

        protected String urlRegex;
        protected String displayNameRegex;
        protected String iconFileNameRegex;
        protected boolean jobPage;
        protected boolean buildPage;

        public SidebarLinkStepExecution (StepContext context, String urlRegex, String displayNameRegex,
                                         String iconFileNameRegex, boolean jobPage, boolean buildPage) {
            super(context);
            this.urlRegex = urlRegex;
            this.displayNameRegex = displayNameRegex;
            this.iconFileNameRegex = iconFileNameRegex;
            this.jobPage = jobPage;
            this.buildPage = buildPage;
            if (displayNameRegex == null) {
                this.displayNameRegex = urlRegex;
            }
        }

        @Override
        public boolean start() throws Exception {
            List<Actionable> targets = new ArrayList<Actionable>();

            if (jobPage) {
                targets.add(getContext().get(Run.class).getParent());
            }

            if (buildPage) {
                targets.add(getContext().get(Run.class));
            }

            for (Actionable a : targets) {
                List<LinkAction> links = a.getActions(LinkAction.class);
                for (LinkAction link : links) {
                    if (urlRegex == null || link.getUnprotectedUrlName().matches(urlRegex)) {
                        if (displayNameRegex == null || link.getDisplayName().matches(displayNameRegex)) {
                            if (iconFileNameRegex == null || link.getIconFileName().matches(iconFileNameRegex)) {
                                links.remove(link); // remove while iterating, ok?
                            }
                        }
                    }
                }
            }

            getContext().onSuccess(Result.SUCCESS);
            return true; // completed synchronously
        }

        @Override
        public void stop(@Nonnull Throwable throwable) throws Exception {

        }
    }
}
