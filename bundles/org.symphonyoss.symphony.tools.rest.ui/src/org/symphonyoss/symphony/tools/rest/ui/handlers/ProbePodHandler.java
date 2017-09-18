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

package org.symphonyoss.symphony.tools.rest.ui.handlers;

import java.io.IOException;

import javax.inject.Inject;

import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.symphonyoss.symphony.tools.rest.probe.ProbePod;
import org.symphonyoss.symphony.tools.rest.ui.console.IConsole;
import org.symphonyoss.symphony.tools.rest.ui.console.IConsoleManager;
import org.symphonyoss.symphony.tools.rest.util.Console;
import org.symphonyoss.symphony.tools.rest.util.home.ISrtHome;
import org.symphonyoss.symphony.tools.rest.util.home.SrtHome;

public class ProbePodHandler
{
  @Inject
  private IConsoleManager consoleManager_;
  
  public ProbePodHandler()
  {
    System.out.println("Construct ProbePodHandler");
  }
  
  @Execute
  public void execute(IWorkbench workbench)
  {
    final IConsole console = consoleManager_.createConsole();
    
    console.getOut().println("Console Probe!");
    
    Job job = Job.create("Probe", (ICoreRunnable) monitor ->
    {
      System.out.println("Probe!");
      
      ISrtHome home = new SrtHome(null, null);
      
      ProbePod  probePod = new ProbePod(null, home);
      
      probePod.setConsole(new Console(console.getIn(), console.getOut(), console.getErr()));
      
      try
      {
        probePod.run();
      }
      catch (IOException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });
    
    job.schedule();
  }
}
