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

package org.activiti.cloud.organization.api;

import java.util.Map;

import org.activiti.cloud.organization.api.process.Extensions;
import org.activiti.cloud.services.auditable.Auditable;

/**
 * Interface for models
 */
public interface Model<A extends Project, U> extends Auditable<U> {

    String getId();

    void setId(String id);

    String getName();

    void setName(String name);

    String getType();

    void setType(String type);

    A getProject();

    void setProject(A project);

    String getVersion();

    String getContentType();

    void setContentType(String contentType);

    String getContent();

    void setContent(String content);

    Extensions getExtensions();

    void setExtensions(Extensions extensions);
    
    Map<String,Object> getMetadata();
    
    void setMetadata(Map<String,Object> metadata);

    String getTemplate();

    void setTemplate(String template);
}
