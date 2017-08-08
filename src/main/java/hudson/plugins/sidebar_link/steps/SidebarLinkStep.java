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
import java.util.Set;

/**
 * Adds a Sidebar link to the job page
 *
 * @author Alex Johnson
 */
public class SidebarLinkStep extends Step {

    private String urlName;
    private String displayName;
    private String iconFileName = "clipboard.png"; // default image

    @DataBoundConstructor
    public SidebarLinkStep (String urlName) {
        this.urlName = urlName;
    }

    @DataBoundSetter
    public void setDisplayName (String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName () {
        return displayName;
    }

    @DataBoundSetter
    public void setIconFileName (String iconFileName) {
        this.iconFileName = iconFileName;
    }

    public String getIconFileName () {
        return iconFileName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new ExecutionImpl(context, urlName, displayName, iconFileName);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public Set<Class<?>> getRequiredContext() {
            return ImmutableSet.of(Run.class, FilePath.class);
        }

        @Override
        public String getFunctionName() {
            return "sidebarLink";
        }

        @Override
        public String getDisplayName() {
            return "Sidebar Link";
        }
    }

    public static class ExecutionImpl extends StepExecution {

        private String urlName;
        private String displayName;
        private String iconFileName;

        public ExecutionImpl (StepContext context, String urlName, String displayName, String iconFileName) {
            super(context);
            this.urlName = urlName;
            this.displayName = displayName;
            this.iconFileName = iconFileName;
            if (displayName == null) {
                this.displayName = urlName;
            }
        }

        @Override
        public boolean start() throws Exception {
            getContext().get(Run.class).getParent().addAction(new LinkAction(urlName, displayName, iconFileName));
            getContext().onSuccess(Result.SUCCESS);
            return true; // completed synchronously
        }

        @Override
        public void stop(@Nonnull Throwable throwable) throws Exception {

        }
    }
}
