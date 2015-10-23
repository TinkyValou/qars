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

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.implementation.JenaSession;
import fr.ensma.lias.qarscore.connection.implementation.SesameSession;
import fr.ensma.lias.qarscore.connection.implementation.SessionJenaSDB;
import fr.ensma.lias.qarscore.connection.implementation.SessionJenaTDB;
import fr.ensma.lias.qarscore.connection.statement.implementation.JenaQueryStatement;
import fr.ensma.lias.qarscore.connection.statement.implementation.SesameQueryStatement;

/**
 * @author Geraud FOKOU
 */
public class QueryStatementFactory {


    public static QueryStatement createQueryStatement (String query, Session session){
	
	if((session instanceof SessionJenaSDB) || (session instanceof SessionJenaTDB)){
	    return new JenaQueryStatement(query, (JenaSession) session);
	}
	if(session instanceof SesameSession) {
	    return new SesameQueryStatement(query, (SesameSession) session);
	}
	return null;
    }
}
