package com.boxuk.jenkins.jslint;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.remoting.Callable;
import java.io.FileReader;
import java.net.URLDecoder;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import jenkins.model.Jenkins;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Class that actually runs the linting. Because it is callable, it is safe
 * to run across slave nodes.
 *
 * @author Gavin Davies <gavin.davies@boxuk.com>
 * @license MIT License http://www.opensource.org/licenses/MIT
 */
public class LintRunner implements Callable<Properties, RuntimeException> {

    /**
     * Build logger used to record what's happened.
     */
    private final BuildListener listener;

    /**
     * Collection of arguments to pass to JSLint.
     */
    private final List<String> args  = new ArrayList<String>();

    /**
     * Create a new LintRunner.
     *
     * @param build The build
     * @param listener Build listener
     * @param includePattern Apache ant pattern
     * @param excludePattern Apache ant pattern
     * @param logfile Where the XML is output to
     * @throws IOException Can be thrown if can't read files
     * @throws InterruptedException Can be thrown if build interrupted
     */
    public LintRunner(
        final AbstractBuild build,
        final BuildListener listener,
        final String includePattern,
        final String excludePattern,
        final String logfile,
        final String arguments
    ) throws IOException, InterruptedException {
        this.listener = listener;

        final FilePath workspaceDir = build.getWorkspace();

        args.add("-DxmlOutput=" + workspaceDir.toString() + "/" + logfile);
        if(arguments != null && arguments.length() > 1) {
            args.add(arguments);
        }

        FilePath[] files = workspaceDir.list(
            includePattern,
            excludePattern
        );

        for (int i = 0; i < files.length; i++) {
            args.add(files[i].toString());
        }
    }

    /**
     * Call this method.
     *
     * @return Some properties. No bearing on anything really, it's just
     * needed by the interface
     */
    public Properties call() {
        listener.getLogger().println("[JSLint] calling jslint with args"
            + args.toString());

        // Create and enters a Context. The Context stores information
        // about the execution environment of a script.
        Context context = Context.enter();

        try {
            // Initialize the standard objects (Object, Function, etc.)
            // This must be done before scripts can be executed. Returns
            // a scope object that we use in later calls.
            ScriptableObject scope = context.initStandardObjects();
            Scriptable argsObj = context.newArray(scope, args.toArray());
            scope.defineProperty(
                "arguments",
                argsObj,
                ScriptableObject.DONTENUM
            );

            URL res = this.getClass().getResource("JSLintBuilder/jslint.js");
            if(res == null) {
                this.getClass().getResource("JSLint/jslint.js");
            }

            String file;
            try {
                file = URLDecoder.decode(res.toString(), "UTF-8");
            } catch(java.io.UnsupportedEncodingException ex) {
                listener.getLogger().println("[JSLint] Unable to decode using UTF-8: " + ex.toString());
                file = URLDecoder.decode(res.toString());
            }
            // Breaks on Windows without this
            // BUT!! breaks on EVERYTHING with it since circa Jenkins 1.519
            // file = file.replaceAll("file:", "");
            listener.getLogger().println("[JSLint] JSLint path is " + file);

            try {
                URL url = new URL(file);
                InputStream inputStream = url.openStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                context.evaluateReader(
                    scope,
                    inputStreamReader,
                    "jslint.js",
                    0,
                    null
                );
            } catch (IOException e) {
                e.printStackTrace();
            }

        } finally {
            // Exit from the context.
            Context.exit();
        }

        return System.getProperties();
    }
}
