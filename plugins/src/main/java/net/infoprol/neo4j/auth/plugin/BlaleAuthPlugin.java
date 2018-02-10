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

package net.infoprol.neo4j.auth.plugin;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

import org.neo4j.server.security.enterprise.auth.plugin.api.AuthProviderOperations;
import org.neo4j.server.security.enterprise.auth.plugin.api.AuthToken;
import org.neo4j.server.security.enterprise.auth.plugin.api.AuthenticationException;
import org.neo4j.server.security.enterprise.auth.plugin.api.PredefinedRoles;
import org.neo4j.server.security.enterprise.auth.plugin.spi.AuthInfo;
import org.neo4j.server.security.enterprise.auth.plugin.spi.AuthPlugin;



public class BlaleAuthPlugin extends AuthPlugin.Adapter
{
    private AuthProviderOperations api;
    private String superuser;

    @Override
    public AuthInfo authenticateAndAuthorize( AuthToken authToken ) throws AuthenticationException
    {
        String username = authToken.principal();
        char[] password = authToken.credentials();

        api.log().info( "LOGIN attempt by '" + username + "'." );

        if ( username == null ) return null;

        if ( username.equals( superuser ) )
        {
            api.log().info( "SUPERUSER LOGIN SUCESS:  '" + superuser + "'." );
            return AuthInfo.of( superuser, Collections.singleton( PredefinedRoles.ADMIN ) );
        }

        return null;
    }

    @Override
    public void initialize( AuthProviderOperations authProviderOperations )
    {
        api = authProviderOperations;
        api.log().info( "initialized! ");

        loadConfig();
    }

    private void loadConfig()
    {
        Path configFile = resolveConfigFilePath();
        Properties properties = loadProperties( configFile );

        String superuser = properties.getProperty( "superuser" );
        this.superuser = superuser;
        api.log().info( "set SUPERUSER to '" + superuser + "'." );
    }

    private Path resolveConfigFilePath()
    {
        return api.neo4jHome().resolve( "conf/BlaleAuth.conf" );
    }

    private Properties loadProperties( Path configFile )
    {
        Properties properties = new Properties();

        try
        {
            InputStream inputStream = new FileInputStream( configFile.toFile() );
            properties.load( inputStream );
        }
        catch ( IOException e )
        {
            api.log().error( "failed to load config file '" + configFile.toString() + "'...  defaulting to the lion..." );
            properties.setProperty( "superuser", "geezus" );
        }

        return properties;
    }
}















