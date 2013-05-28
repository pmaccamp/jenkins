package hudson.node_monitors;

import hudson.model.Computer;
import hudson.node_monitors.DiskSpaceMonitorDescriptor.DiskSpace;
import hudson.slaves.DiskSpaceMonitorNodeProperty;
import hudson.slaves.NodeProperty;
import org.kohsuke.stapler.DataBoundConstructor;

import java.text.ParseException;
import java.util.logging.Logger;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class AbstractDiskSpaceMonitor extends NodeMonitor {

    /**
     * The free space threshold, below which the node monitor will be triggered.
     * This is a human readable string representation as entered by the user, so
     * that we can retain the original notation.
     */
    public final String masterFreeSpaceThreshold;

    @DataBoundConstructor
    public AbstractDiskSpaceMonitor(String threshold) throws ParseException {
        this.masterFreeSpaceThreshold = threshold;
        DiskSpace.parse(threshold); // make sure it parses
    }

    public AbstractDiskSpaceMonitor() {
        this.masterFreeSpaceThreshold = "1GB";
    }

    public long getThresholdBytes(Computer c) {
        String freeSpaceThreshold = masterFreeSpaceThreshold;
        // If a slave has defined its own threshold, use it in place of defaultFreeSpaceThreshold
        for (NodeProperty<?> nodeFreeSpaceThreshold : c.getNode().getNodeProperties()) {
            if (nodeFreeSpaceThreshold instanceof DiskSpaceMonitorNodeProperty) {
                freeSpaceThreshold = ((DiskSpaceMonitorNodeProperty) nodeFreeSpaceThreshold).getNodeFreeSpaceThreshold();
            }
        }
        if (freeSpaceThreshold == null) {
            return DEFAULT_THRESHOLD; // backward compatibility with the data format that didn't have 'freeSpaceThreshold'
        }
        try {
            return DiskSpace.parse(freeSpaceThreshold).size;
        } catch (ParseException e) {
            return DEFAULT_THRESHOLD;
        }
    }

    @Override
    public Object data(Computer c) {
        DiskSpace size = (DiskSpace) super.data(c);
        if (size != null && size.size < getThresholdBytes(c)) {
            size.setTriggered(this.getClass(), true);
            if (getDescriptor().markOffline(c, size)) {
                LOGGER.warning(Messages.DiskSpaceMonitor_MarkedOffline(c.getName()));
            }
        }
        if (size != null && size.size > getThresholdBytes(c) && c.isOffline() && c.getOfflineCause() instanceof DiskSpace) {
            if (this.getClass().equals(((DiskSpace) c.getOfflineCause()).getTrigger())) {
                if (getDescriptor().markOnline(c)) {
                    LOGGER.warning(Messages.DiskSpaceMonitor_MarkedOnline(c.getName()));
                }
            }
        }
        return size;
    }
    private static final Logger LOGGER = Logger.getLogger(AbstractDiskSpaceMonitor.class.getName());
    private static final long DEFAULT_THRESHOLD = 1024L * 1024 * 1024;
}
