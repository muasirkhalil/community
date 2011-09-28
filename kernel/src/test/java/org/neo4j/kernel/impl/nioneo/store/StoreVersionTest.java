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

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.internal.matchers.StringContains.containsString;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.junit.Test;
import org.neo4j.kernel.CommonFactories;
import org.neo4j.kernel.IdGeneratorFactory;
import org.neo4j.kernel.impl.util.FileUtils;

public class StoreVersionTest
{
    @Test
    public void allStoresShouldHaveTheCurrentVersionIdentifier() throws IOException
    {
        File outputDir = new File( "target/var/" + StoreVersionTest.class.getSimpleName() );
        FileUtils.deleteRecursively( outputDir );
        assertTrue( outputDir.mkdirs() );
        String storeFileName = new File(outputDir, "neostore").getPath();

        HashMap config = new HashMap();
        config.put( IdGeneratorFactory.class, CommonFactories.defaultIdGeneratorFactory() );
        config.put( FileSystemAbstraction.class, CommonFactories.defaultFileSystemAbstraction() );
        config.put( "neo_store", storeFileName );

        NeoStore.createStore( storeFileName, config );
        NeoStore neoStore = new NeoStore( config );

        CommonAbstractStore[] stores = {
                neoStore.getNodeStore(),
                neoStore.getRelationshipStore(),
                neoStore.getRelationshipTypeStore(),
                neoStore.getPropertyStore(),
                neoStore.getPropertyStore().getIndexStore()
        };

        for ( CommonAbstractStore store : stores )
        {
            assertThat(store.getTypeAndVersionDescriptor(), containsString( CommonAbstractStore.ALL_STORES_VERSION ));
        }
    }
}