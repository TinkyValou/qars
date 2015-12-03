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
package fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.implementation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.MFSSearch;

/**
 * @author Geraud FOKOU
 */
public abstract class AbstractLatticeStrategy implements MFSSearch {

    public int number_of_query_executed = 0;
    public int number_of_query_reexecuted = 0;
    public int size_of_cartesian_product = 0;
    public long duration_of_execution = 0;

    protected Logger logger = Logger.getLogger(AbstractLatticeStrategy.class);
    protected CQuery CURRENT_CONJUNCTIVE_QUERY;
    protected List<CQuery> MFS_CURRENT_QUERY;
    protected List<CQuery> XSS_CURRENT_QUERY;
    protected CQuery actualQuery;
    protected List<CQuery> failingCauses;
    protected List<CQuery> maximalSubqueries;

    /**
     * 
     */
    public AbstractLatticeStrategy() {
	super();
	this.logger_init();
    }

    /**
     * Computes all the MFS and XSS of a CQuery query In comment we can put old
     * PXSS at the end of the list of PXSS Or like in the initial case we can
     * insert new PXSS at the END due to this choice the efficiency of the
     * algorithm could change?
     * 
     * @param query
     */
    protected void computeMFS(CQuery query) {

	/**
	 * log the current query
	 */
	for (int i = 0; i < query.getElementList().size(); i++) {
	    logger.info(query.getElementList().get(i).getElement().toString()
		    + "-->" + query.getElementList().get(i).getLabel());
	}

	failingCauses = new ArrayList<CQuery>();
	maximalSubqueries = new ArrayList<CQuery>();

	if (!query.isValidQuery()) {
	    return;
	}

	/**
	 * If you aren't sure that query is an empty query
	 */
	CQuery anCause = getOneMFS(query);
	/**
	 * if you are sure that query is an empty query
	 */
	// CQuery anCause = getFirstOneMFS(query);

	if (anCause.getElementList().isEmpty()) {
	    maximalSubqueries.add(CQueryFactory.cloneCQuery(query));
	    return;
	}

	failingCauses.add(anCause);

	ArrayList<CQuery> potentialsMaximalSubqueries = new ArrayList<CQuery>();
	for (CElement elt : failingCauses.get(failingCauses.size() - 1)
		.getElementList()) {
	    CQuery tempquery = CQueryFactory.cloneCQuery(query);
	    tempquery.getElementList().remove(elt);
	    potentialsMaximalSubqueries.add(tempquery);
	}

	while (potentialsMaximalSubqueries.size() != 0) {

	    CQuery tempquery = potentialsMaximalSubqueries.get(0);

	    if (!tempquery.isValidQuery()) {
		potentialsMaximalSubqueries.remove(0);
		continue;
	    }

	    anCause = getOneMFS(tempquery);

	    if (anCause.getElementList().isEmpty()) {
		ArrayList<CQuery> oldMaximalSubqueries = potentialsMaximalSubqueries;
		potentialsMaximalSubqueries = new ArrayList<CQuery>();
		potentialsMaximalSubqueries.addAll(oldMaximalSubqueries);
		for (CQuery pxss : oldMaximalSubqueries) {
		    if (pxss.isSubQueryOf(tempquery)) {
			potentialsMaximalSubqueries.remove(pxss);
		    }
		}
		boolean isContained = false;
		for (CQuery xss : maximalSubqueries) {
		    if (tempquery.isSubQueryOf(xss)) {
			isContained = true;
			break;
		    }
		}
		if (!isContained) {
		    maximalSubqueries.add(CQueryFactory.cloneCQuery(tempquery));
		}
		continue;
	    }

	    failingCauses.add(anCause);
	    ArrayList<CQuery> newMaximalSubqueries = new ArrayList<CQuery>();
	    ArrayList<CQuery> oldMaximalSubqueries = new ArrayList<CQuery>();

	    for (CQuery pxss : potentialsMaximalSubqueries) {
		if (pxss.getElementList().containsAll(
			failingCauses.get(failingCauses.size() - 1)
				.getElementList())) {
		    for (CElement elt : failingCauses.get(
			    failingCauses.size() - 1).getElementList()) {
			CQuery temp = CQueryFactory.cloneCQuery(pxss);
			temp.getElementList().remove(elt);
			newMaximalSubqueries.add(temp);
		    }
		} else {
		    oldMaximalSubqueries.add(pxss);
		}
	    }
	    // Ancienne PXSS avant les nouvelles
	    potentialsMaximalSubqueries = oldMaximalSubqueries;
	    potentialsMaximalSubqueries.addAll(newMaximalSubqueries);

	    // Nouvelles PXSS avant les anciennes
	    // potentialsMaximalSubqueries = newMaximalSubqueries;
	    // potentialsMaximalSubqueries.addAll(oldMaximalSubqueries);
	}
    }

    /**
     * Execute the query save in the strategy, the main query
     */
    @Override
    public boolean hasLeastKAnswers() {

	return this.hasLeastKAnswers(CURRENT_CONJUNCTIVE_QUERY);
    }

    @Override
    public boolean isMFS() {

	return this.isMFS(CURRENT_CONJUNCTIVE_QUERY);
    }

    @Override
    public CQuery getOneMFS() {

	return MFS_CURRENT_QUERY.get(0);
    }

    @Override
    public List<CQuery> getAllMFS() {

	return MFS_CURRENT_QUERY;
    }

    @Override
    public List<CQuery> getAllXSS() {

	return XSS_CURRENT_QUERY;
    }

    @Override
    abstract public boolean hasLeastKAnswers(CQuery query);

    @Override
    public boolean isMFS(CQuery query) {

	if (!query.isValidQuery()) {
	    return false;
	}

	if (hasLeastKAnswers(query)) {
	    return false;
	}

	for (CElement elt : query.getElementList()) {
	    CQuery tempQuery = CQueryFactory.cloneCQuery(query);
	    tempQuery.getElementList().remove(elt);
	    if (tempQuery.isValidQuery()) {
		if (!hasLeastKAnswers(tempQuery)) {
		    return false;
		}
	    }
	}
	return true;
    }

    @Override
    public CQuery getOneMFS(CQuery query) {

	// if (!query.isValidQuery()) {
	// return null;
	// }

	if (hasLeastKAnswers(query)) {
	    return CQueryFactory.createCQuery(new ArrayList<CElement>());
	}

	if (query.getElementList().size() == 1) {
	    return CQueryFactory.cloneCQuery(query);
	}

	List<CElement> causes = new ArrayList<CElement>();
	CQuery tempQuery = CQueryFactory.cloneCQuery(query);

	for (int i = 0; i < query.getElementList().size() - 1; i++) {
	    CElement elt = query.getElementList().get(i);
	    tempQuery.getElementList().remove(elt);
	    CQuery temp = CQueryFactory.cloneCQuery(tempQuery);
	    temp.getElementList().addAll(causes);
	    if (temp.isValidQuery()) {
		if (hasLeastKAnswers(temp)) {
		    causes.add(elt);
		}
	    }
	}

	CElement elt = query.getElementList().get(
		query.getElementList().size() - 1);

	if (causes.size() == 0) {
	    causes.add(elt);
	    return CQueryFactory.createCQuery(causes);
	}

	tempQuery.getElementList().remove(elt);
	CQuery temp = CQueryFactory.cloneCQuery(tempQuery);
	temp.getElementList().addAll(causes);
	if (temp.isValidQuery()) {
	    if (hasLeastKAnswers(temp)) {
		causes.add(elt);
	    }
	}
	return CQueryFactory.createCQuery(causes);
    }

    /**
     * Special get one MFS for a query which we are sure that it fails so we
     * don't execute the first hasLeastKAnswers
     * 
     * @param query
     * @return
     */
    public CQuery getFirstOneMFS(CQuery query) {

	if (query.getElementList().size() == 1) {
	    return CQueryFactory.cloneCQuery(query);
	}

	List<CElement> causes = new ArrayList<CElement>();
	CQuery tempQuery = CQueryFactory.cloneCQuery(query);

	for (int i = 0; i < query.getElementList().size() - 1; i++) {
	    CElement elt = query.getElementList().get(i);
	    tempQuery.getElementList().remove(elt);
	    CQuery temp = CQueryFactory.cloneCQuery(tempQuery);
	    temp.getElementList().addAll(causes);
	    if (temp.isValidQuery()) {
		if (hasLeastKAnswers(temp)) {
		    causes.add(elt);
		}
	    }
	}

	CElement elt = query.getElementList().get(
		query.getElementList().size() - 1);

	if (causes.size() == 0) {
	    causes.add(elt);
	    return CQueryFactory.createCQuery(causes);
	}

	tempQuery.getElementList().remove(elt);
	CQuery temp = CQueryFactory.cloneCQuery(tempQuery);
	temp.getElementList().addAll(causes);
	if (temp.isValidQuery()) {
	    if (hasLeastKAnswers(temp)) {
		causes.add(elt);
	    }
	}
	return CQueryFactory.createCQuery(causes);
    }

    @Override
    public List<CQuery> getAllMFS(CQuery query) {
	this.actualQuery = query;
	number_of_query_executed = 0;
	number_of_query_reexecuted = 0;
	size_of_cartesian_product = 0;
	duration_of_execution = System.currentTimeMillis();
	this.computeMFS(query);
	duration_of_execution = System.currentTimeMillis()
		- duration_of_execution;
	return failingCauses;
    }

    @Override
    public List<CQuery> getAllXSS(CQuery query) {

	if (this.actualQuery == query) {
	    return maximalSubqueries;
	} else {
	    this.actualQuery = query;
	    this.computeMFS(query);
	    return maximalSubqueries;
	}
    }

    protected void logger_init() {

	LocalDateTime time = LocalDateTime.now();
	String time_value = "" + time.getDayOfMonth() + time.getMonthValue()
		+ time.getHour() + time.getMinute() + time.getSecond();

	String logfile = "mfsSearch-Process" + "-" + time_value + ".log";

	PatternLayout layout = new PatternLayout();
	String conversionPattern = "%-5p [%C{1}]: %m%n";
	layout.setConversionPattern(conversionPattern);

	FileAppender fileAppender = new FileAppender();
	fileAppender.setFile(logfile);
	fileAppender.setLayout(layout);
	fileAppender.activateOptions();
	logger.removeAllAppenders();
	logger.addAppender(fileAppender);
    }
}