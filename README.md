JSLint for Jenkins
==================

A Jenkins plugin which bundles JSLint and Rhino and lints your code. [Official plugin page](https://wiki.jenkins-ci.org/display/JENKINS/JSLint+plugin).

Provided by [Box UK](http://www.boxuk.com/) - see our [Labs Page](http://www.boxuk.com/labs/jslint-jenkins-plugin)
for more info on this plugin!

For more on why we believe in coding standards, see [Rhodri Pugh's post on coding standards](http://www.boxuk.com/blog/coding-standards)

The version of JSLint is modified to run in a "pure Java" version of Rhino, and to be less exacting than
the "main" version of JSLint, which can be found [here](https://github.com/douglascrockford/JSLint/). JSLint
is the work of the wonderful [Douglas Crockford](http://www.crockford.com/). This plugin exposes no options
for JSLint, please feel free to fork the project and contribute!

Using this plugin
-----------------

Install this plugin on your Jenkins (url will be something like http://{jenkins}/pluginManager/advanced) and
restart Jenkins.

There are no global configuration options for this plugin, it's all done per-job.

Then, go to the Jenkins job you want to lint. All you need to do is click "Add build step" and select "JSLint". Then,
set the files you want to include/exclude. These are standard Apache Ant filters and if you need help just click the
question mark icon for some tips.

The next thing to set is the output file. This is in the checkstyle XML format so we recommend using it with the
[Jenkins CheckStyle plugin](https://wiki.jenkins-ci.org/display/JENKINS/Checkstyle+Plugin). This will integrate
the JSLint output into your build output screen. Pretty handy!

We recommend clicking "Advanced" and setting the status thresholds to "0" so any jslint failure will fail the build.

You'll probably find that if you use library "foo" you get a lot of "undefined foo" errors at first. Just put a
globals statement at the top of your JavaScript file so jslint knows that "foo" is a global:

    /*globals foo, bar*/
    var baz = function() {
        foo.doSomethingWith(bar);
    };

Please note: you must NOT have a space between "*" and "globals", otherwise it won't work

Deploying via Maven
-------------------

You need permissions to do this. This serves as an aide memoire for the maintainer!

First, commit everything that you've done. Then, let Maven manage it all.

    mvn release:clean
    mvn release:prepare
    mvn release:perform
    mvn deploy

Make sure the Jenkins Github repo is up to date with the Box UK one.

[Here are the instructions for Jenkins hosted plugins](https://wiki.jenkins-ci.org/display/JENKINS/Hosting+Plugins). Make sure you have set up ~/.m2/settings.xml with your credentials as per the instructions.

The package should show up in http://repo.jenkins-ci.org/releases/org/jenkins-ci/plugins/jslint/

http://mirrors.karan.org/jenkins/updates/update-center.json Takes about 6 hours to update so check back there later!



License
-------

MIT LICENSE

Copyright (c) 2012 Box UK

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
