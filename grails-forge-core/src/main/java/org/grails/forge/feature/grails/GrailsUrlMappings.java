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
package org.grails.forge.feature.grails;

import jakarta.inject.Singleton;
import org.grails.forge.application.ApplicationType;
import org.grails.forge.application.generator.GeneratorContext;
import org.grails.forge.feature.DefaultFeature;
import org.grails.forge.feature.Feature;
import org.grails.forge.options.Options;
import org.grails.forge.template.RockerTemplate;
import org.grails.forge.feature.grails.templates.urlMappings;

import java.util.Set;

@Singleton
public class GrailsUrlMappings implements DefaultFeature {

    @Override
    public boolean shouldApply(ApplicationType applicationType, Options options, Set<Feature> selectedFeatures) {
        return applicationType == ApplicationType.WEB || applicationType == ApplicationType.WEB_PLUGIN || applicationType == ApplicationType.REST_API || applicationType == ApplicationType.TAACK;
    }

    @Override
    public String getName() {
        return "grails-url-mappings";
    }

    @Override
    public boolean supports(ApplicationType applicationType) {
        return true;
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public void apply(GeneratorContext generatorContext) {
        generatorContext.addTemplate("urlMappings", new RockerTemplate(getPath(), urlMappings.template(generatorContext.getProject(), generatorContext.getApplicationType())));
    }

    protected String getPath() {
        return "grails-app/controllers/{packagePath}/UrlMappings.groovy";
    }
}
