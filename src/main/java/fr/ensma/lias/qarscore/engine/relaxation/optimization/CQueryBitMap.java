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
package fr.ensma.lias.qarscore.engine.relaxation.optimization;

import java.util.HashMap;
import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import fr.ensma.lias.qarscore.engine.query.CQuery;

/**
 * @author Geraud FOKOU
 */
public class CQueryBitMap implements CQueryIndexMap {

    private Map<CQuery, Integer> listCQuery;
    
    private RoaringBitmap values;
    
    
    /**
     * 
     */
    public CQueryBitMap(int size) {
	
	listCQuery = new HashMap<CQuery, Integer>(size);
	values = new RoaringBitmap();
    }

    @Override
    public boolean contains(CQuery query) {
	
	return listCQuery.containsKey(query);
    }

    @Override
    public Integer get(CQuery query) {
	
	Integer index = listCQuery.get(query);
	if(index != null){
	    return values.select(index);
	}
	return null;
    }

    @Override
    public void put(CQuery query, Integer numberAnswers) {
	
	if (!this.contains(query)){
	    listCQuery.put(query, values.getCardinality()+1);
	    values.add(numberAnswers);
	}
    }

}
