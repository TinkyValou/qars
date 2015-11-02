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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.ReasonerRegistry;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.statement.QueryStatement;
import fr.ensma.lias.qarscore.connection.statement.QueryStatementFactory;
import fr.ensma.lias.qarscore.exception.NotYetImplementedException;
import fr.ensma.lias.qarscore.properties.Properties;

/**
 * @author Geraud FOKOU
 */
public abstract class JenaSession implements Session {

    /**
     * Only one session is allowed for an instance of the program
     */
    protected static Session session;

    /**
     * Dataset use for querying
     */
    protected Dataset dataset;

    /**
     * model of semantic data
     */
    protected Model model;
    
    /**
     * Ontology of the data
     */
    protected OntModel ontology;

    /**
     * Statistics about data in the session
     */
    protected Map<Resource, Double> information_content;

    /**
     * @return the information_content
     */
    public Map<Resource, Double> getInformation_content() {
	return information_content;
    }

    /**
     * The number of instance of one class
     * 
     * @param classe
     * @return
     */
    protected double getInstanceNumber(OntClass classe) {

	int number = classe.listInstances(true).toList().size();
	List<OntClass> subclasses = classe.listSubClasses(true).toList();

	while (!subclasses.isEmpty()) {

	    OntClass currentClass = subclasses.get(0);
	    subclasses.remove(currentClass);
	    if (currentClass.isIntersectionClass()) {
		continue;
	    }
	    if (currentClass.isRestriction()) {
		continue;
	    }

	    number = number + currentClass.listInstances(true).toList().size();
	    subclasses.addAll(currentClass.listSubClasses(true).toList());
	}
	return number;
    }

    /**
     * The number of instance of one property
     * 
     * @param property
     * @return
     */
    protected double getInstanceNumber(OntProperty property) {
	
	int number = this.ontology.listResourcesWithProperty(property, null).toList().size();
	return number;
    }

    protected void set_model(){
	
	/**
	 * Return a prebuilt standard configuration for the default RDFS reasoner
	 */
	if (Properties.getModelMemSpec().equals(OntModelSpec.OWL_MEM_RDFS_INF)) {
	    model = ModelFactory.createInfModel(
		    ReasonerRegistry.getRDFSReasoner(),
		    dataset.getDefaultModel());
	    ontology = ModelFactory.createOntologyModel(
		    OntModelSpec.OWL_MEM_RDFS_INF, model);
	}

	/**
	 * Return a prebuilt standard configuration for the default subclass/subproperty transitive closure reasoner.
	 */
	else if (Properties.getModelMemSpec().equals(
		OntModelSpec.OWL_MEM_TRANS_INF)) {
	    model = ModelFactory.createInfModel(
		    ReasonerRegistry.getTransitiveReasoner(),
		    dataset.getDefaultModel());
	    ontology = ModelFactory.createOntologyModel(
		    OntModelSpec.OWL_MEM_TRANS_INF, model);
	}

	/**
	 * Default model without inferred triple 
	 */
	else if (Properties.getModelMemSpec().equals(
		OntModelSpec.OWL_MEM)) {
	    model = dataset.getDefaultModel();
	    ontology = ModelFactory.createOntologyModel(
		    OntModelSpec.OWL_MEM, model);
	}
	/**
	 * Default model without inferred triple 
	 */
	else if (Properties.getModelMemSpec().equals(
		OntModelSpec.OWL_DL_MEM)) {
	    model = dataset.getDefaultModel();
	    ontology = ModelFactory.createOntologyModel(
		    OntModelSpec.OWL_DL_MEM, model);
	}

	/**
	 * Prebuilt standard configuration for the default OWL reasoner.
	 */
	else if (Properties.getModelMemSpec().equals(
		OntModelSpec.OWL_DL_MEM_RULE_INF)) {
	    model = ModelFactory.createInfModel(
		    ReasonerRegistry.getOWLReasoner(),
		    dataset.getDefaultModel());
	    ontology = ModelFactory.createOntologyModel(
		    OntModelSpec.OWL_DL_MEM_RULE_INF, model);
	}

	/**
	 * Prebuilt standard configuration for the default OWL reasoner.
	 */
	else if (Properties.getModelMemSpec().equals(
		OntModelSpec.OWL_DL_MEM_RDFS_INF)) {
	    model = ModelFactory.createInfModel(
		    ReasonerRegistry.getRDFSReasoner(),
		    dataset.getDefaultModel());
	    ontology = ModelFactory.createOntologyModel(
		    OntModelSpec.OWL_DL_MEM_RULE_INF, model);
	}

	else if (Properties.getModelMemSpec().equals(
		OntModelSpec.RDFS_MEM)) {
	    model = dataset.getDefaultModel();
	    ontology = ModelFactory.createOntologyModel(
		    OntModelSpec.RDFS_MEM, model);
	}
	/**
	 * Return a prebuilt standard configuration for the default RDFS reasoner
	 */
	else if (Properties.getModelMemSpec().equals(
		OntModelSpec.RDFS_MEM_RDFS_INF)) {
	    model = ModelFactory.createInfModel(
		    ReasonerRegistry.getRDFSReasoner(),
		    dataset.getDefaultModel());
	    ontology = ModelFactory.createOntologyModel(
		    OntModelSpec.RDFS_MEM_RDFS_INF, model);
	}

	/**
	 * Return a prebuilt standard configuration for the default subclass/subproperty transitive closure reasoner.
	 */
	else if (Properties.getModelMemSpec().equals(
		OntModelSpec.RDFS_MEM_TRANS_INF)) {
	    model = ModelFactory.createInfModel(
		    ReasonerRegistry.getTransitiveReasoner(),
		    dataset.getDefaultModel());
	    ontology = ModelFactory.createOntologyModel(
		    OntModelSpec.RDFS_MEM_TRANS_INF, model);
	}
	
	else {
	    throw new NotYetImplementedException("unknow ontology specification");
	}

    }
    
    public Dataset getDataset() {
	return dataset;
    }

    public Model getModel() {
	return model;
    }

    public OntModel getOntology() {
	return ontology;
    }

    /**
     * Return all the triple of the ontology for the data set in the SDB Triple
     * store
     */
    public List<Triple> getTripleList() {

	List<Triple> allTriple = new ArrayList<Triple>();

	StmtIterator tripleIterator = this.getModel().listStatements();
	while (tripleIterator.hasNext()) {
	    allTriple.add(tripleIterator.next().asTriple());
	}
	return allTriple;
    }

    /**
     * Return the ontology and its instance in a JSON format
     */
    public String getOntoJSON() {

	//JenaJSONLD.init();
	StringWriter writer = new StringWriter();
	getModel().write(writer, "JSON-LD");

	return writer.toString();
    }

    @Override
    public QueryStatement createStatement(String query) {
	return QueryStatementFactory.createQueryStatement(query, session);
    }

    @Override
    public void close() {
	model.close();
	ontology.close();
    }

    @Override
    public boolean isclose() {
	return model.isClosed();
    }

    @Override
    public void open() {
	model.begin();
	ontology.begin();
    }

    @Override
    public boolean isopen() {
	return !model.isClosed();
    }

    @Override
    public double similarityMeasureClass(Node original_node, Node relaxed_node) {

	if(original_node.equals(relaxed_node)){
	    return 1.0;
	}

	OntClass original_class = null;
	OntClass relaxed_class = null;

	if (relaxed_node.isVariable()) {
	    return 0.0;
	}

	if (original_node.isURI()) {
	    original_class = this.getOntology().getOntClass(
		    original_node.getURI());
	}

	if (original_class == null) {
	    return literalOrValueMeasure(original_node, relaxed_node);
	}

	if (relaxed_node.isURI()) {
	    relaxed_class = this.getOntology().getOntClass(
		    relaxed_node.getURI());
	}

	return conceptMeasure(original_class, relaxed_class);
    }

    @Override
    public double similarityMeasureProperty(Node original_node, Node relaxed_node) {

	if(original_node.equals(relaxed_node)){
	    return 1.0;
	}

	OntProperty original_property = null;
	OntProperty relaxed_property = null;

	if (relaxed_node.isVariable()) {
	    return 0.0;
	}

	if (original_node.isURI()) {
	    original_property = this.getOntology().getOntProperty(original_node.getURI());	   
	}

	if (original_property == null) {
	    return literalOrValueMeasure(original_node, relaxed_node);
	}

	if (relaxed_node.isURI()) {
	    relaxed_property = this.getOntology().getOntProperty(
		    relaxed_node.getURI());
	}

	return propertyMeasure(original_property, relaxed_property);
    }

    private double conceptMeasure(OntClass original_class,
	    OntClass relaxed_class) {

	if ((!original_class.isClass()) || (!original_class.isURIResource())) {
	    return -1;
	}
	if ((!relaxed_class.isClass()) || (!relaxed_class.isURIResource())) {
	    return -1;
	}

	OntClass least_common_class = getLeastCommonClassAncestor(original_class,
		relaxed_class);

	if (least_common_class == null) {
	    return 0;
	}

	double ic_lcc = 0;
	
	ic_lcc = information_content.get(least_common_class);
	
	double ic_class1 = 0;
	double ic_class2 = 0;
	ic_class1 = information_content.get(original_class);
	ic_class2 = information_content.get(relaxed_class);

	return ic_lcc / (ic_class1 + ic_class2 - ic_lcc);

    }

    private double propertyMeasure(OntProperty original_property,
	    OntProperty relaxed_property) {
	
	if (!original_property.isProperty()) {
	    return -1;
	}
	if (!relaxed_property.isProperty()) {
	    return -1;
	}

	OntProperty least_common_Property = getLeastCommonPropertyAncestor(original_property,
		relaxed_property);

	if (least_common_Property == null) {
	    return 0;
	}

	double ic_lcp = 0;
	
	ic_lcp = information_content.get(least_common_Property);
	
	double ic_prop1 = 0;
	double ic_prop2 = 0;
	ic_prop1 = information_content.get(original_property);
	ic_prop2 = information_content.get(relaxed_property);

	return ic_lcp / (ic_prop1 + ic_prop2 - ic_lcp);

    }

    /**
     * 
     * @param original_class
     * @param relaxed_class
     * @return
     */
    private OntClass getLeastCommonClassAncestor(OntClass original_class,
	    OntClass relaxed_class) {

	List<OntClass> super_class_1 = original_class.listSuperClasses(true)
		.toList();
	List<OntClass> super_class_2 = relaxed_class.listSuperClasses(true)
		.toList();
	List<OntClass> new_super_class1 = new ArrayList<OntClass>();
	List<OntClass> new_super_class2 = new ArrayList<OntClass>();

	for (OntClass class_of_super_class_1 : super_class_1) {
	    for (OntClass class_of_super_class_2 : super_class_2) {
		new_super_class2.addAll(class_of_super_class_2.listSuperClasses(
			true).toList());
		/*
		 * if ((classe1_super_class.equals(relaxed_class)) ||
		 * (classe2_super_class.equals(original_class))) { continue; }
		 */
		if (class_of_super_class_1.equals(class_of_super_class_2)) {
		    return class_of_super_class_1;
		}
	    }
	    new_super_class1.addAll(class_of_super_class_1.listSuperClasses(true)
		    .toList());
	}

	while ((!new_super_class1.isEmpty()) || (!new_super_class2.isEmpty())) {

	    for (OntClass class_of_new_super_class_1 : new_super_class1) {
		for (OntClass class_of_super_class_2 : super_class_2) {
		    /*
		     * if ((classe1_super_class.equals(relaxed_class)) ||
		     * (classe2_super_class.equals(original_class))) { continue;
		     * }
		     */
		    if (class_of_new_super_class_1.equals(class_of_super_class_2)) {
			return class_of_new_super_class_1;
		    }
		}
	    }

	    for (OntClass class_of_new_super_class_2 : new_super_class2) {
		for (OntClass class_of_super_class_1 : super_class_1) {
		    /*
		     * if ((classe1_super_class.equals(relaxed_class)) ||
		     * (classe2_super_class.equals(original_class))) { continue;
		     * }
		     */
		    if (class_of_super_class_1.equals(class_of_new_super_class_2)) {
			return class_of_super_class_1;
		    }
		}
	    }

	    List<OntClass> temp_super_class1 = new ArrayList<OntClass>();
	    List<OntClass> temp_super_class2 = new ArrayList<OntClass>();

	    for (OntClass class_of_new_super_class_1 : new_super_class1) {
		for (OntClass class_of_new_super_class_2 : new_super_class2) {
		    temp_super_class2.addAll(class_of_new_super_class_2
			    .listSuperClasses(true).toList());
		    /*
		     * if ((classe1_super_class.equals(relaxed_class)) ||
		     * (classe2_super_class.equals(original_class))) { continue;
		     * }
		     */
		    if (class_of_new_super_class_1.equals(class_of_new_super_class_2)) {
			return class_of_new_super_class_1;
		    }
		}
		temp_super_class1.addAll(class_of_new_super_class_1.listSuperClasses(
			true).toList());
	    }

	    super_class_1.addAll(new_super_class1);
	    super_class_2.addAll(new_super_class2);
	    new_super_class1.clear();
	    new_super_class2.clear();
	    new_super_class1.addAll(temp_super_class1);
	    new_super_class2.addAll(temp_super_class2);
	}

	return null;
    }

    /**
     * 
     * @param original_property
     * @param relaxed_property
     * @return
     */
    private OntProperty getLeastCommonPropertyAncestor(OntProperty original_property,
	    OntProperty relaxed_property) {

	List<OntProperty> super_property_1 = new ArrayList<OntProperty>();
	List<OntProperty> super_property_2 = new ArrayList<OntProperty>();

	super_property_1.addAll(original_property.listSuperProperties(true).toList());
	super_property_2.addAll(relaxed_property.listSuperProperties(true).toList());
	
	
	List<OntProperty> new_super_property_1 = new ArrayList<OntProperty>();
	List<OntProperty> new_super_property_2 = new ArrayList<OntProperty>();

	for (OntProperty property_of_super_property_1 : super_property_1) {
	    for (OntProperty property_of_super_property_2 : super_property_2) {
		new_super_property_2.addAll(property_of_super_property_2.listSuperProperties(true).toList());
		/*
		 * if ((classe1_super_class.equals(relaxed_class)) ||
		 * (classe2_super_class.equals(original_class))) { continue; }
		 */
		if (property_of_super_property_1.equals(property_of_super_property_2)) {
		    return property_of_super_property_1;
		}
	    }
	    new_super_property_1.addAll(property_of_super_property_1.listSuperProperties(true).toList());
	}

	while ((!new_super_property_1.isEmpty()) || (!new_super_property_2.isEmpty())) {

	    for (OntProperty property_of_new_super_property_1 : new_super_property_1) {
		for (OntProperty property_of_super_property_2 : super_property_2) {
		    /*
		     * if ((classe1_super_class.equals(relaxed_class)) ||
		     * (classe2_super_class.equals(original_class))) { continue;
		     * }
		     */
		    if (property_of_new_super_property_1.equals(property_of_super_property_2)) {
			return property_of_new_super_property_1;
		    }
		}
	    }

	    for (OntProperty property_of_new_super_property_2 : new_super_property_2) {
		for (OntProperty property_of_super_property_1 : super_property_1) {
		    /*
		     * if ((classe1_super_class.equals(relaxed_class)) ||
		     * (classe2_super_class.equals(original_class))) { continue;
		     * }
		     */
		    if (property_of_super_property_1.equals(property_of_new_super_property_2)) {
			return property_of_super_property_1;
		    }
		}
	    }

	    List<OntProperty> temp_super_class1 = new ArrayList<OntProperty>();
	    List<OntProperty> temp_super_class2 = new ArrayList<OntProperty>();

	    for (OntProperty property_of_new_super_property_1 : new_super_property_1) {
		for (OntProperty property_of_new_super_property_2 : new_super_property_2) {
		    temp_super_class2.addAll(property_of_new_super_property_2.listSuperProperties(true).toList());
		    /*
		     * if ((classe1_super_class.equals(relaxed_class)) ||
		     * (classe2_super_class.equals(original_class))) { continue;
		     * }
		     */
		    if (property_of_new_super_property_1.equals(property_of_new_super_property_2)) {
			return property_of_new_super_property_1;
		    }
		}
		temp_super_class1.addAll(property_of_new_super_property_1.listSuperProperties(true).toList());
	    }

	    super_property_1.addAll(new_super_property_1);
	    super_property_2.addAll(new_super_property_2);
	    new_super_property_1.clear();
	    new_super_property_2.clear();
	    new_super_property_1.addAll(temp_super_class1);
	    new_super_property_2.addAll(temp_super_class2);
	}

	return null;
    }

    private double literalOrValueMeasure(Node original_node, Node relaxed_node) {
	// TODO Auto-generated method stub

	return 0.0;
    }

}
