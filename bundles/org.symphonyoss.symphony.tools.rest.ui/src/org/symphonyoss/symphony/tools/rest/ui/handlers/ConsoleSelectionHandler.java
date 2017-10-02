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

import org.eclipse.swt.widgets.Shell;
import org.symphonyoss.symphony.tools.rest.ui.console.IConsole;
import org.symphonyoss.symphony.tools.rest.ui.console.IConsoleManager;
import org.symphonyoss.symphony.tools.rest.util.Console;

public abstract class ConsoleSelectionHandler<T> extends SelectionHandler<T>
{
  @Inject
  private IConsoleManager consoleManager_;
  
  public ConsoleSelectionHandler(String commandName, Class<T> type, String typeName, boolean selectionRequired)
  {
    super(commandName, type, typeName, selectionRequired);
  }

  @Override
  protected void execute(Shell shell, T selection)
  {
    final IConsole console = consoleManager_.createConsole();
    final Console srtConsole = new Console(console.getIn(), console.getOut(), console.getErr());
    
    srtConsole.getOut().println(getCommandName() + "(" + selection + ") starting...");
    
    try
    {
      execute(shell, selection, srtConsole);
      
      srtConsole.getOut().println(getCommandName() + "(" + selection + ") completed.");
    }
    catch (RuntimeException e)
    {
      srtConsole.getErr().println(getCommandName() + "(" + selection + ") FAILED");
      e.printStackTrace(srtConsole.getErr());
    }
    finally
    {
      srtConsole.close();
      console.close();
    }
  }

  protected abstract void execute(Shell shell, T selection, Console srtConsole);
}
