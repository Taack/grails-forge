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

import io.micronaut.context.annotation.Prototype;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.ReflectiveAccess;
import org.grails.forge.application.ApplicationType;
import org.grails.forge.application.ContextFactory;
import org.grails.forge.application.TaackAvailableFeatures;
import org.grails.forge.application.generator.ProjectGenerator;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;

@CommandLine.Command(name = CreateTaackappCommand.NAME, description = "Creates a Taack application")
@Prototype
public class CreateTaackappCommand extends CreateCommand {

    public static final String NAME = "create-taackapp";

    @CommandLine.Option(names = {"-f", "--features"}, paramLabel = "FEATURE", split = ",", description = "The features to use. Possible values: ${COMPLETION-CANDIDATES}", completionCandidates = TaackAvailableFeatures.class)
    @ReflectiveAccess
    List<String> features = new ArrayList<>();

    public CreateTaackappCommand(TaackAvailableFeatures availableFeatures,
                                 ContextFactory contextFactory,
                                 ProjectGenerator projectGenerator) {
        super(availableFeatures, contextFactory, ApplicationType.TAACK, projectGenerator);
    }

    @NonNull
    @Override
    protected List<String> getSelectedFeatures() {
        return features;
    }
}
