/*
 * Copyright 2019 Alfresco, Inc. and/or its affiliates.
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

package org.activiti.cloud.services.organization.converter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.cloud.organization.api.ModelContentConverter;
import org.activiti.cloud.organization.api.ModelType;
import org.activiti.cloud.organization.api.ProcessModelType;
import org.activiti.cloud.organization.core.error.ModelingException;
import org.activiti.cloud.services.common.file.FileContent;
import org.apache.commons.lang3.ArrayUtils;

import static org.activiti.bpmn.converter.util.BpmnXMLUtil.createSafeXmlInputFactory;

/**
 * Implementation of {@link ModelContentConverter} for process models
 */
public class ProcessModelContentConverter implements ModelContentConverter<BpmnProcessModelContent> {

    private final ProcessModelType processModelType;

    private final BpmnXMLConverter bpmnConverter;

    public ProcessModelContentConverter(ProcessModelType processModelType,
                                        BpmnXMLConverter bpmnConverter) {
        this.bpmnConverter = bpmnConverter;
        this.processModelType = processModelType;
    }

    @Override
    public ModelType getHandledModelType() {
        return processModelType;
    }

    @Override
    public Optional<BpmnProcessModelContent> convertToModelContent(byte[] bytes) {
        if (ArrayUtils.isEmpty(bytes)) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(convertToBpmnModel(bytes))
                    .map(BpmnProcessModelContent::new);
        } catch (IOException | XMLStreamException ex) {
            throw new ModelingException("Invalid bpmn model",
                                        ex);
        }
    }

    @Override
    public byte[] convertToBytes(BpmnProcessModelContent bpmnProcessModelContent) {
         return bpmnConverter.convertToXML(bpmnProcessModelContent.getBpmnModel());
    }

    public Optional<BpmnProcessModelContent> convertToModelContent(BpmnModel bpmnModel) {
        return Optional.ofNullable(bpmnModel)
                .map(BpmnProcessModelContent::new);
    }

    public BpmnModel convertToBpmnModel(byte[] modelContent) throws IOException, XMLStreamException {
        try (InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(modelContent))) {
            XMLStreamReader xmlReader = createSafeXmlInputFactory().createXMLStreamReader(reader);
            return bpmnConverter.convertToBpmnModel(xmlReader);
        }
    }

    @Override
    public FileContent overrideModelId(FileContent fileContent,
                                       Map<String, String> modelIdentifiers) {
        Optional<BpmnProcessModelContent> processModelContent = this.convertToModelContent(fileContent.getFileContent());
        if (processModelContent.isPresent()) {
            BpmnProcessModelContent modelContent = processModelContent.get();
            ReferenceIdOverrider referenceIdOverrider = new ReferenceIdOverrider(modelIdentifiers);
            this.overrideAllProcessDefinition(modelContent, referenceIdOverrider);
            byte[] overriddenContent = this.convertToBytes(modelContent);
            return new FileContent(fileContent.getFilename(), fileContent.getContentType(), overriddenContent);
        }
        return fileContent;
    }

    public void overrideAllProcessDefinition(BpmnProcessModelContent processModelContent,
                                             ReferenceIdOverrider referenceIdOverrider) {
        processModelContent.getBpmnModel().getProcesses().forEach(process -> {
            referenceIdOverrider.overrideProcessId(process);
            overrideAllIdReferences(process, referenceIdOverrider);
        });
    }

    private void overrideAllIdReferences(Process process,
                                        ReferenceIdOverrider referenceIdOverrider) {
        process.getFlowElements().forEach(element -> element.accept(referenceIdOverrider));
    }

}
