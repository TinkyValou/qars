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
package fr.ensma.lias.qarscore.connection.implementation;

import java.sql.Connection;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.sdb.store.StoreFactory;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.similaritymeasure.SimilarityMeasureConcept;
import fr.ensma.lias.qarscore.properties.Properties;

/**
 * @author Geraud FOKOU
 */
public class SessionSDB extends JenaSession {

    /**
     * Only for SDB Database
     */
    protected Store store;

    /**
     * Construct a SDB Session if there isn't existed
     */
    public static Session getSessionSDB(Connection connect) {
	if (session != null) {
	    return session;
	}
	session = new SessionSDB(connect);
	return session;
    }

    private SessionSDB(Connection connect) {

	SDBConnection connectSDB = SDBFactory.createConnection(connect);
	StoreDesc storeDesc = new StoreDesc(Properties.getSdbLayout(),
		Properties.getSdbSupportType());
	store = StoreFactory.create(storeDesc, connectSDB);

	dataset = SDBFactory.connectDataset(store);
	model = SDBFactory.connectDefaultModel(store);
	ontologyModel = ModelFactory.createOntologyModel(
		Properties.getModelMemSpec(), model);
	SimilarityMeasureConcept.get_concept_measure(this);
    }

    /**
     * For TDB session DataStore is null
     */
    @Override
    public Store getDataStore() {
	return store;
    }
}
