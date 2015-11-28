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

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lias.qarscore.InitTest;
import fr.ensma.lias.qarscore.connection.implementation.JenaSession;

/**
 * @author Geraud FOKOU
 */
public class SessionTDBTest extends InitTest {

    public Map<OntClass, Integer> class_instance;
    public Map<OntProperty, Integer> property_Triplet;
    public Logger logger = Logger.getLogger(SessionTDBTest.class);

    @Before
    public void setUp() {

	super.setUp();
	// Properties.setModelMemSpec(OntModelSpec.OWL_MEM);
	// Properties.setOntoLang("OWL");
	class_instance = new HashMap<OntClass, Integer>();
	property_Triplet = new HashMap<OntProperty, Integer>();
	// sessionJena = SessionFactory.getTDBSession(tdb_path);
    }

    @After
    public void tearDown() {
	try {
	    sessionJena.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    // @Test
    public void testSessionTDB() {

	Assert.assertNotNull(((JenaSession) sessionJena).getDataset());
	Assert.assertNotNull(((JenaSession) sessionJena).getModel());
	Assert.assertNotNull(((JenaSession) sessionJena).getOntology());
	Assert.assertTrue(((JenaSession) sessionJena).getTripleList().size() != 0);
	logger.info(((JenaSession) sessionJena).getDataset().toString());

	for (Resource classe : ((JenaSession) sessionJena)
		.getInformation_content().keySet()) {
	    logger.info(classe.getURI()
		    + " has information content "
		    + ((JenaSession) sessionJena).getInformation_content().get(
			    classe));
	}
    }

    @Test
    public void testOntologyTDB() {
	Assert.assertNotNull(((JenaSession) sessionJena).getDataset());
	Assert.assertNotNull(((JenaSession) sessionJena).getModel());
	OntModel ontology_1 = ((JenaSession) sessionJena).getOntology();
	OntModel ontology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, ontology_1.getBaseModel());
	Assert.assertNotNull(ontology);

	String rdf_prefix = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ";

	String instance_by_class_query = rdf_prefix
		+ " SELECT ?classe (COUNT(?instance) AS ?numberInstance)  "
		+ "WHERE {?instance rdf:type ?classe . }" + "GROUP BY ?classe "
		+ "ORDER BY ?numberInstance ";

	String triple_by_property_query = rdf_prefix
		+ " SELECT ?property (COUNT(?property) AS ?numberProperty)  "
		+ "WHERE { ?s ?property ?o  .} " + "GROUP BY ?property "
		+ "ORDER BY ?numberProperty ";

	QueryExecution qexec = QueryExecutionFactory.create(
		instance_by_class_query, ((JenaSession) sessionJena).getModel());
	ResultSet result = qexec.execSelect();
	while(result.hasNext()){
	    QuerySolution sol = result.next();
	    logger.info(sol.getResource("classe").getLocalName()+"--->"+sol.getLiteral("numberInstance").getInt());
	}
  
	int size_instance = ontology.listIndividuals().toList().size();
	logger.info("Number of Instances --->"+size_instance);
	
	qexec = QueryExecutionFactory.create(
		triple_by_property_query, ((JenaSession) sessionJena).getModel());
	result = qexec.execSelect();
	while(result.hasNext()){
	    QuerySolution sol = result.next();
	    logger.info(sol.getResource("property").getLocalName()+"--->"+sol.getLiteral("numberProperty").getInt());
	}

	int size_triple = ontology.listStatements().toList().size();
	logger.info("Number of Triple --->"+size_triple);
    }
}
