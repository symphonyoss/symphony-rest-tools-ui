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

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class ModelObjectView
{
  private static final String ICONS = "icons/";
  private static final String OBJ16 = ICONS + "obj16/";
  private static final String GIF = ".gif";
      
  private static final Bundle BUNDLE = FrameworkUtil.getBundle(ModelObjectView.class);
  
  public static final ImageDescriptor IMAGE_SYMPHONY     = obj("symphony");
  public static final ImageDescriptor IMAGE_WEB          = obj("web");
  public static final ImageDescriptor IMAGE_KEY_MANAGER  = obj("key_manager");
  public static final ImageDescriptor IMAGE_SESSION_AUTH = obj("session_auth");
  public static final ImageDescriptor IMAGE_KEY_AUTH     = obj("key_auth");
  public static final ImageDescriptor IMAGE_AGENT        = obj("agent");
  
  private static ImageDescriptor obj(String name)
  {
    return image(OBJ16 + name + GIF);
  }
  
  private static ImageDescriptor image(String path)
  {
    URL url = FileLocator.find(BUNDLE, new Path(path), null);
    
    return ImageDescriptor.createFromURL(url);
  }
}