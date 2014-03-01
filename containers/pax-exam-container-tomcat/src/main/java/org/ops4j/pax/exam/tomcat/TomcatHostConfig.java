/*
 * Copyright 2012 Harald Wellmann
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
package org.ops4j.pax.exam.tomcat;

import java.io.File;

import org.apache.catalina.startup.HostConfig;
import org.apache.catalina.util.ContextName;

public class TomcatHostConfig extends HostConfig {

    @Override
    public void deployWAR(ContextName cn, File war) {
        setCopyXML(true);
        super.deployWAR(cn, war);
        addServiced(cn.getName());
    }
    
    @Override
    protected void deployDescriptor(ContextName cn, File contextXml) {
        super.deployDescriptor(cn, contextXml);
        addServiced(cn.getName());
    }

    @Override
    public void unmanageApp(String contextName) {
        super.unmanageApp(contextName);
        removeServiced(contextName);
    }
}
