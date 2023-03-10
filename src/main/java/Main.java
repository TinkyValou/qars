import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.SessionFactory;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.MFSSearch;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.implementation.StrategyFactory;
import fr.ensma.lias.qarscore.loader.JenaBulkLoader;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] params = new String[5];
        params[0] = "C:/ER"; // Data folder
        params[1] = "TURTLE";
        params[2] = "TDB";
        params[3] = "C:/TDB"; // TDB repository path
        params[4] = "true"; // Enable RDFS entailment
        JenaBulkLoader.main(params);

        Session session = SessionFactory.getJenaTDBSession(params[3]);

        String QUERY_2 = "PREFIX base: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl> "
                + "PREFIX ub:   <http://swat.cse.lehigh.edu/onto/univ-bench.owl#> "
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + "PREFIX owl:  <http://www.w3.org/2002/07/owl#> "
                + "PREFIX xdt:  <http://www.w3.org/2001/XMLSchema#> "
                + "SELECT ?X ?Y1 ?Y2 ?Y3 ?Y4 ?Y5 "
                + "WHERE { ?X rdf:type ub:UndergraduateStudent . "
                + "?X ub:memberOf ?Y1 . "
                + "?X ub:mastersDegreeFrom <http://www.University822.edu>  . "
                + "?X ub:emailAddress ?Y2 . "
                + "?X ub:advisor <http://www.Department0.University0.edu/FullProfessor0> . "
                + "?X ub:takesCourse ?Y4. "
                + "?X ub:name ?Y5  . "
                + " }";

        CQuery conjunctiveQuery = CQueryFactory.createCQuery(QUERY_2);
        MFSSearch relaxationStrategy = StrategyFactory.getLatticeStrategy(session, conjunctiveQuery);
        List<CQuery> allMFS = relaxationStrategy.getAllMFS();
        List<CQuery> allXSS = relaxationStrategy.getAllXSS();

    }
}
