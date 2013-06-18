/*
 * Copyright 2013 Christoph Läubrich
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
package org.ops4j.pax.exam.obr.internal;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.ops4j.pax.exam.obr.ObrValidation;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.obr.RepositoryAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BundleActivator} that is used internaly to interact with {@link RepositoryAdmin}
 */
public class ObrOptionActivator implements BundleActivator, ServiceTrackerCustomizer, ObrValidation {

    private static final Logger LOG = LoggerFactory.getLogger(ObrOptionActivator.class);

    private ServiceTracker serviceTracker;
    private Set<String> urlList;
    private List<String[]> bundleList;
    private BundleContext context;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final List<Exception> exceptions = new ArrayList<Exception>();
    private final List<Future<?>> submitedOBRs = new ArrayList<Future<?>>();
    private final CountDownLatch barrier = new CountDownLatch(1);

    @SuppressWarnings("unchecked")
    @Override
    public void start(BundleContext bundleContext) throws Exception {
        this.context = bundleContext;
        URL entry = bundleContext.getBundle().getEntry("/obrdata.obj");
        InputStream stream = entry.openStream();
        Hashtable<String, Object> barrierProperties;
        try {
            ObjectInputStream oi = new ObjectInputStream(stream);
            try {
                urlList = (Set<String>) oi.readObject();
                bundleList = (List<String[]>) oi.readObject();
                barrierProperties = (Hashtable<String, Object>) oi.readObject();
            }
            finally {
                oi.close();
            }
        }
        finally {
            stream.close();
        }
        LOG.info("Waiting for RepositoryAdmin to resolve {}", asString(bundleList));
        serviceTracker = new ServiceTracker(bundleContext, RepositoryAdmin.class.getName(), this);
        serviceTracker.open();
        // register the CountDownLatch
        bundleContext.registerService(CountDownLatch.class.getName(), barrier, barrierProperties);
        // Register the validation service
        bundleContext.registerService(ObrValidation.class.getName(), this, null);
    }

    private static Object asString(List<String[]> bundleList) {
        StringBuilder sb = new StringBuilder();
        for (String[] strings : bundleList) {
            sb.append("symbolicname=");
            sb.append(strings[0]);
            if (strings[1] != null) {
                sb.append(",version=");
                sb.append(strings[1]);
                sb.append(" ");
            }
        }
        return sb;
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        serviceTracker.close();
        executorService.shutdownNow();
    }

    @Override
    public Object addingService(ServiceReference reference) {
        Object service = context.getService(reference);
        if (service instanceof RepositoryAdmin) {
            final RepositoryAdmin repositoryAdmin = (RepositoryAdmin) service;
            for (String rawURL : urlList) {
                try {
                    repositoryAdmin.addRepository(new URL(rawURL));
                    LOG.info("Added repository with URL {}", rawURL);
                }
                // CHECKSTYLE:SKIP
                catch (Exception e) {
                    LOG.info("Adding repository with URL {} failed", rawURL, e);
                    synchronized (exceptions) {
                        exceptions.add(e);
                    }
                }
            }
            Future<?> submit = executorService.submit(new ObrResolverRunnable(repositoryAdmin,
                bundleList, exceptions, barrier));
            synchronized (submitedOBRs) {
                submitedOBRs.add(submit);
            }
        }
        return service;
    }

    @Override
    public void modifiedService(ServiceReference reference, Object service) {
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        if (service instanceof RepositoryAdmin) {
            final RepositoryAdmin repositoryAdmin = (RepositoryAdmin) service;
            for (String rawURL : urlList) {
                try {
                    repositoryAdmin.removeRepository(new URL(rawURL));
                    LOG.info("removed repository with URL {}", rawURL);
                }
                // CHECKSTYLE:SKIP
                catch (Exception e) {
                    LOG.info("removing repository with URL {} failed", rawURL, e);
                    synchronized (exceptions) {
                        exceptions.add(e);
                    }
                }
            }
        }
        context.ungetService(reference);
    }

    @Override
    public void validate() {
        Future<?>[] futures;
        synchronized (submitedOBRs) {
            futures = submitedOBRs.toArray(new Future<?>[0]);
        }
        if (futures.length > 0) {
            // Wait for each future to complete...
            for (Future<?> future : futures) {
                try {
                    future.get();
                }
                catch (InterruptedException e) {
                    // We ignore this...
                }
                catch (ExecutionException e) {
                    synchronized (exceptions) {
                        exceptions.add(e);
                    }
                }
            }
            Exception[] recordedExceptions;
            synchronized (exceptions) {
                recordedExceptions = exceptions.toArray(new Exception[0]);
            }
            if (recordedExceptions.length > 0) {
                StringBuffer sb = new StringBuffer("recorded " + recordedExceptions.length
                    + " exception(s) during execution: \r\n");
                // Anything failed in the chain...
                for (int i = 0; i < recordedExceptions.length; i++) {
                    Exception exception = recordedExceptions[i];
                    sb.append("\r\nException ");
                    sb.append((i + 1));
                    sb.append(": ");
                    sb.append(exception.toString());
                    sb.append("\r\n");
                    StringWriter writer = new StringWriter();
                    PrintWriter pw = new PrintWriter(writer);
                    exception.printStackTrace(pw);
                    pw.flush();
                    sb.append(writer.toString());
                }
                throw new RuntimeException(sb.toString());
            }
        }
        else {
            throw new RuntimeException(
                "no obr action was started until now, maybe the OBRService is not avaiable?");
        }
    }
}
