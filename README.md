[![FINOS - Archived](https://github.com/finos/contrib-toolbox/raw/master/images/badge-archived.png)](https://finosfoundation.atlassian.net/wiki/spaces/FINOS/pages/75530367/Archived)

# This Repo is obsolete and will be deleted soon

The Eclipse based UI for Symphony REST Tools has moved into the main repo at https://github.com/symphonyoss/symphony-rest-tools

























# symphony-rest-tools-ui
An Eclipse based UI for symphony-rest-tools

This project has only just been started and there is not a lot of functionality yet, but I hope it will grow quickly. The idea is that this UI will expose all of the functionality of https://github.com/symphonyoss/symphony-rest-tools and more.

## Building
Until we get to the point where we can do a release build you need to build the pre-requisits and install them into your local maven repo.

```
$ mkdir /tmp/srt
$ cd /tmp/srt
$ git clone https://github.com/SymphonyOSF/S2-super-pom.git
$ git clone https://github.com/symphonyoss/symphony-rest-tools.git
$ git clone https://github.com/symphonyoss/symphony-rest-tools-ui.git
$ cd S2-super-pom/
$ mvn clean install
$ cd ../symphony-rest-tools
$ mvn clean install
$ cd ../symphony-rest-tools-ui
$ mvn clean install
```

The result of which should be something like

```
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO] 
[INFO] symphony-rest-tools-ui ............................. SUCCESS [  0.118 s]
[INFO] bundles ............................................ SUCCESS [  0.005 s]
[INFO] org.symphonyoss.symphony.tools.rest.ui.dependencies  SUCCESS [  0.958 s]
[INFO] org.symphonyoss.symphony.tools.rest.ui.console ..... SUCCESS [  0.716 s]
[INFO] org.symphonyoss.symphony.tools.rest.ui ............. SUCCESS [  0.209 s]
[INFO] features ........................................... SUCCESS [  0.005 s]
[INFO] org.symphonyoss.symphony.tools.rest.ui.feature ..... SUCCESS [  0.080 s]
[INFO] org.symphonyoss.symphony.tools.rest.ui.console.feature SUCCESS [  0.059 s]
[INFO] products ........................................... SUCCESS [  0.006 s]
[INFO] update ............................................. SUCCESS [  1.562 s]
[INFO] org.symphonyoss.symphony.tools.rest.ui.product ..... SUCCESS [ 18.625 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 29.069 s
[INFO] Finished at: 2017-09-17T16:27:19-07:00
[INFO] Final Memory: 105M/1490M
[INFO] ------------------------------------------------------------------------
$ 
```

At this point a cross platform build for Windows, OSX and Linux has been done

```
$ tree -L 5 products/org.symphonyoss.symphony.tools.rest.ui.product/target/products
products/org.symphonyoss.symphony.tools.rest.ui.product/target/products
├── srt
│   ├── linux
│   │   └── gtk
│   │       ├── x86
│   │       │   ├── artifacts.xml
│   │       │   ├── configuration
│   │       │   ├── eclipse
│   │       │   ├── eclipse.ini
│   │       │   ├── features
│   │       │   ├── p2
│   │       │   └── plugins
│   │       └── x86_64
│   │           ├── artifacts.xml
│   │           ├── configuration
│   │           ├── eclipse
│   │           ├── eclipse.ini
│   │           ├── features
│   │           ├── p2
│   │           └── plugins
│   ├── macosx
│   │   └── cocoa
│   │       └── x86_64
│   │           └── Eclipse.app
│   └── win32
│       └── win32
│           ├── x86
│           │   ├── artifacts.xml
│           │   ├── configuration
│           │   ├── eclipse.exe
│           │   ├── eclipse.ini
│           │   ├── eclipsec.exe
│           │   ├── features
│           │   ├── p2
│           │   └── plugins
│           └── x86_64
│               ├── artifacts.xml
│               ├── configuration
│               ├── eclipse.exe
│               ├── eclipse.ini
│               ├── eclipsec.exe
│               ├── features
│               ├── p2
│               └── plugins
├── srt-linux.gtk.x86.zip
├── srt-linux.gtk.x86_64.zip
├── srt-macosx.cocoa.x86_64.zip
├── srt-win32.win32.x86.zip
└── srt-win32.win32.x86_64.zip
```
The zip files are distribution archives and there are exploded copies which you can execute directly.

On OSX if you navigate to products/org.symphonyoss.symphony.tools.rest.ui.product/target/products/srt/macosx/cocoa/x86_64/Eclipse.app in Finder and launch that app the UI should come up and look like this:

![image](https://user-images.githubusercontent.com/14877967/30525963-93ea99d8-9bc6-11e7-99f6-b628e1fc2af5.png)

Obviously there isn't a lot here yet, but try right clicking on the Pods view and then watch the Console view (you can type into this view to answer prompts)

![image](https://user-images.githubusercontent.com/14877967/30525991-0e0ba996-9bc7-11e7-977e-1028625643be.png)
