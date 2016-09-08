/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.tools.intellij.appengine.facet;

import com.google.cloud.tools.intellij.ui.GoogleCloudToolsIcons;

import com.intellij.framework.FrameworkTypeEx;
import com.intellij.framework.addSupport.FrameworkSupportInModuleProvider;

import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

/**
 * @author nik
 */
public class AppEngineFrameworkType extends FrameworkTypeEx {
  public static final String ID = "appengine-java";

  public AppEngineFrameworkType() {
    super(ID);
  }

  static AppEngineFrameworkType getFrameworkType() {
    return EP_NAME.findExtension(AppEngineFrameworkType.class);
  }

  @NotNull
  @Override
  public FrameworkSupportInModuleProvider createProvider() {
    return new AppEngineSupportProvider();
  }

  @NotNull
  @Override
  public String getPresentableName() {
    return "Google App Engine";
  }

  @NotNull
  @Override
  public Icon getIcon() {
    return GoogleCloudToolsIcons.APP_ENGINE;
  }
}