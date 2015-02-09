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
package fr.ensma.lias.qarscore.engine.query;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.expr.E_LogicalAnd;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;

import fr.ensma.lias.qarscore.exception.NotYetImplementedException;

/**
 * @author Geraud FOKOU
 */
public class CQueryFactory {

    /**
     * List of Simple literal create
     */
    private static List<CElement> elementList;

    /**
     * List of group element
     */
    private static List<ElementGroup> groupList;

    /**
     * extract clause in an element of SPARQL query
     * 
     * @param element
     * @throws NotYetImplementedException
     */
    private static void getClause(Element element) throws NotYetImplementedException {

	if (element instanceof ElementPathBlock) {
	    List<TriplePath> triplePathList = ((ElementPathBlock) element)
		    .getPattern().getList();

	    for (TriplePath triplePath : triplePathList) {
		ElementPathBlock currentTripleElt = new ElementPathBlock();
		currentTripleElt.addTriplePath(triplePath);
		elementList.add(CElement.createCTriple(currentTripleElt));
	    }
	}

	else if (element instanceof ElementFilter) {

	    Expr expression = ((ElementFilter) element).getExpr();
	    getClause(expression);
	}

	else if (element instanceof ElementGroup) {

	    for (Element elementInGroup : ((ElementGroup) element)
		    .getElements()) {
		if (elementInGroup instanceof ElementGroup) {
		    groupList.add((ElementGroup) elementInGroup);
		} else {
		    getClause(elementInGroup);
		}
	    }
	}

	else {
	    throw new NotYetImplementedException(
		    "This Element type don't support by the API");
	}
    }

    /**
     * Extract clause in an expression of SPARQL query
     * 
     * @param expression
     * @throws NotYetImplementedException
     */
    private static void getClause(Expr expression) throws NotYetImplementedException {

	if (expression instanceof E_LogicalAnd) {
	    ElementFilter currentFilterElt = new ElementFilter(
		    ((E_LogicalAnd) expression).getArg1());
	    elementList.add(CElement.createCTriple(currentFilterElt));

	    currentFilterElt = new ElementFilter(
		    ((E_LogicalAnd) expression).getArg2());
	    elementList.add(CElement.createCTriple(currentFilterElt));
	} else {
	    ElementFilter currentFilterElt = new ElementFilter(expression);
	    elementList.add(CElement.createCTriple(currentFilterElt));
	}
    }

    /**
     * For a SPARQL Query query creates the corresponding CQuery
     * 
     * @param query
     * @return
     * @throws NotYetImplementedException
     */
    public static CQuery createCQuery(Query query) throws NotYetImplementedException {

	groupList = new ArrayList<ElementGroup>();
	elementList = new ArrayList<CElement>();
	groupList.add((ElementGroup) query.getQueryPattern());

	for (int i = 0; i < groupList.size(); i++) {
	    getClause(groupList.get(i));
	}

	List<Node> selectedQueryVar = new ArrayList<Node>();
	selectedQueryVar.addAll(query.getProjectVars());

	return CQuery.createCQuery(elementList, groupList, selectedQueryVar);
    }

    public static CQuery create(String query) throws NotYetImplementedException {
	
	return CQuery.createCQuery(createCQuery(QueryFactory.create(query)));
    }

}
