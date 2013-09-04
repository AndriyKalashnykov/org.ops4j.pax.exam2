/*
 * Copyright 2012 Harald Wellmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.exam.regression.javaee.userprobe;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.warProbe;
import static org.ops4j.pax.exam.Info.getOps4jBaseVersion;
import static org.ops4j.pax.exam.Info.getPaxExamVersion;

import org.ops4j.pax.exam.ConfigurationFactory;
import org.ops4j.pax.exam.Option;

public class ProbeConfigurationFactory implements ConfigurationFactory {

    @Override
    public Option[] createConfiguration() {
        return options(
            warProbe()
            .library("target/test-classes")
            .library(
                maven("org.ops4j.pax.exam.samples", "pax-exam-sample1-service", getPaxExamVersion()))
            .library(
                maven("org.ops4j.pax.exam.samples", "pax-exam-sample1-model", getPaxExamVersion()))
            .library(maven("org.ops4j.pax.exam", "pax-exam-servlet-bridge", getPaxExamVersion()))
            .library(maven("org.ops4j.pax.exam", "pax-exam-cdi", getPaxExamVersion()))
            .library(maven("org.ops4j.pax.exam", "pax-exam", getPaxExamVersion()))
            .library(maven("org.ops4j.base", "ops4j-base-spi", getOps4jBaseVersion()))
            .library(maven("junit", "junit", "4.9"))
            );
    }
}
