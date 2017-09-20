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

package org.symphonyoss.symphony.tools.rest.ui;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.symphonyoss.symphony.tools.rest.model.AgentConfig;
import org.symphonyoss.symphony.tools.rest.model.IVirtualModelObject;
import org.symphonyoss.symphony.tools.rest.model.Pod;
import org.symphonyoss.symphony.tools.rest.model.PodConfig;
import org.symphonyoss.symphony.tools.rest.ui.pods.ModelObjectView;

public class ModelObjectImageAndLabelProvider<M> extends ModelObjectLabelProvider<M>
{
  private final ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());

  public ModelObjectImageAndLabelProvider(Display display, Class<M> type, ILabelProvider<M> labelProvider)
  {
    super(display, type, labelProvider);
  }
  
  @Override
  public Image getImage(Object element)
  {
    if(element instanceof IVirtualModelObject)
    {
      switch(((IVirtualModelObject)element).getTypeName())
      {
        case PodConfig.TYPE_NAME:
          return resourceManager.createImage(ModelObjectView.IMAGE_SYMPHONY);
          
        case PodConfig.WEB_TYPE_NAME:
          return resourceManager.createImage(ModelObjectView.IMAGE_WEB);
          
        case Pod.TYPE_KEY_MANAGER:
          return resourceManager.createImage(ModelObjectView.IMAGE_KEY_MANAGER);
          
        case Pod.TYPE_SESSION_AUTH:
          return resourceManager.createImage(ModelObjectView.IMAGE_SESSION_AUTH);
          
        case Pod.TYPE_KEY_AUTH:
          return resourceManager.createImage(ModelObjectView.IMAGE_KEY_AUTH);
          
        case AgentConfig.TYPE_NAME:
          return resourceManager.createImage(ModelObjectView.IMAGE_AGENT);
      }
    }
  
    return null;
  }
}
