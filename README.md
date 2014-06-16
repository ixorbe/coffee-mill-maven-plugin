coffee-mill-maven-plugin
=========================

**coffee-mill-maven-plugin** is a Maven plugin focusing on modern web development and especially on the client side
technologies. A lot of technologies have emerged to make client-side development a bit easier.

Why using Apache Maven for such kind of code ? Because such development can also benefit from _modularity_,
 _testing_ and from a somewhat strict build process. Actually, they definitely requires it to keep project
 development on rails.

The goal of the project is to cover the complete lifecycle of such artifacts : compilation, validation, tests,
aggregation, minification, reporting... Everything required to deliver reusable and robust artifacts.

Features
--------

* Build aggregated and minified JavaScript components
* Make your component more robust with unit and integration tests using Jasmine
* Build modular JavaScript application by depending on your other components
* Use [CoffeeScript](http://coffeescript.org "CoffeeScript") or [TypeScript](http://www.typescriptlang.org "TypeScript") without thinking about compilation
* Compile [Dust](http://akdubya.github.io/dustjs/ "Dust") templates for you
* Validate your CSS files, compile LESS files
* Optimize your JPEG and PNG files using [jpegtran](http://jpegclub.org/jpegtran/ "jpegtran") and [OptiPNG](http://optipng.sourceforge.net "OptiPNG")
* Don't worry about aggregation and minification, the plugin manages that for you
* Don't launch [Maven](http://maven.apache.org "Apache Maven") continuously, the _watch_ mode monitor your files and process them

Documentation
-------------

The documentation is available on:

* Last stable version : http://nanoko-project.github.com/coffee-mill-maven-plugin/maven/release/
* Development version : http://nanoko-project.github.com/coffee-mill-maven-plugin/maven/snapshot/

License
-------
This plugin is licensed under the Apache License 2.0. The project is part of the _nanoko_ initiative.

Issues Tracker
--------------
Follow project issues on our JIRA instance : https://ubidreams.atlassian.net/browse/NANOKO/component/10101
