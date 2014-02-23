/*
 * Copyright 2012 Harald Wellmann
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.ops4j.pax.exam.maven;

/**
 * Keys for storing and looking properties of the Pax Exam container background process in
 * the Maven plugin context. Used for communication between the mojos of this Maven plugin.
 */
public class Constants {

    /**
     * Key for the runner for the Pax Exam container background process.
     */
    public static final String TEST_CONTAINER_RUNNER_KEY = "exam.testContainerRunner";

    /**
     * Key for the shutdown port of the Pax Exam container background process.
     */
    public static final String TEST_CONTAINER_PORT_KEY = "exam.testContainerPort";

    /** Hidden utility class constructor. */
    private Constants() {
    }
}
