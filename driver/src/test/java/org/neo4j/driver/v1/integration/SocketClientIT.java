/**
 * Copyright (c) 2002-2015 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.neo4j.driver.v1.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.internal.connector.socket.SocketClient;
import org.neo4j.driver.internal.connector.socket.SocketResponseHandler;
import org.neo4j.driver.internal.logging.DevNullLogger;
import org.neo4j.driver.internal.messaging.InitMessage;
import org.neo4j.driver.internal.messaging.Message;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.exceptions.ClientException;
import org.neo4j.driver.v1.util.TestNeo4j;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class SocketClientIT
{
    @Rule
    public TestNeo4j neo4j = new TestNeo4j();

    private SocketClient client = null;

    @Before
    public void setup()
    {
        URI url = URI.create( neo4j.address() );
        client = new SocketClient( url.getHost(), url.getPort(), Config.defaultConfig(),
                new DevNullLogger() );
    }

    @After
    public void tearDown()
    {
        if( client != null )
        {
            client.stop();
        }
    }

    @Test
    public void shouldCloseConnectionWhenReceivingProtocolViolationError() throws Exception
    {
        // Given
        List<Message> messages = new ArrayList<>( 2 );
        messages.add( new InitMessage( "EvilClientV1_Hello" ) );
        messages.add( new InitMessage( "EvilClientV1_World" ) );

        SocketResponseHandler handler = mock( SocketResponseHandler.class );
        when( handler.protocolViolationErrorOccurred() ).thenReturn( true );
        when( handler.receivedResponses() ).thenReturn( 0, 1, 2 );
        when( handler.serverFailure() ).thenReturn(
                new ClientException( "Neo.ClientError.Request.InvalidFormat", "Hello, world!" ) );

        // When & Then
        client.start();
        try
        {
            client.send( messages, handler );
            fail( "The client should receive a protocol violation error" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof ClientException );
            assertThat( e.getMessage(), equalTo( "Hello, world!" ) );
        }

        assertThat( client.isOpen(), equalTo( false ) );
        verify( handler, times(1) ).protocolViolationErrorOccurred();
        verify( handler, times(1) ).receivedResponses();
    }
}