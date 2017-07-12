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
package hudson.plugins.sidebar_link;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.JobPropertyDescriptor;
import jenkins.model.TransientActionFactory;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Alex Johnson
 */
public class JobLinks extends AbstractJobLinks<Job<?, ?>> {

    @DataBoundConstructor
    public JobLinks(List<LinkAction> links) {
        super(links);
    }

    @Override
    public JobPropertyDescriptor getDescriptor () {
        return new AbstractJobLinks.DescriptorImpl();
    }

    @Extension(optional = true)
    public static class TransientActionFactoryImpl extends TransientActionFactory<Job> {
        @Override
        public Class<Job> type() {
            return Job.class;
        }

        public Collection<LinkAction> createFor(Job job) {
            JobLinks links = (JobLinks) job.getProperty(JobLinks.class);
            if (links == null) {
                return Collections.emptyList();
            }
            return Collections.unmodifiableList(links.getLinks());
        }
    }

}
