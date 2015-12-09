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
package fr.ensma.lias.qarscore.engine.relaxation.strategies;

import java.util.ArrayList;
import java.util.List;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.operators.TripleRelaxation;
import fr.ensma.lias.qarscore.engine.relaxation.utils.GraphRelaxationIndex;
import fr.ensma.lias.qarscore.engine.relaxation.utils.NodeRelaxed;

/**
 * @author Geraud FOKOU
 */
public class HuangRelaxationStrategy {
    
    public int number_check_queries = 0;
    protected Session session;
    protected CQuery query_to_relax;
    protected NodeRelaxed[][] relaxation_of_element;
    protected GraphRelaxationIndex relaxed_graph;
    protected List<GraphRelaxationIndex> relaxed_queries;
    protected List<GraphRelaxationIndex> already_relaxed_queries;
    protected CQuery current_relaxed_query;
    protected double current_similarity;
    protected List<int[]> current_level;

    public HuangRelaxationStrategy(CQuery query, Session s) {
	query_to_relax = query;
	session = s;
	relaxed_queries = new ArrayList<GraphRelaxationIndex>();
    }

    protected void triple_relaxation(int[] relaxation_limit_index, int[] relaxation_index) {

	relaxation_of_element = new NodeRelaxed[query_to_relax.getElementList()
		.size()][];
	for (int i = 0; i < query_to_relax.getElementList().size(); i++) {
	    CElement element = query_to_relax.getElementList().get(i);
	    relaxation_of_element[i] = (new TripleRelaxation(element, session,
		    TripleRelaxation.SIM_ORDER)).get_relaxed_node_list();
	    relaxation_limit_index[i] = relaxation_of_element[i].length;
	    relaxation_index[i] = 0;
	}
    }
    
    public void begin_relax_process() {

	int[] relaxation_limit_index = new int[query_to_relax.getElementList()
		.size()];
	int[] relaxation_index = new int[query_to_relax.getElementList().size()];

	already_relaxed_queries = new ArrayList<GraphRelaxationIndex>();
	triple_relaxation(relaxation_limit_index, relaxation_index);
	relaxed_graph = new GraphRelaxationIndex(relaxation_index,
		relaxation_limit_index, true);
	relaxed_queries.add(relaxed_graph);

    }

    public CQuery next() {

	List<CElement> elt_relaxed_query = new ArrayList<CElement>();
	if (this.relaxed_queries.isEmpty()) {
	    return null;
	}
	GraphRelaxationIndex current_graph = relaxed_queries.remove(0);
	
	this.current_similarity = 1.0;
	this.current_level = new ArrayList<int[]>();

	for (int i = 0; i < current_graph.getElement_index().length; i++) {

	    elt_relaxed_query.add(getRelaxedElement(i,
		    current_graph.getElement_index()[i]));
	    this.current_similarity = this.current_similarity
		    * relaxation_of_element[i][current_graph.getElement_index()[i]]
			    .getSimilarity();
	    this.current_level.add(relaxation_of_element[i][current_graph.getElement_index()[i]]
		    .getRelaxation_level());
	}
	
	for (int j = 0; j < current_graph.getChild_elt().length; j++) {
	    if (!alreadyRelaxed(current_graph.getChild_elt()[j])) {
		this.insert(current_graph.getChild_elt()[j]);
	    }
	}
	already_relaxed_queries.add(current_graph);
	current_relaxed_query = CQueryFactory.createCQuery(elt_relaxed_query, query_to_relax.getSelectedQueryVar());
	return current_relaxed_query;
    }

    private boolean alreadyRelaxed(GraphRelaxationIndex graphRelaxationIndex) {

	boolean found = false;
	int i = 0;
	while ((!found) && (i < this.already_relaxed_queries.size())) {
	    found = hasSameIndex(graphRelaxationIndex.getElement_index(),
		    already_relaxed_queries.get(i).getElement_index());
	    i = i + 1;
	}
	if (found) {
	    return found;
	}
	i = 0;
	while ((!found) && (i < this.relaxed_queries.size())) {
	    found = hasSameIndex(graphRelaxationIndex.getElement_index(),
		    relaxed_queries.get(i).getElement_index());
	    i = i + 1;
	}
	return found;
    }

    private boolean hasSameIndex(int[] element_index, int[] other_element_index) {

	if (element_index == other_element_index) {
	    return true;
	}

	if (element_index.length != other_element_index.length) {
	    return false;
	}
	
	boolean is_same = true;
	int i = 0;
	while ((is_same) && (i < other_element_index.length)) {
	    is_same = is_same && other_element_index[i] == element_index[i];
	    i = i + 1;
	}
	return is_same;
    }

    protected void insert(GraphRelaxationIndex child) {

	double current_child_similarity = 1.0;
	// int current_child_level = 0;
	for (int i = 0; i < child.getElement_index().length; i++) {

	    current_child_similarity = current_child_similarity
		    * relaxation_of_element[i][child.getElement_index()[i]]
			    .getSimilarity();
	    // current_child_level = current_child_level +
	    // relaxation_of_element[i][child.getElement_index()[i]].getRelaxation_level();
	}
	int pos = 0;
	int index = this.relaxed_queries.size()-1;
	boolean found_pos = false;
	while ((!found_pos) && (0 <= index)) {
	    double index_sim = 1.0;
	    GraphRelaxationIndex elt_index = relaxed_queries.get(index);
	    for (int i = 0; i < elt_index.getElement_index().length; i++) {
		index_sim = index_sim
			* relaxation_of_element[i][elt_index.getElement_index()[i]]
				.getSimilarity();
	    }
	    found_pos = current_child_similarity  <= index_sim ;
	    pos = index + 1;
	    index = index - 1;
	}
	if (found_pos) {
	    relaxed_queries.add(pos, child);
	} else {
	    relaxed_queries.add(0, child);
	}
    }

    protected CElement getRelaxedElement(int num_triple, int relaxation_rank) {

	CElement relax_element = CElement.createCTriple(query_to_relax
		.getElementList().get(0).getElement());
	relax_element = relax_element
		.replace_subject(relaxation_of_element[num_triple][relaxation_rank]
			.getNode_1());
	relax_element = relax_element
		.replace_predicat(relaxation_of_element[num_triple][relaxation_rank]
			.getNode_2());
	relax_element = relax_element
		.replace_object(relaxation_of_element[num_triple][relaxation_rank]
			.getNode_3());

	return relax_element;
    }

    public boolean hasNext() {

	return (!this.relaxed_queries.isEmpty());
    }

    /**
     * @return the current_similarity
     */
    public Double getCurrent_similarity() {
	return current_similarity;
    }

    /**
     * @return the current_level
     */
    public List<int[]> getCurrent_level() {
	return current_level;
    }

    /**
     * @return the query_to_relax
     */
    public CQuery getQuery_to_relax() {
	return query_to_relax;
    }

    /**
     * @return the current_relaxed_query
     */
    public CQuery getCurrent_relaxed_query() {
	return current_relaxed_query;
    }

    /**
     * @return the relaxed_graph
     */
    public GraphRelaxationIndex getRelaxed_graph() {
	return relaxed_graph;
    }
}
