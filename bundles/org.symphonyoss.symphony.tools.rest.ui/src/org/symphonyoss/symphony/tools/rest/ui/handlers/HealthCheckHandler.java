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

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.symphonyoss.symphony.tools.rest.model.IPod;
import org.symphonyoss.symphony.tools.rest.probe.CheckPod;
import org.symphonyoss.symphony.tools.rest.ui.console.IConsole;
import org.symphonyoss.symphony.tools.rest.ui.console.IConsoleManager;
import org.symphonyoss.symphony.tools.rest.util.Console;
import org.symphonyoss.symphony.tools.rest.util.home.ISrtHome;

public class HealthCheckHandler
{
  @Inject
  private IConsoleManager consoleManager_;
  @Inject
  private ISrtHome        srtHome_;
  
  @Execute
  public void execute(IWorkbench workbench, @Named(IServiceConstants.ACTIVE_SELECTION)
  @Optional Object selection)
  {
    final IConsole console = consoleManager_.createConsole();
    
    console.getOut().println("HealthCheckHandler!");
    
    if (selection!=null && selection instanceof IPod)
    {
      String name = ((IPod)selection).getName();
      
      console.getOut().println("HealthCheck " + name);
    
      Job job = Job.create("HealthCheck " + name, (ICoreRunnable) monitor ->
      {
        Console srtConsole = new Console(console.getIn(), console.getOut(), console.getErr());
        
        CheckPod  probePod = new CheckPod(srtConsole,
            name, srtHome_);
        
        try
        {
          probePod.run();
        }
        catch (RuntimeException e)
        {
          e.printStackTrace(console.getErr());
        }
      });
      
      job.schedule();
    }
  }
}
