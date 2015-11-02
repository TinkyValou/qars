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
package fr.ensma.lias.qarscore.connection.statement;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;

import fr.ensma.lias.qarscore.InitTest;
import fr.ensma.lias.qarscore.SPARQLQueriesSample;

/**
 * @author Geraud FOKOU
 */
public class StatementTest extends InitTest{

    private QueryStatement queryStatement;

    /**
     */
    @Before
    public void setUp() {
	super.setUp();
   }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testJena() {
	
	queryStatement = sessionJena.createStatement(SPARQLQueriesSample.EDBT_QUERY_1);
	ResultSet result = (ResultSet) queryStatement.executeQuery();
	Assert.assertNotNull(result);
	int i = 0;
	while (result.hasNext()) {
	    QuerySolution solution = result.next();
	    for(String var:result.getResultVars()){
		 Logger.getRootLogger().info(
			    solution.get(var));
	    }
	    i++;
	}
	Logger.getRootLogger().info(i);

	
    }

    @Test
    public void testSesame() {
	
	queryStatement = sessionSesame.createStatement(SPARQLQueriesSample.QUERY_1);
	TupleQueryResult result = (TupleQueryResult) queryStatement.executeQuery();
	Assert.assertNotNull(result);
	int i = 0;
	while (result.hasNext()) {
	    BindingSet solution = result.next();
	    for(String var:result.getBindingNames()){
		 Logger.getRootLogger().info(
			    solution.getBinding(var));
	    }
	    i++;
	}
	Logger.getRootLogger().info(i);	
    }

}
