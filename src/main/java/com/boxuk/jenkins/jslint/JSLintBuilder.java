package com.boxuk.jenkins.jslint;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import java.io.IOException;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * A {@link Builder} for JSLint. Takes parameters to determine filesets to
 * include/exclude and creates output in Checkstyle format.
 *
 * <p>
 * When a build is performed, the
 * {@link #perform(AbstractBuild, Launcher, BuildListener)} method will be
 * invoked.
 * </p>
 *
 * @author Gavin Davies <gavin.davies@boxuk.com>
 * @license MIT License http://www.opensource.org/licenses/MIT
 */
public class JSLintBuilder extends Builder {

    /**
     * Ant file-set pattern to define the files to scan.
     */
    private final String includePattern;

    /**
     * Ant file-set pattern to define the files to exclude from scan.
     */
    private final String excludePattern;

    /**
     * Location to log to - e.g. target/jslint.xml.
     */
    private final String logfile;
    
    /**
     * User entered arguments
     */
    private final String arguments;

    /**
     * Fields in config.jelly must match the parameter names in the
     * "DataBoundConstructor".
     *
     * @param includePattern files to include - apache ant filter
     * @param excludePattern files to exclude - apache ant filter
     * @param logfile xml location to log to
     */
    @DataBoundConstructor
    public JSLintBuilder(
        final String includePattern,
        final String excludePattern,
        final String logfile,
        final String arguments
    ) {
        this.includePattern = includePattern;
        this.excludePattern = excludePattern;
        this.logfile = logfile;
        this.arguments = arguments;
    }

    /**
     * @return The include pattern - apache ant style filter
     */
    public final String getIncludePattern() {
        return includePattern;
    }

    /**
     * @return The exclude pattern - apache ant style filter
     */
    public final String getExcludePattern() {
        return excludePattern;
    }

    /**
     * @return Where to put the XML log
     */
    public final String getLogfile() {
        return logfile;
    }
    
    /**
     * @return User arguments
     */
    public final String getArguments() {
        return arguments;
    }

    /**
     * Perform this JSLint operation.
     *
     * @param build The build
     * @param launcher Launcher
     * @param listener Listener
     * @return True if it worked, else false
     * @throws IOException Can be thrown if can't read files
     * @throws InterruptedException Can be thrown if build interrupted
     */
    @Override
    public final boolean perform(
        final AbstractBuild build,
        final Launcher launcher,
        final BuildListener listener
    ) throws InterruptedException, IOException {

        listener.getLogger().println("[JSLint] Ready");

        // Get a "channel" to the build machine and run the task there
        launcher.getChannel().call(
            new LintRunner(
                build,
                listener,
                includePattern,
                excludePattern,
                logfile,
                arguments
            )
        );

        listener.getLogger().println("[JSLint] Complete");

        return true;
    }

    /**
     * Implementation of an extension point. Descriptor for this class.
     *
     * This builder can be used with all kinds of project types.
     */
    @Extension
    public static final class DescriptorImpl
        extends BuildStepDescriptor<Builder> {

        /**
         * @param aClass Project to check whether this plugin can be used with.
         * @return Whether this descriptor is applicable - always true
         */
        public boolean isApplicable(
            final Class<? extends AbstractProject> aClass
        ) {
            return true;
        }

        /**
         * @return This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "JSLint";
        }
    }
}
