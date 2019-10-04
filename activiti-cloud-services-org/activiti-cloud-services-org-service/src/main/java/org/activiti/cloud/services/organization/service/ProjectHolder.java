/*
 * Copyright 2018 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.cloud.services.organization.service;

import java.util.Collection;
import java.util.Optional;

import org.activiti.cloud.organization.api.Model;
import org.activiti.cloud.organization.api.ModelType;
import org.activiti.cloud.organization.api.Project;
import org.activiti.cloud.services.common.file.FileContent;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;

/**
 * Builder for projects
 */
public class ProjectHolder {

    private Project project;

    private final MultiKeyMap<String, ModelJsonFile> modelJsonFilesMap = new MultiKeyMap<>();

    private final MultiKeyMap<String, FileContent> modelContentFilesMap = new MultiKeyMap<>();
    
    private final MultiKeyMap<String, FileContent> extensionFilesMap = new MultiKeyMap<>();

    public ProjectHolder setProject(Project project) {
        if (this.project == null) {
            this.project = project;
        }
        return this;
    }

    public ProjectHolder addModelJsonFile(String modelName,
                                          ModelType modelType,
                                          FileContent fileContent) {
        modelJsonFilesMap.put(key(modelName,
                                  modelType),
                              new ModelJsonFile(modelType,
                                                fileContent));
        return this;
    }

    public ProjectHolder addModelContent(String modelName,
                                         ModelType modelType,
                                         FileContent fileContent) {
        modelContentFilesMap.put(key(modelName,
                                     modelType),
                                 fileContent);
        return this;
    }
    
    public ProjectHolder addModelExtension(String modelName,
                                         ModelType modelType,
                                         FileContent fileContent) {
        extensionFilesMap.put(key(modelName,
                                     modelType),
                                 fileContent);
        return this;
    }

    public Optional<Project> getProjectMetadata() {
        return Optional.ofNullable(project);
    }

    public Collection<ModelJsonFile> getModelJsonFiles() {
        return modelJsonFilesMap.values();
    }

    public Optional<FileContent> getModelContentFile(Model model) {
        return Optional.ofNullable(model.getName())
                .map(name -> key(name,
                                 model.getType()))
                .map(modelContentFilesMap::get);
    }
    
    public Optional<FileContent> getModelExtensions(Model model) {
        return Optional.ofNullable(model.getName())
                .map(name -> key(name,
                                 model.getType()))
                .map(extensionFilesMap::get);
    }

    private MultiKey<String> key(String name,
                                 ModelType type) {
        return key(name,
                   type.getName());
    }

    private MultiKey<String> key(String name,
                                 String type) {
        return new MultiKey<>(name,
                              type);
    }

    class ModelJsonFile {

        private final ModelType modelType;

        private final FileContent fileContent;

        public ModelJsonFile(ModelType modelType,
                             FileContent fileContent) {
            this.modelType = modelType;
            this.fileContent = fileContent;
        }

        public ModelType getModelType() {
            return modelType;
        }

        public FileContent getFileContent() {
            return fileContent;
        }
    }
}
