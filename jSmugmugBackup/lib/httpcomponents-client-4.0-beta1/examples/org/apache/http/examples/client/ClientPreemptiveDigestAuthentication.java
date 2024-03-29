/*
 * $HeadURL: https://svn.apache.org/repos/asf/httpcomponents/httpclient/tags/4.0-beta1/module-client/src/examples/org/apache/http/examples/client/ClientPreemptiveDigestAuthentication.java $
 * $Revision: 659971 $
 * $Date: 2008-05-25 14:01:22 +0200 (Sun, 25 May 2008) $
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.http.examples.client;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

/**
 * An example of HttpClient can be customized to authenticate 
 * preemptively using DIGEST scheme.
 * <b/>
 * Generally, preemptive authentication can be considered less
 * secure than a response to an authentication challenge
 * and therefore discouraged.
 * <b/>
 * This code is NOT officially supported! Use at your risk.
 */
public class ClientPreemptiveDigestAuthentication {

    public static void main(String[] args) throws Exception {

        DefaultHttpClient httpclient = new DefaultHttpClient();

        httpclient.getCredentialsProvider().setCredentials(
                new AuthScope("localhost", 80), 
                new UsernamePasswordCredentials("username", "password"));

        BasicHttpContext localcontext = new BasicHttpContext();
        // Generate DIGEST scheme object, initialize it and stick it to 
        // the local execution context
        DigestScheme digestAuth = new DigestScheme();
        // Suppose we already know the realm name
        digestAuth.overrideParamter("realm", "some realm");        
        // Suppose we already know the expected nonce value 
        digestAuth.overrideParamter("nonce", "whatever");        
        localcontext.setAttribute("preemptive-auth", digestAuth);
        
        // Add as the first request interceptor
        httpclient.addRequestInterceptor(new PreemptiveAuth(), 0);
        // Add as the last response interceptor
        httpclient.addResponseInterceptor(new PersistentDigest());
        
        HttpHost targetHost = new HttpHost("localhost", 80, "http"); 

        HttpGet httpget = new HttpGet("/");

        System.out.println("executing request: " + httpget.getRequestLine());
        System.out.println("to target: " + targetHost);
        
        for (int i = 0; i < 3; i++) {
            HttpResponse response = httpclient.execute(targetHost, httpget, localcontext);
            HttpEntity entity = response.getEntity();

            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            if (entity != null) {
                System.out.println("Response content length: " + entity.getContentLength());
                System.out.println("Chunked?: " + entity.isChunked());
                entity.consumeContent();
            }
        }
    }
    
    static class PreemptiveAuth implements HttpRequestInterceptor {

        public void process(
                final HttpRequest request, 
                final HttpContext context) throws HttpException, IOException {
            
            AuthState authState = (AuthState) context.getAttribute(
                    ClientContext.TARGET_AUTH_STATE);
            
            // If no auth scheme avaialble yet, try to initialize it preemptively
            if (authState.getAuthScheme() == null) {
                AuthScheme authScheme = (AuthScheme) context.getAttribute(
                        "preemptive-auth");
                CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(
                        ClientContext.CREDS_PROVIDER);
                HttpHost targetHost = (HttpHost) context.getAttribute(
                        ExecutionContext.HTTP_TARGET_HOST);
                if (authScheme != null) {
                    Credentials creds = credsProvider.getCredentials(
                            new AuthScope(
                                    targetHost.getHostName(), 
                                    targetHost.getPort()));
                    if (creds == null) {
                        throw new HttpException("No credentials for preemptive authentication");
                    }
                    authState.setAuthScheme(authScheme);
                    authState.setCredentials(creds);
                }
            }
            
        }
        
    }

    static class PersistentDigest implements HttpResponseInterceptor {

        public void process(
                final HttpResponse response, 
                final HttpContext context) throws HttpException, IOException {
            AuthState authState = (AuthState) context.getAttribute(
                    ClientContext.TARGET_AUTH_STATE);
            if (authState != null) {
                AuthScheme authScheme = authState.getAuthScheme();
                // Stick the auth scheme to the local context, so
                // we could try to authenticate subsequent requests
                // preemptively
                if (authScheme instanceof DigestScheme) {
                    context.setAttribute("preemptive-auth", authScheme);
                }
            }
        }
        
    }
    
}
