/*********************************************************************************
 * This file is part of QARS Project.
 * Copyright (C) 2015  LIAS - ENSMA
 *   Teleport 2 - 1 avenue Clement Ader
 *   BP 40109 - 86961 Futuroscope Chasseneuil Cedex - FRANCE
 * 
 * QARS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QARS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with QARS.  If not, see <http://www.gnu.org/licenses/>.
 **********************************************************************************/
package fr.ensma.lias.qarscore.connection;

import org.junit.Assert;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModelSpec;

import fr.ensma.lias.qarscore.properties.Properties;

/**
 * @author Geraud FOKOU
 */
public class SessionTest {

    /** URL of SDB database on postgres **/
    private final String POSTGRES_DB_URL = "jdbc:postgresql://localhost:5432/";
    
    /** User credentials */
    private final String POSTGRES_DB_USER = "postgres";
    private final String POSTGRES_DB_PASSWORD = "psql";


    @Test
    public void testSessionTDB() {

	Properties.setModelMemSpec(OntModelSpec.OWL_MEM);
	Properties.setOntoLang("OWL");
	
	Session session = SessionFactory.getTDBSession("LUBM100");

	Assert.assertNotNull(session.getDataset());
	Assert.assertNotNull(session.getDataModel());
	Assert.assertNotNull(session.getOntologyModel());
	Assert.assertNull(session.getDataStore());
	
    }

    @Test
    public void testSessionSDB() {
	
	Properties.setModelMemSpec(OntModelSpec.OWL_MEM);
	Properties.setOntoLang("OWL");
	
	Session session = SessionFactory.getSDBSession(POSTGRES_DB_URL, POSTGRES_DB_USER, POSTGRES_DB_PASSWORD, "LUBM1");

	Assert.assertNotNull(session.getDataset());
	Assert.assertNotNull(session.getDataModel());
	Assert.assertNotNull(session.getOntologyModel());
	Assert.assertNull(session.getDataStore());
	
    }

}
