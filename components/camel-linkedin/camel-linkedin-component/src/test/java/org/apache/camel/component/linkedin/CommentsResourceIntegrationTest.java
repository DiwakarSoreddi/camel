/*
 * Camel Api Route test generated by camel-component-util-maven-plugin
 * Generated on: Wed Jul 09 19:57:10 PDT 2014
 */
package org.apache.camel.component.linkedin;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.builder.RouteBuilder;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.camel.component.linkedin.internal.LinkedInApiCollection;
import org.apache.camel.component.linkedin.internal.CommentsResourceApiMethod;

/**
 * Test class for {@link org.apache.camel.component.linkedin.api.CommentsResource} APIs.
 */
public class CommentsResourceIntegrationTest extends AbstractLinkedInTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(CommentsResourceIntegrationTest.class);
    private static final String PATH_PREFIX = LinkedInApiCollection.getCollection().getApiName(CommentsResourceApiMethod.class).getName();

    // TODO provide parameter values for getComment
    @Ignore
    @Test
    public void testGetComment() throws Exception {
        final Map<String, Object> headers = new HashMap<String, Object>();
        // parameter type is String
        headers.put("CamelLinkedIn.comment_id", null);
        // parameter type is String
        headers.put("CamelLinkedIn.fields", null);

        final org.apache.camel.component.linkedin.api.model.Comment result = requestBodyAndHeaders("direct://GETCOMMENT", null, headers);

        assertNotNull("getComment result", result);
        LOG.debug("getComment: " + result);
    }

    // TODO provide parameter values for removeComment
    @Ignore
    @Test
    public void testRemoveComment() throws Exception {
        // using String message body for single parameter "comment_id"
        requestBody("direct://REMOVECOMMENT", null);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                // test route for getComment
                from("direct://GETCOMMENT")
                  .to("linkedin://" + PATH_PREFIX + "/getComment");

                // test route for removeComment
                from("direct://REMOVECOMMENT")
                  .to("linkedin://" + PATH_PREFIX + "/removeComment?inBody=comment_id");

            }
        };
    }
}