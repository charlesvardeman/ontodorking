package edu.nd.crc.jenatest;

/**
 * Created by cvardema on 5/21/15.
 * SPIN API example based on Post at:
 * https://semanticwebrecipes.wordpress.com/2014/07/09/using-the-spin-api-in-your-eclipse-project/
 */
import org.topbraid.spin.arq.ARQFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;

abstract class SPINutil {
    /**
     * Executes a simple "SELECT * WHERE {...}" SPARQL query and dumps the
     * result on System.out Example: runSelect(ontModel,
     * "BIND (xsd:string(afn:now()) AS ?str)",
     * "BIND (fn:substring(?str, 1, 4) AS ?sub)",
     * "BIND (xsd:integer(?sub) AS ?year)",
     * "kennedys:ArnoldSchwarzenegger kennedys:birthYear ?birthYear",
     * "BIND (?year AS ?currentYear)",
     * "BIND ((?currentYear - ?birthYear) AS ?age)" );
     *
     * @param model
     *            The model on which to execute the query
     * @param clauses
     *            The clauses to use in WHERE. This method will add "."
     *            separators between the clauses. A clause may use namespace
     *            prefixes defined in the model.
     */

    static void runSelect(final Model model, final String... clauses) {
        final Query query = SPINutil.selectQuery(model, clauses);
        QueryExecution qe = ARQFactory.get().createQueryExecution(query, model);
        ResultSet results = qe.execSelect();
        ResultSetFormatter.out(System.out, results, query);
        qe.close();
    }

    /**
     * Helper method for runSelect()
     */
    private static Query selectQuery(final Model model, final String... clauses) {
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT * WHERE {");
        for (final String clause : clauses) {
            sb.append(clause);
            sb.append(" . ");
        }
        sb.append("}");
        return ARQFactory.get().createQuery(model, sb.toString());
    }
}
