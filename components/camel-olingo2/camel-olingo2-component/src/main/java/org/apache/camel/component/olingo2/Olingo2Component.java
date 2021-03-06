/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.olingo2;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.component.olingo2.api.impl.Olingo2AppImpl;
import org.apache.camel.component.olingo2.internal.Olingo2ApiCollection;
import org.apache.camel.component.olingo2.internal.Olingo2ApiName;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.util.ObjectHelper;
import org.apache.camel.util.component.AbstractApiComponent;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

/**
 * Represents the component that manages {@link Olingo2Endpoint}.
 */
@UriEndpoint(scheme = "olingo2", consumerClass = Olingo2Consumer.class, consumerPrefix = "consumer")
public class Olingo2Component extends AbstractApiComponent<Olingo2ApiName, Olingo2Configuration, Olingo2ApiCollection> {

    // component level shared proxy
    private Olingo2AppWrapper apiProxy;

    public Olingo2Component() {
        super(Olingo2Endpoint.class, Olingo2ApiName.class, Olingo2ApiCollection.getCollection());
    }

    public Olingo2Component(CamelContext context) {
        super(context, Olingo2Endpoint.class, Olingo2ApiName.class, Olingo2ApiCollection.getCollection());
    }

    @Override
    protected Olingo2ApiName getApiName(String apiNameStr) throws IllegalArgumentException {
        return Olingo2ApiName.fromValue(apiNameStr);
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        // parse remaining to extract resourcePath and queryParams
        final String[] pathSegments = remaining.split("/", -1);
        final String methodName = pathSegments[0];

        if (pathSegments.length > 1) {
            final StringBuilder resourcePath = new StringBuilder();
            for (int i = 1; i < pathSegments.length; i++) {
                resourcePath.append(pathSegments[i]);
                if (i < (pathSegments.length - 1)) {
                    resourcePath.append('/');
                }
            }
            // This will override any URI supplied ?resourcePath=... param
            parameters.put(Olingo2Endpoint.RESOURCE_PATH_PROPERTY, resourcePath.toString());
        }

        final Olingo2Configuration endpointConfiguration = createEndpointConfiguration(Olingo2ApiName.DEFAULT);
        final Endpoint endpoint = createEndpoint(uri, methodName, Olingo2ApiName.DEFAULT, endpointConfiguration);

        // set endpoint property inBody
        setProperties(endpoint, parameters);

        // configure endpoint properties and initialize state
        endpoint.configureProperties(parameters);

        return endpoint;
    }

    @Override
    protected Endpoint createEndpoint(String uri, String methodName, Olingo2ApiName apiName,
                                      Olingo2Configuration endpointConfiguration) {
        return new Olingo2Endpoint(uri, this, apiName, methodName, endpointConfiguration);
    }

    public Olingo2AppWrapper createApiProxy(Olingo2Configuration endpointConfiguration) {
        final Olingo2AppWrapper result;
        if (endpointConfiguration.equals(this.configuration)) {
            synchronized (this) {
                if (apiProxy == null) {
                    apiProxy = createOlingo2App(this.configuration);
                }
            }
            result = apiProxy;
        } else {
            result = createOlingo2App(endpointConfiguration);
        }
        return result;
    }

    private Olingo2AppWrapper createOlingo2App(Olingo2Configuration configuration) {

        HttpAsyncClientBuilder clientBuilder = configuration.getHttpAsyncClientBuilder();
        if (clientBuilder == null) {
            clientBuilder = HttpAsyncClientBuilder.create();

            // apply simple configuration properties
            final RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
            requestConfigBuilder.setConnectTimeout(configuration.getConnectTimeout());
            requestConfigBuilder.setSocketTimeout(configuration.getSocketTimeout());

            final HttpHost proxy = configuration.getProxy();
            if (proxy != null) {
                requestConfigBuilder.setProxy(proxy);
            }

            // set default request config
            clientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());

            SSLContextParameters sslContextParameters = configuration.getSslContextParameters();
            if (sslContextParameters == null) {
                // use defaults if not specified
                sslContextParameters = new SSLContextParameters();
            }
            try {
                clientBuilder.setSSLContext(sslContextParameters.createSSLContext());
            } catch (GeneralSecurityException e) {
                throw ObjectHelper.wrapRuntimeCamelException(e);
            } catch (IOException e) {
                throw ObjectHelper.wrapRuntimeCamelException(e);
            }
        }

        apiProxy = new Olingo2AppWrapper(new Olingo2AppImpl(configuration.getServiceUri(), clientBuilder));
        apiProxy.getOlingo2App().setContentType(configuration.getContentType());
        apiProxy.getOlingo2App().setHttpHeaders(configuration.getHttpHeaders());

        return apiProxy;
    }

    public void closeApiProxy(Olingo2AppWrapper apiProxy) {
        if (this.apiProxy != apiProxy) {
            // not a shared proxy
            apiProxy.close();
        }
    }

    @Override
    protected void doStop() throws Exception {
        if (apiProxy != null) {
            apiProxy.close();
        }
    }
}
