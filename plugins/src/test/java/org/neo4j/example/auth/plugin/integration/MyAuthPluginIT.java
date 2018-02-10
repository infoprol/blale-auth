/**
 * Copyright (c) 2002-2017 "Neo Technology,"
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
package org.neo4j.example.auth.plugin.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.harness.ServerControls;
import org.neo4j.harness.internal.EnterpriseInProcessServerBuilder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertTrue;

public class MyAuthPluginIT
{
    private ServerControls server;

    @Before
    public void setUp() throws Exception
    {
        // Start up server with authentication enables
        server = new EnterpriseInProcessServerBuilder()
                .withConfig( "dbms.security.auth_enabled", "true" )
                .withConfig( "dbms.security.auth_provider", "plugin-org.neo4j.example.auth.plugin.MyAuthPlugin" )
                .newServer();
    }

    @After
    public void tearDown() throws Exception
    {
        server.close();
    }

    @Test
    public void shouldAuthenticateNeo4jUser() throws Throwable
    {
        // When & Then
        try( Driver driver = GraphDatabase.driver( server.boltURI(),
             AuthTokens.basic( "neo4j", "neo4j" ) );
             Session session = driver.session() )
        {
            Value single = session.run( "RETURN 1" ).single().get( 0 );
            assertThat( single.asLong(), equalTo( 1L ) );
        }
    }

    @Test
    public void shouldAuthenticateAndAuthorizeKalleMoraeusAsAdmin() throws Exception
    {
        Driver driver = GraphDatabase.driver( server.boltURI(), AuthTokens.basic( "moraeus", "suearom" ) );
        Session session = driver.session();

        session.run( "CREATE (a:Person {name:'Kalle Moraeus', title:'Riksspelman'})" );

        StatementResult result =
                session.run( "MATCH (a:Person) WHERE a.name = 'Kalle Moraeus' RETURN a.name AS name, a.title AS title" );
        assertTrue( result.hasNext() );
        while ( result.hasNext() )
        {
            Record record = result.next();
            assertThat( record.get( "name" ).asString(), equalTo( "Kalle Moraeus" ) );
            assertThat( record.get( "title" ).asString(), equalTo( "Riksspelman" ) );
            System.out.println( record.get( "title" ).asString() + " " + record.get( "name" ).asString() );
        }

        session.close();
        driver.close();
    }
}
