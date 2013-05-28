/*
 * The MIT License
 *
 * Copyright 2013 Patrick McKeown.
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
package hudson.slaves;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Node;
import hudson.node_monitors.NodeMonitor;
import hudson.util.DescribableList;
import java.text.ParseException;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 *
 * @author Patrick McKeown
 */
public class DiskSpaceMonitorNodeProperty extends NodeProperty<Node> {

    /**
     * Slave specific free space threshold, below which the node monitor will be
     * triggered. This is a human readable string representation as entered by
     * the user, so that we can retain the original notation.
     */
    private static final DescribableList<NodeMonitor,Descriptor<NodeMonitor>> monitors
            = new DescribableList<NodeMonitor, Descriptor<NodeMonitor>>();
    
    @DataBoundConstructor
    public DiskSpaceMonitorNodeProperty(String nodeFreeSpaceThreshold) throws ParseException {
        this.nodeFreeSpaceThreshold = nodeFreeSpaceThreshold;
    }

    public String getNodeFreeSpaceThreshold() {
        return nodeFreeSpaceThreshold;
    }

    @Extension
    public static class DescriptorImpl extends NodePropertyDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.DiskSpaceMonitorNodeProperty_displayName();
        }
    }
}
