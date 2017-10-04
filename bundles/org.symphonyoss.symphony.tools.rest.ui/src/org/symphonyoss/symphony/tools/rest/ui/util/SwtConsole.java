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

package org.symphonyoss.symphony.tools.rest.ui.util;

import java.io.BufferedReader;
import java.io.PrintWriter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.symphonyoss.symphony.tools.rest.SrtCommand;
import org.symphonyoss.symphony.tools.rest.ui.SrtImageRegistry;
import org.symphonyoss.symphony.tools.rest.util.Console;

public class SwtConsole extends Console
{

  private Shell shell_;
  private IProgressMonitor monitor_;
  private SrtImageRegistry imageRegistry_;

  public SwtConsole(Shell shell, SrtImageRegistry imageRegistry, BufferedReader in, PrintWriter out, PrintWriter err)
  {
    super(in, out, err);
    shell_ = shell;
    imageRegistry_ = imageRegistry;
  }

  @Override
  public void execute(SrtCommand srtCommand)
  {
    shell_.getDisplay().syncExec(() ->
    {
      ConsoleWizard wizard = new ConsoleWizard(shell_, SwtConsole.this, srtCommand);
      
      ConsoleWizardDialog wizardDialog = new ConsoleWizardDialog(shell_,
          wizard);
      
      wizard.setDialog(wizardDialog);
      wizardDialog.open();
    });
  }

  public void setProgressMonitor(IProgressMonitor monitor)
  {
    monitor_ = monitor;
  }

  @Override
  public void beginTask(String name, int totalWork)
  {
    monitor_.beginTask(name, totalWork);
  }

  @Override
  public void done()
  {
    monitor_.done();
  }

  @Override
  public boolean isCanceled()
  {
    return monitor_.isCanceled();
  }

  @Override
  public void setTaskName(String name)
  {
    monitor_.setTaskName(name);
  }

  @Override
  public void subTask(String name)
  {
    monitor_.subTask(name);
  }

  @Override
  public void worked(int work)
  {
    monitor_.worked(work);
  }

  public SrtImageRegistry getImageRegistry()
  {
    return imageRegistry_;
  }

}
