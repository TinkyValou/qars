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
package fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine;

import java.util.List;

import fr.ensma.lias.qarscore.engine.query.CQuery;

/**
 * @author Geraud FOKOU
 */
public interface MFSSearch {

	// Methods for the Default CQuery uses with the RelaxationStrategies
	/**
	 * Says if the current Query has at least K answers in the dataset
	 * 
	 * @return
	 */
	boolean hasLeastKAnswers();

	/**
	 * Says if the current CQuery is a MFS or not
	 * 
	 * @return
	 */
	boolean isMFS();

	/**
	 * return a query failure cause
	 * 
	 * @return
	 */
	CQuery getOneMFS();

	/**
	 * Return the list of all the MFS of the CQuery previously compute
	 * 
	 * @return
	 */
	List<CQuery> getAllMFS();

	/**
	 * Return all the maximal success subqueries of a CQuery previously compute
	 * 
	 * @return
	 */
	List<CQuery> getAllXSS();

	// Methods for the subQueries of the Default CQuery uses with the
	// RelaxationStrategies

	/**
	 * Says if the CQuery query has at least K answers in the dataset
	 * 
	 * @param query
	 * @param k
	 * @return
	 */
	boolean hasLeastKAnswers(CQuery query);

	/**
	 * Says if a CQuery is a MFS or not
	 * 
	 * @param query
	 * @return
	 */
	boolean isMFS(CQuery query);

	/**
	 * return a query failure cause for the query
	 * 
	 * @return
	 */
	CQuery getOneMFS(CQuery query);

	/**
	 * Return the list of all the MFS of the CQuery query
	 * 
	 * @param query
	 * @return
	 */
	List<CQuery> getAllMFS(CQuery query);

	/**
	 * Return all the maximal success subqueries of a CQuery query
	 * 
	 * @param query
	 * @return
	 */
	List<CQuery> getAllXSS(CQuery query);

	/**
	 * Find if exist the rest of the MFS in the query
	 * 
	 * @param query
	 * @param part_mfs
	 * @return
	 */
	List<CQuery> getOtherMFS(CQuery query, List<CQuery> part_mfs);

	/**
	 * Find if exist the other part of the MFS which are super queries of one of
	 * sub_mfs_part
	 * 
	 * @param query
	 * @param part_mfs
	 * @param sub_mfs_part
	 * @return
	 */
	List<CQuery> getOtherMFS(CQuery query, List<CQuery> part_mfs, List<CQuery> sub_mfs_part);
}