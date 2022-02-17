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
package org.grails.forge.feature.build.gradle;

import jakarta.inject.Singleton;
import org.grails.forge.application.ApplicationType;
import org.grails.forge.application.generator.GeneratorContext;
import org.grails.forge.build.gradle.GradleBuild;
import org.grails.forge.build.gradle.GradleBuildCreator;
import org.grails.forge.build.gradle.GradlePlugin;
import org.grails.forge.feature.Feature;
import org.grails.forge.feature.build.BuildFeature;
import org.grails.forge.feature.build.gitignore;
import org.grails.forge.feature.build.gradle.templates.buildGradle;
import org.grails.forge.feature.build.gradle.templates.gradleProperties;
import org.grails.forge.feature.build.gradle.templates.settingsGradle;
import org.grails.forge.options.BuildTool;
import org.grails.forge.options.Options;
import org.grails.forge.template.BinaryTemplate;
import org.grails.forge.template.RockerTemplate;
import org.grails.forge.template.URLTemplate;

import java.util.Set;

@Singleton
public class Gradle implements BuildFeature {
    private static final String WRAPPER_JAR = "gradle/wrapper/gradle-wrapper.jar";
    private static final String WRAPPER_PROPS = "gradle/wrapper/gradle-wrapper.properties";

    private final GradleBuildCreator dependencyResolver;

    public Gradle(GradleBuildCreator dependencyResolver) {
        this.dependencyResolver = dependencyResolver;
    }

    @Override
    public String getName() {
        return "gradle";
    }

    @Override
    public void apply(GeneratorContext generatorContext) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        generatorContext.addTemplate("gradleWrapperJar", new BinaryTemplate(WRAPPER_JAR, classLoader.getResource(WRAPPER_JAR)));
        generatorContext.addTemplate("gradleWrapperProperties", new URLTemplate(WRAPPER_PROPS, classLoader.getResource(WRAPPER_PROPS)));
        generatorContext.addTemplate("gradleWrapper", new URLTemplate("gradlew", classLoader.getResource("gradle/gradlew"), true));
        generatorContext.addTemplate("gradleWrapperBat", new URLTemplate("gradlew.bat", classLoader.getResource("gradle/gradlew.bat"), false));

        if ((generatorContext.getFeatures().language() != null && generatorContext.getFeatures().language().isGroovy()) || generatorContext.getFeatures().testFramework().isSpock()) {
            generatorContext.addBuildPlugin(GradlePlugin.builder().id("groovy").build());
        }

        BuildTool buildTool = generatorContext.getBuildTool();
        GradleBuild build = dependencyResolver.create(generatorContext);

        generatorContext.addTemplate("build", new RockerTemplate(buildTool.getBuildFileName(), buildGradle.template(
                generatorContext.getApplicationType(),
                generatorContext.getProject(),
                generatorContext.getFeatures(),
                build
        )));

        generatorContext.addTemplate("gitignore", new RockerTemplate(".gitignore", gitignore.template()));
        generatorContext.addTemplate("projectProperties", new RockerTemplate("gradle.properties", gradleProperties.template(generatorContext.getBuildProperties().getProperties())));
        String settingsFile = "settings.gradle";
        generatorContext.addTemplate("gradleSettings", new RockerTemplate(settingsFile, settingsGradle.template(generatorContext.getProject(), build)));
    }

    @Override
    public boolean shouldApply(ApplicationType applicationType,
                               Options options,
                               Set<Feature> selectedFeatures) {
        return options.getBuildTool().isGradle();
    }
}
