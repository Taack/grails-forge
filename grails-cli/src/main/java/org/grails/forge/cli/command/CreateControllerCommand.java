/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grails.forge.cli.command;

import io.micronaut.context.annotation.Parameter;
import io.micronaut.context.annotation.Prototype;
import io.micronaut.core.annotation.ReflectiveAccess;
import io.micronaut.core.util.functional.ThrowingSupplier;
import jakarta.inject.Inject;
import org.grails.forge.application.Project;
import org.grails.forge.cli.CodeGenConfig;
import org.grails.forge.io.ConsoleOutput;
import org.grails.forge.io.OutputHandler;
import org.grails.forge.template.RenderResult;
import org.grails.forge.template.RockerTemplate;
import org.grails.forge.template.TemplateRenderer;
import org.grails.forge.cli.command.templates.controller;
import org.grails.forge.cli.command.templates.controllerSpec;
import picocli.CommandLine;

import java.io.IOException;

@CommandLine.Command(name = CreateControllerCommand.NAME, description = "Creates a Grails Controller")
public class CreateControllerCommand extends CodeGenCommand {

    public static final String NAME = "create-controller";

    @ReflectiveAccess
    @CommandLine.Parameters(paramLabel = "CONTROLLER-NAME", description = "The name of the controller to create")
    String controllerName;

    @Inject
    public CreateControllerCommand(@Parameter CodeGenConfig config) {
        super(config);
    }

    public CreateControllerCommand(CodeGenConfig config,
                                   ThrowingSupplier<OutputHandler, IOException> outputHandlerSupplier,
                                   ConsoleOutput consoleOutput) {
        super(config, outputHandlerSupplier, consoleOutput);
    }

    @Override
    public boolean applies() {
        return true;
    }

    @Override
    public Integer call() throws Exception {
        final Project project = getProject(controllerName);
        TemplateRenderer templateRenderer = getTemplateRenderer(project);
        final RenderResult controllerRenderResult = templateRenderer.render(new RockerTemplate("grails-app/controllers/{packagePath}/{className}Controller.groovy", controller.template(project)), overwrite);
        final RenderResult controllerSpecRenderResult = templateRenderer.render(new RockerTemplate("src/test/groovy/{packagePath}/{className}ControllerSpec.groovy", controllerSpec.template(project)), overwrite);
        if (controllerRenderResult != null && controllerSpecRenderResult != null) {
            logRenderResult(controllerRenderResult);
            logRenderResult(controllerSpecRenderResult);
        }

        return 0;
    }

    private void logRenderResult(RenderResult result) throws Exception {
        if (result != null) {
            if (result.isSuccess()) {
                out("@|blue ||@ Rendered controller to " + result.getPath());
            } else if (result.isSkipped()) {
                warning("Rendering skipped for " + result.getPath() + " because it already exists. Run again with -f to overwrite.");
            } else if (result.getError() != null) {
                throw result.getError();
            }
        }
    }
}
