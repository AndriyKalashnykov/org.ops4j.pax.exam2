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
package org.ops4j.pax.exam.regression.maven;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class WabSampleIT {

    @Test
    public void checkPlainTextFromWabServlet() throws InterruptedException {
        Client client = Client.create();
        
        // The server is started by the exam-maven-plugin in a background process:
        // We need to make sure it has finished startup.
        client.addFilter(new RetryFilter(10, 2000));
        
        
        WebResource resource = client.resource("http://localhost:8181/wab/WABServlet");
        String response = resource.get(String.class);
        assertThat(response, containsString("wab symbolic name : wab-sample"));
    }
}
