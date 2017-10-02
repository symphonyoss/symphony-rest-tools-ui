/*
 *
 *
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.symphonyoss.symphony.tools.rest.ui.pods;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.symphonyoss.symphony.jcurl.JCurl;
import org.symphonyoss.symphony.tools.rest.model.IModelListener;
import org.symphonyoss.symphony.tools.rest.model.IModelObject;
import org.symphonyoss.symphony.tools.rest.model.IPodManager;
import org.symphonyoss.symphony.tools.rest.model.IUrlEndpoint;
import org.symphonyoss.symphony.tools.rest.ui.ModelObjectContentProvider;
import org.symphonyoss.symphony.tools.rest.ui.ModelObjectLabelProvider;
import org.symphonyoss.symphony.tools.rest.ui.ModelObjectStatusImageAndLabelProvider;
import org.symphonyoss.symphony.tools.rest.ui.ModelObjectTypeImageAndLabelProvider;
import org.symphonyoss.symphony.tools.rest.ui.browser.IBrowserManager;
import org.symphonyoss.symphony.tools.rest.util.home.ISrtHome;

public class PodsView extends ModelObjectView
{
//  @Inject
//  private IConsoleManager consoleManager_;
  @Inject
  private ISrtHome        srtHome_;
  @Inject
  private IBrowserManager browserManager_;
  
  @PostConstruct
  public void createControls(Composite parent, EMenuService menuService, ESelectionService selectionService)
  {
    // more code...
    TreeViewer viewer = new TreeViewer(parent, SWT.MULTI);

    viewer.setContentProvider(new ModelObjectContentProvider());
   
    
    viewer.getTree().setHeaderVisible(true);
    Display display = viewer.getControl().getDisplay();

    TreeViewerColumn mainColumn = new TreeViewerColumn(viewer, SWT.NONE);
    mainColumn.getColumn().setText("Name");
    mainColumn.getColumn().setWidth(300);
    mainColumn.setLabelProvider(
            new ModelObjectTypeImageAndLabelProvider(display,
                (o) -> o.getName()));
    
    TreeViewerColumn typeColumn = new TreeViewerColumn(viewer, SWT.NONE);
    typeColumn.getColumn().setText("Type");
    typeColumn.getColumn().setWidth(100);
    typeColumn.setLabelProvider(
            new ModelObjectLabelProvider<IModelObject>(display,
                IModelObject.class,
                (o) -> o.getTypeName()));
    
    TreeViewerColumn statusColumn = new TreeViewerColumn(viewer, SWT.NONE);
    statusColumn.getColumn().setText("Status");
    statusColumn.getColumn().setWidth(100);
    statusColumn.setLabelProvider(
            new ModelObjectStatusImageAndLabelProvider(display));
    
    TreeViewerColumn statusMessageColumn = new TreeViewerColumn(viewer, SWT.NONE);
    statusMessageColumn.getColumn().setText("Status Message");
    statusMessageColumn.getColumn().setWidth(200);
    statusMessageColumn.setLabelProvider(
            new ModelObjectLabelProvider<IModelObject>(display,
                IModelObject.class,
                (o) -> o.getComponentStatusMessage()));
    
    TreeViewerColumn urlColumn = new TreeViewerColumn(viewer, SWT.NONE);
    urlColumn.getColumn().setText("URL");
    urlColumn.getColumn().setWidth(300);
    urlColumn.setLabelProvider(
            new ModelObjectLabelProvider<IUrlEndpoint>(display,
                IUrlEndpoint.class,
                (o) -> o.getUrl()));
    
    // register context menu on the table
    menuService.registerContextMenu(viewer.getControl(), "org.symphonyoss.symphony.tools.rest.ui.popupmenu.pods");
    
    viewer.addDoubleClickListener(new IDoubleClickListener()
    {
      
      @Override
      public void doubleClick(DoubleClickEvent event)
      {
        final IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        if (selection == null || selection.isEmpty())
          return;

        Iterator<?> it = selection.iterator();
        
        while(it.hasNext())
        {
          Object obj = it.next();
          
          if(obj instanceof IUrlEndpoint)
          {
            URL url = ((IUrlEndpoint) obj).getUrl();
            
            boolean planB = true;
            
            if(url == null)
              MessageDialog.openError(viewer.getControl().getShell(), "Not a URL", "The selected object does not have a valid URL");
            else if(planB)
            {
              try 
              {
                JCurl jCurl = JCurl.builder().build();
                HttpURLConnection connection = jCurl.connect(url);
                
                
            
                if(connection.getResponseCode() != 200)
                {
                  MessageDialog.openError(viewer.getControl().getShell(),
                      "Failed to connect",
                      "Error " + connection.getResponseCode()
                      );
  
                  return;
                }
                
                try(
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                            connection.getInputStream())))
                {
                  StringBuilder s = new StringBuilder();
                  String line;
                  
                  while((line = reader.readLine()) != null)
                  {
                    line = line.replaceAll("href=\"", "href=\"" + url + "/")
                        .replaceAll("<script src=\"app-", "<script src=\"" + url + "/app-")
                        .replaceAll("\"./browsers.html\"", "\"" + url + "/browsers.html\"")
                        .replaceAll("'/login'", "'" + url + "/login'")
                        .replaceAll("var chrome32 = \\(chromeVer >= 32\\);", "var chrome32 = true;")
                        ;
                    System.err.println(line);
                    s.append(line);
                    s.append("\n");
                  }
                  String html = "<html>\n" + 
                      "  <head>\n" + 
                      "    <meta charset=\"utf-8\" />\n" + 
                      "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />\n" + 
                      "    <link type=\"text/css\" href=\"https://qa4.symphony.com/app-41f81485e5.css\" rel=\"stylesheet\" />\n" + 
                      "    <link rel=\"icon\" href=\"https://qa4.symphony.com/favicon.ico?v=1.1\">\n" + 
                      "    <script type=\"text/javascript\">\n"
                      + "window.navigator.userAgent='BRUCE Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.91 Safari/537.36';\n"
                      + "alert('window.navigator.userAgent=' + window.navigator.userAgent);\n" + 
                      "      var isIE11 = !!window.navigator.userAgent.match(/Trident.*rv[ :]*11\\./);\n" + 
                      "\n" + 
                      "      var chromeVer = -1;\n" + 
                      "      var chrome = window.navigator.userAgent.match(/(Chrome|CriOS)\\/(\\d+)\\./);\n" + 
                      "      if (chrome && chrome.length == 3) {\n" + 
                      "        chromeVer = parseInt(chrome[2],10);\n" + 
                      "      }\n" + 
                      "\n" + 
                      "      var ua = window.navigator.userAgent;\n" + 
                      "\n" + 
                      "      var iPhone = /iphone/i.test(ua);\n" + 
                      "      var iPad = /ipad/i.test(ua);\n" + 
                      "      var wp = /Windows Phone/i.test(ua);\n" + 
                      "      var android = /android/i.test(ua);\n" + 
                      "      var bb = /BlackBerry/i.test(ua);\n" + 
                      "      var bb10 = /BB10/i.test(ua);\n" + 
                      "\n" + 
                      "      var supportsWebWorker = (function() {\n" + 
                      "          //\n" + 
                      "          // to support inline images, web workers are used to decrypt images.\n" + 
                      "          // two key features are needed:\n" + 
                      "          // 1. webworkify creates worker using object url\n" + 
                      "          // 2. transferable support needed to move large file data between\n" + 
                      "          //    main thread to worker\n" + 
                      "          // older versions of IE do not support these features (e.g., 11.0.9600.17031).\n" + 
                      "          // it is known that IE ver 11.0.9600.18350 or higher does support\n" + 
                      "          // this feature set. in between versions of IE11 might supports these\n" + 
                      "          // features but it is not possible install and check.\n" + 
                      "          // also IE11 user agent does not give full vesion, so it is not\n" + 
                      "          // possible to use that method to filter out browsers,\n" + 
                      "          // so instead relying on feature detection below.\n" + 
                      "          //\n" + 
                      "          var worker;\n" + 
                      "          try {\n" + 
                      "              var URL = window.URL || window.webkitURL || window.mozURL || window.msURL;\n" + 
                      "              var blob = new Blob([], { type: 'text/javascript' });\n" + 
                      "              var workerUrl = URL.createObjectURL(blob);\n" + 
                      "              worker = new Worker(workerUrl);\n" + 
                      "          } catch(e) {\n" + 
                      "              return false;\n" + 
                      "          }\n" + 
                      "\n" + 
                      "          var ab = new ArrayBuffer(1);\n" + 
                      "          try {\n" + 
                      "              worker.postMessage(ab, [ab]);\n" + 
                      "              if (ab.byteLength) {\n" + 
                      "                  return false;\n" + 
                      "              } else {\n" + 
                      "                  return true;\n" + 
                      "              }\n" + 
                      "            } catch(e) {\n" + 
                      "                return false;\n" + 
                      "            }\n" + 
                      "      }());\n" + 
                      "\n" + 
                      "      // safari on osx\n" + 
                      "      var safariMajorVersion = -1;\n" + 
                      "      var safariUA = window.navigator.userAgent.match(/Version\\/(\\d+)(?:\\.\\d+)* Safari/);\n" + 
                      "      if (safariUA && safariUA.length == 2) {\n" + 
                      "          safariMajorVersion = parseInt(safariUA[1], 10);\n" + 
                      "      }\n" + 
                      "\n" + 
                      "      if (wp || bb || bb10) {\n" + 
                      "          window.location = \"./mobile.html\"\n" + 
                      "      }\n" + 
                      "\n" + 
                      "      alert('chromeVer=' + chromeVer);"
                      + "var chrome32 = (chromeVer >= 32);\n" + 
                      "      var safari8 = (safariMajorVersion >= 8);\n" + 
                      "\n" + 
                      "      // desktop client browsers must support web worker features\n" + 
                      "      if (!supportsWebWorker && (isIE11 || chrome32 || safari8)) {\n" + 
                      "          window.location = \"https://qa4.symphony.com/browsers.html\"\n" + 
                      "      }\n" + 
                      "\n" + 
                      "      if (!(isIE11 || chrome32 || iPhone || iPad || wp || bb || bb10 || safari8)) {\n" + 
                      "        window.location = \"https://qa4.symphony.com/browsers.html\"\n" + 
                      "      }\n" + 
                      "\n" + 
                      "      window.env = {\n" + 
                      "          LOGIN_URL: 'https://qa4.symphony.com/login',\n" + 
                      "          PUBLIC_POD: false,\n" + 
                      "          iOS: iPhone || iPad,\n" + 
                      "          android: android,\n" + 
                      "          MOBILE_SUPPORTED: true,\n" + 
                      "          // This app id is also hardcoded into the iOs landing page \"iOS.html\"\n" + 
                      "          iOSAppId: 1098963705\n" + 
                      "      };\n" + 
                      "    </script>\n" + 
                      "    <script src=\"https://qa4.symphony.com/app-99b7022635.js\"></script>\n" + 
                      "    <title>Symphony | Secure Seamless Communication</title>\n" + 
                      "  </head>\n" + 
                      "  <body>\n" + 
                      "    <header>\n" + 
                      "      <div id=\"logo\"></div>\n" + 
                      "    </header>\n" + 
                      "  </body>\n" + 
                      "</html>";
                  
                  html = s.toString();
                  
                  browserManager_.createBrowser(url, html);
                }
              }
              catch(IOException e)
              {
                MessageDialog.openError(viewer.getControl().getShell(),
                    "Failed to connect",
                    "Error " + e
                    );
              } 
            }
            else
            {
              browserManager_.createBrowser(url, null);
            }
          }
        }
      }
    });
    
    viewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      @Override
      public void selectionChanged(SelectionChangedEvent event)
      {
        IStructuredSelection selection = viewer.getStructuredSelection();
        selectionService.setSelection(selection.getFirstElement());
      }
    });
    
    viewer.setInput(srtHome_.getPodManager());
    ColumnViewerToolTipSupport.enableFor(viewer);
    
    IPodManager podManager = srtHome_.getPodManager();
    
    podManager.addListener(new IModelListener()
    {
      
      @Override
      public void modelObjectChanged(IModelObject modelObject)
      {
        display.asyncExec(() -> viewer.update(modelObject, null));
      }

      @Override
      public void modelObjectStructureChanged(IModelObject modelObject)
      {
        if(modelObject == podManager)
          display.asyncExec(() -> viewer.refresh());
        else
          display.asyncExec(() -> viewer.refresh(modelObject));
      }
    });
    
    podManager.loadAll();
  }
  
}
