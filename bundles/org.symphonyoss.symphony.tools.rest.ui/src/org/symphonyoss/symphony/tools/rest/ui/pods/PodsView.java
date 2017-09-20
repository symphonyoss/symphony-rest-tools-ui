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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.symphonyoss.symphony.tools.rest.model.IModelListener;
import org.symphonyoss.symphony.tools.rest.model.IUrlEndpoint;
import org.symphonyoss.symphony.tools.rest.model.IVirtualModelObject;
import org.symphonyoss.symphony.tools.rest.ui.ModelObjectContentProvider;
import org.symphonyoss.symphony.tools.rest.ui.ModelObjectImageAndLabelProvider;
import org.symphonyoss.symphony.tools.rest.ui.ModelObjectLabelProvider;
import org.symphonyoss.symphony.tools.rest.util.home.ISrtHome;

public class PodsView extends ModelObjectView
{
//  @Inject
//  private IConsoleManager consoleManager_;
  @Inject
  private ISrtHome        srtHome_;
  
  @PostConstruct
  public void createControls(Composite parent, EMenuService menuService)
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
            new ModelObjectImageAndLabelProvider<IVirtualModelObject>(display,
                IVirtualModelObject.class,
                (o) -> o.getName()));
    
    TreeViewerColumn typeColumn = new TreeViewerColumn(viewer, SWT.NONE);
    typeColumn.getColumn().setText("Type");
    typeColumn.getColumn().setWidth(100);
    typeColumn.setLabelProvider(
            new ModelObjectLabelProvider<IVirtualModelObject>(display,
                IVirtualModelObject.class,
                (o) -> o.getTypeName()));
    
    TreeViewerColumn urlColumn = new TreeViewerColumn(viewer, SWT.NONE);
    urlColumn.getColumn().setText("URL");
    urlColumn.getColumn().setWidth(300);
    urlColumn.setLabelProvider(
            new ModelObjectLabelProvider<IUrlEndpoint>(display,
                IUrlEndpoint.class,
                (o) -> o.getUrl()));
    
    // register context menu on the table
    menuService.registerContextMenu(viewer.getControl(), "org.symphonyoss.symphony.tools.rest.ui.popupmenu.pods");
    
    viewer.setInput(srtHome_);
    ColumnViewerToolTipSupport.enableFor(viewer);
    
    srtHome_.addListener(new IModelListener()
    {
      
      @Override
      public void modelObjectChanged(IVirtualModelObject modelObject)
      {
        display.asyncExec(() -> viewer.refresh(modelObject));
      }
      
      @Override
      public void modelChanged()
      {
        display.asyncExec(() -> viewer.refresh());
      }
    });
  }
}
