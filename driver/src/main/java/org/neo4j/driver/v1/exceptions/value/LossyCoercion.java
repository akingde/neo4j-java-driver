/**
 * Copyright (c) 2002-2016 "Neo Technology,"
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
package org.neo4j.driver.v1.exceptions.value;

import static java.lang.String.format;

public class LossyCoercion extends ValueException
{
    private static final long serialVersionUID = -6259981390929065201L;

    public LossyCoercion( String sourceTypeName, String destinationTypeName )
    {
        super( format( "Cannot coerce %s to %s without loosing precision", sourceTypeName, destinationTypeName ) );
    }

}