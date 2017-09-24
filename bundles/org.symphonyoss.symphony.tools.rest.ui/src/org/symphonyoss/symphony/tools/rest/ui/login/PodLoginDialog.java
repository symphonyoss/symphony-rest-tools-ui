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

package org.symphonyoss.symphony.tools.rest.ui.login;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.symphonyoss.symphony.tools.rest.Srt;
import org.symphonyoss.symphony.tools.rest.model.IPod;
import org.symphonyoss.symphony.tools.rest.model.PrincipalConfigBuilder;
import org.symphonyoss.symphony.tools.rest.util.Console;

public class PodLoginDialog extends Dialog
{
  private final IPod    pod_;
  private final Console console_;
  private final String  html_;

  private boolean       running_ = true;
  private String        skey_;
  private String        kmsession_;
  private String        podUrl_;
  private String        kmUrl_;
  private Label         skeyWidget_;
  private Label         kmsessionWidget_;
  
  public PodLoginDialog(Shell shell, IPod pod, Console console, String html)
  {
    super(shell);
    pod_ = pod;
    console_ = console;
    html_ = html;
    
    kmUrl_ = podUrl_ = pod_.getPodConfig().getPodUrl();
    
    try
    {
      URL kmUrl = new URL(pod_.getPodConfig().getKeyManagerUrl());
      
      kmUrl_ = kmUrl.getProtocol() + "://" + kmUrl.getHost();
    }
    catch (MalformedURLException e)
    {
      MessageDialog.openError(shell,
          "Invalid Key Manager URL",
          "Key manager URL \"" + pod_.getPodConfig().getKeyManagerUrl() + "\" is invalid\n\n" +
              e
          );
    }
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    Composite container = (Composite) super.createDialogArea(parent);
    
    GridLayout layout = new GridLayout(2, false);
    layout.marginRight = 0;
    layout.marginLeft = 0;
    layout.marginTop = 0;
    layout.marginBottom = 0;
    container.setLayout(layout);
//    container.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    
    Browser browser = new Browser(container, SWT.NONE);
    browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
    
    Label skeyLabel = new Label(container, SWT.NONE);
    
    skeyLabel.setText("skey");
//    skeyLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_YELLOW));
    
    skeyWidget_ = new Label(container, SWT.NONE);
    skeyWidget_.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    skeyWidget_.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
    
    Label kmsessionLabel = new Label(container, SWT.NONE);
    
    kmsessionLabel.setText("kmsession");
//    kmsessionLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_YELLOW));
    
    kmsessionWidget_ = new Label(container, SWT.NONE);
    kmsessionWidget_.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    kmsessionWidget_.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
    
    Browser.clearSessions();
    
    browser.addProgressListener(new ProgressListener()
    {
      
      @Override
      public void completed(ProgressEvent event)
      {
        System.err.println("Progress completed " + event);
      }
      
      @Override
      public void changed(ProgressEvent event)
      {
        System.err.println("Progress changed " + event);
      }
    });
    
    browser.addLocationListener(new LocationListener()
    {
      @Override
      public void changing(LocationEvent event)
      {
        System.err.println("Changing location " + event.location);

        if(event.location.contains("file://"))
        {
          String newLocation = event.location.replaceAll("file://", podUrl_);
        
          System.err.println("I changed it to " + newLocation);
          
          event.doit = false;
          
          browser.getDisplay().asyncExec(() -> browser.setUrl(newLocation));
        }
      }
      
      @Override
      public void changed(LocationEvent event)
      {
        System.err.println("Changed location " + event.location);
                
        System.err.println("HTML------------------------\n" + browser.getText());
        System.err.println("HTML------------------------\n\n\n");

      }
    });

    browser.setText(html_, true);
    
    Runnable timer = new Runnable()
    {
      @Override
      public void run()
      {
        skey_ = Browser.getCookie(Srt.SKEY, podUrl_);
        kmsession_ = Browser.getCookie(Srt.KMSESSION, kmUrl_);
        System.err.println("time skey=" + skey_);
        System.err.println("time kmSession_=" + kmsession_);
        
        if(running_)
        {
          if(skey_ != null)
          {
            skeyWidget_.setText(skey_);
            skeyWidget_.redraw();
          }
          
          if(kmsession_ != null)
          {
            kmsessionWidget_.setText(kmsession_);
            kmsessionWidget_.redraw();
          }
          
          if(skey_ != null && kmsession_ != null)
          {
            cancelPressed();
          }
          else
          {
            container.getDisplay().timerExec(1000, this);
          }
        }
      }
    };
    
    container.getDisplay().timerExec(1000, timer);

    return container;
  }
  
  @Override
  protected void cancelPressed()
  {
    running_ = false;
    if(skey_ != null || kmsession_ != null)
      pod_.addPrincipal(console_, skey_, kmsession_);
    
    super.cancelPressed();
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent)
  {
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  // overriding this methods allows you to set the
  // title of the custom dialog
  @Override
  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText(pod_.getName() + " Login");
  }

  @Override
  protected Point getInitialSize()
  {
    return new Point(650, 600);
  }

  public String getSkey()
  {
    return skey_;
  }

  public String getKmsession()
  {
    return kmsession_;
  }

}