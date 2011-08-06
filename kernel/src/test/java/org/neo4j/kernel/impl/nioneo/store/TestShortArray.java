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
package org.neo4j.kernel.impl.nioneo.store;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Array;

import org.junit.Test;
import org.neo4j.helpers.Pair;
import org.neo4j.kernel.impl.util.Bits;

public class TestShortArray
{
    private static final int DEFAULT_PAYLOAD_SIZE = 16;

    @Test
    public void canEncodeSomeSampleArrays() throws Exception
    {
        assertCanEncodeAndDecodeToSameValue( new boolean[] { true, false, true } );
        assertCanEncodeAndDecodeToSameValue( new byte[] { -1, -10, 43, 127, 0, 4, 2, 3, 56, 47, 67, 43 } );
        assertCanEncodeAndDecodeToSameValue( new short[] { 1,2,3,45,5,6,7 } );
        assertCanEncodeAndDecodeToSameValue( new int[] { 1,2,3,4,5,6,7 } );
        assertCanEncodeAndDecodeToSameValue( new long[] { 1,2,3,4,5,6,7 } );
        assertCanEncodeAndDecodeToSameValue( new float[] { 0.34f, 0.21f } );
    }

    private void assertCanEncodeAndDecodeToSameValue( Object value )
    {
        assertCanEncodeAndDecodeToSameValue( value, DEFAULT_PAYLOAD_SIZE );
    }
    
    private void assertCanEncodeAndDecodeToSameValue( Object value, int payloadSize )
    {
        Pair<long[], Integer> result = ShortArray.encode( value, DEFAULT_PAYLOAD_SIZE );
        Bits bits = new Bits( result.first() );
        assertArraysEquals( value, ShortArray.decode( result ) );
    }

    private void assertArraysEquals( Object value1, Object value2 )
    {
        assertEquals( value1.getClass().getComponentType(), value2.getClass().getComponentType() );
        int length1 = Array.getLength( value1 );
        int length2 = Array.getLength( value2 );
        assertEquals( length1, length2 );
        
        for ( int i = 0; i < length1; i++ )
        {
            Object item1 = Array.get( value1, i );
            Object item2 = Array.get( value2, i );
            assertEquals( item1, item2 );
        }
    }
}