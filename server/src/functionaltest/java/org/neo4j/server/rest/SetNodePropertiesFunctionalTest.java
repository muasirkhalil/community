/**
 * Copyright (c) 2002-2011 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.server.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.kernel.impl.annotations.Documented;
import org.neo4j.server.rest.domain.JsonHelper;
import org.neo4j.server.rest.domain.JsonParseException;
import org.neo4j.test.GraphDescription.Graph;
import org.neo4j.test.GraphDescription.NODE;
import org.neo4j.test.GraphDescription.PROP;

public class SetNodePropertiesFunctionalTest extends
        AbstractRestFunctionalTestBase
{

    /**
     * Update node properties.
     * 
     * This will replace all existing properties on the node with the new set
     * of attributes.
     */
    @Graph( "jim knows joe" )
    @Documented
    @Test
    public void shouldReturn204WhenPropertiesAreUpdated()
            throws JsonParseException
    {
        Node jim = data.get().get( "jim" );
        assertFalse( jim.hasProperty( "age" ) );
        gen.get().payload(
                JsonHelper.createJsonFrom( MapUtil.map( "age", "18" ) ) ).expectedStatus(
                204 ).put( getPropertiesUri( jim ) );
        assertTrue( jim.hasProperty( "age" ) );
    }

    @Test
    @Graph( "jim knows joe" )
    public void shouldReturn400WhenSendinIncompatibleJsonProperties()
            throws JsonParseException
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put( "jim", new HashMap<String, Object>() );
        gen.get().payload( JsonHelper.createJsonFrom( map ) ).expectedStatus(
                400 ).put( getPropertiesUri( data.get().get( "jim" ) ) );
    }

    @Test
    @Graph( "jim knows joe" )
    public void shouldReturn400WhenSendingCorruptJsonProperties()
    {
        JaxRsResponse response = RestRequest.req().put(
                getPropertiesUri( data.get().get( "jim" ) ),
                "this:::Is::notJSON}" );
        assertEquals( 400, response.getStatus() );
        response.close();
    }

    @Test
    @Graph( "jim knows joe" )
    public void shouldReturn404WhenPropertiesSentToANodeWhichDoesNotExist()
            throws JsonParseException
    {
        gen.get().payload(
                JsonHelper.createJsonFrom( MapUtil.map( "key", "val" ) ) ).expectedStatus(
                404 ).put( getDataUri() + "/node/12345/poperties" );
    }

    private URI getPropertyUri( Node node, String key ) throws Exception
    {
        return new URI( getPropertiesUri( node ) + "/" + key );
    }

    /**
     * Set property on node.
     * 
     * Setting different properties will retain the existing ones for this node.
     */
    @Documented
    @Graph( nodes = {@NODE(name="jim", properties={@PROP(key="foo2", value="bar2")})} )
    @Test
    public void shouldReturn204WhenPropertyIsSet() throws Exception
    {
        Node jim = data.get().get( "jim" );
        gen.get().payload( JsonHelper.createJsonFrom( "bar" ) ).expectedStatus(
                204 ).put( getPropertyUri( jim, "foo" ).toString() );
        assertTrue( jim.hasProperty( "foo" ) );
        assertTrue( jim.hasProperty( "foo2" ) );
    }

    /**
     * Property values can not be nested.
     * 
     * Nesting properties is not supported. You could for example store the
     * nested json as a string instead.
     */
    @Documented
    @Test
    public void shouldReturn400WhenSendinIncompatibleJsonProperty()
            throws Exception
    {
        gen.get().payload( "{\"foo\" : {\"bar\" : \"baz\"}}" ).expectedStatus(
                400 ).post( getDataUri() + "node/" );
    }

    @Test
    @Graph( "jim knows joe" )
    public void shouldReturn400WhenSendingCorruptJsonProperty()
            throws Exception
    {
        JaxRsResponse response = RestRequest.req().put(
                getPropertyUri( data.get().get( "jim" ), "foo" ),
                "this:::Is::notJSON}" );
        assertEquals( 400, response.getStatus() );
        response.close();
    }

    @Test
    @Graph( "jim knows joe" )
    public void shouldReturn404WhenPropertySentToANodeWhichDoesNotExist()
            throws Exception
    {
        JaxRsResponse response = RestRequest.req().put(
                getDataUri() + "/node/1234/foo",
                JsonHelper.createJsonFrom( "bar" ) );
        assertEquals( 404, response.getStatus() );
        response.close();
    }

}
