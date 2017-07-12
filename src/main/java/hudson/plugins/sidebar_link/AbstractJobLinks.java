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
import hudson.model.*;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Sidebar links for a Job
 *
 * @author Alex Johnson
 */
public abstract class AbstractJobLinks<T extends Job<?, ?>> extends JobProperty<T> {

    private List<LinkAction> links = new ArrayList<LinkAction>();

    public AbstractJobLinks(List<LinkAction> links) {
        this.links = links != null ? links : new ArrayList<LinkAction>();
    }

    @Deprecated
    public void arbitraryMethod(){}

    public List<LinkAction> getLinks() { return links; }

    @Override
    public Collection<? extends Action> getJobActions(T job) {
        return Collections.EMPTY_SET;
    }

    @Extension
    public static class DescriptorImpl extends JobPropertyDescriptor {
        @Override
        public String getDisplayName() {
            return "Sidebar Links";
        }

        @Override
        public AbstractJobLinks newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return formData.has("sidebar-links")
                    ? req.bindJSON(ProjectLinks.class, formData.getJSONObject("sidebar-links"))
                    : null;
        }
    }

}
