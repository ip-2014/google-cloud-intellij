/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.tools.intellij.appengine.server.instance;

import com.google.cloud.tools.intellij.appengine.util.AppEngineUtil;
import com.google.cloud.tools.intellij.util.GctBundle;

import com.intellij.javaee.run.configuration.CommonModel;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.packaging.artifacts.Artifact;
import com.intellij.packaging.impl.run.BuildArtifactsBeforeRunTaskProvider;
import com.intellij.ui.IdeBorderFactory.PlainSmallWithoutIndent;
import com.intellij.ui.PanelWithAnchor;
import com.intellij.ui.components.JBLabel;

import org.jetbrains.annotations.NotNull;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author nik
 */
public class AppEngineRunConfigurationEditor extends SettingsEditor<CommonModel> implements
    PanelWithAnchor {

  private final Project myProject;
  private JPanel myMainPanel;
  private JComponent anchor;
  private JBLabel myWebArtifactToDeployLabel;
  private JComboBox myArtifactComboBox;
  private JTextField host;
  private JBLabel myPortLabel;
  private JTextField port;
  private JPanel appEngineSettingsPanel;
  private Artifact myLastSelectedArtifact;

  public AppEngineRunConfigurationEditor(Project project) {
    myProject = project;
    myArtifactComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        onArtifactChanged();
      }
    });

    setAnchor(myWebArtifactToDeployLabel);
    appEngineSettingsPanel.setBorder(
        PlainSmallWithoutIndent.createTitledBorder(
            null /* border */,
            GctBundle.message("appengine.run.settings.title.label"),
            0 /* titleJustification */,
            0 /* titlePosition */,
            null /* titleFont */,
            null /* titleColor */));
  }

  private void onArtifactChanged() {
    final Artifact selectedArtifact = getSelectedArtifact();
    if (!Comparing.equal(myLastSelectedArtifact, selectedArtifact)) {
      if (myLastSelectedArtifact != null) {
        BuildArtifactsBeforeRunTaskProvider
            .setBuildArtifactBeforeRunOption(myMainPanel, myProject, myLastSelectedArtifact, false);
      }
      if (selectedArtifact != null) {
        BuildArtifactsBeforeRunTaskProvider
            .setBuildArtifactBeforeRunOption(myMainPanel, myProject, selectedArtifact, true);
      }
      myLastSelectedArtifact = selectedArtifact;
    }
  }

  /**
   * Resets the configuration editor form using the settings in the server model. The following
   * settings have been omitted from the form:
   * <ul>
   * <li> maxModuleInstances - we set this on behalf of the user to prevent breaking the dev app
   * server in debug mode. See
   * <a href="https://github.com/GoogleCloudPlatform/google-cloud-intellij/issues/928">#928</a>
   * </li>
   * <li> automaticRestart - it is set to false so that HotSwap doesn't break IJ's debug server.
   * <a href="https://github.com/GoogleCloudPlatform/google-cloud-intellij/issues/972">#927</a>.
   * </li>
   * </ul>
   */
  @Override
  protected void resetEditorFrom(CommonModel commonModel) {
    final AppEngineServerModel serverModel = (AppEngineServerModel) commonModel.getServerModel();
    final Artifact artifact = serverModel.getArtifact();
    myArtifactComboBox.setSelectedItem(artifact);
    port.setText(intToString(serverModel.getPort()));
    host.setText(serverModel.getHost());
  }

  @Override
  protected void applyEditorTo(CommonModel commonModel) throws ConfigurationException {
    final AppEngineServerModel serverModel = (AppEngineServerModel) commonModel.getServerModel();
    serverModel.setPort(validateInteger(port.getText(), "port"));
    serverModel.setArtifact(getSelectedArtifact());
    serverModel.setHost(host.getText());
  }

  private Integer validateInteger(String intText, String description)
      throws ConfigurationException {
    try {
      return Integer.parseInt(intText);
    } catch (NumberFormatException nfe) {
      throw new ConfigurationException(
          "'" + intText + "' is not a valid " + description + " number.");
    }
  }

  private String intToString(Integer value) {
    return value != null ? String.valueOf(value) : "";
  }

  private Artifact getSelectedArtifact() {
    return (Artifact) myArtifactComboBox.getSelectedItem();
  }
  
  @NotNull
  @Override
  protected JComponent createEditor() {
    AppEngineUtil.setupAppEngineStandardArtifactCombobox(myProject, myArtifactComboBox);
    return myMainPanel;
  }

  @Override
  public JComponent getAnchor() {
    return anchor;
  }

  @Override
  public void setAnchor(JComponent anchor) {
    this.anchor = anchor;
    myWebArtifactToDeployLabel.setAnchor(anchor);
    myPortLabel.setAnchor(anchor);
  }
}
