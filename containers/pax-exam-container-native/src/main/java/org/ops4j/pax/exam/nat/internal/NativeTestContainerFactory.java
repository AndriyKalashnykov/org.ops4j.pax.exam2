/*
 * Copyright 2009 Toni Menzel.
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
package org.ops4j.pax.exam.nat.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.kohsuke.MetaInfServices;
import org.ops4j.pax.exam.ExamSystem;
import org.ops4j.pax.exam.TestContainer;
import org.ops4j.pax.exam.TestContainerException;
import org.ops4j.pax.exam.TestContainerFactory;
import org.osgi.framework.launch.FrameworkFactory;

/**
 * @author Toni Menzel
 * @since Jan 7, 2010
 */
@MetaInfServices
public class NativeTestContainerFactory implements TestContainerFactory {

    public TestContainer[] create(ExamSystem system) {
        List<TestContainer> containers = new ArrayList<TestContainer>();
        Iterator<FrameworkFactory> factories = ServiceLoader.load(FrameworkFactory.class)
            .iterator();
        boolean factoryFound = false;
        while (factories.hasNext()) {
            try {
                containers.add(new NativeTestContainer(system, factories.next()));
                factoryFound = true;
            }
            catch (IOException e) {
                throw new TestContainerException("Problem initializing container.", e);
            }
        }
        if (!factoryFound) {
            throw new TestContainerException(
                "No service org.osgi.framework.launch.FrameworkFactory found in META-INF/services on classpath");
        }
        return containers.toArray(new TestContainer[containers.size()]);
    }
}
