package edu.nd.crc.jenatest;

import java.util.Iterator;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;
import org.topbraid.spin.vocabulary.SP;
import org.topbraid.spin.vocabulary.SPIN;
import virtuoso.jena.driver.*;

/**
 * Created by cvardema on 5/27/15.
 */
public class VIRTtest {


    public static void main(String args[]) {
    /*			STEP 1			*/
        VirtGraph graph = new VirtGraph("kennedysSPIN", "jdbc:virtuoso://192.168.59.103:1111", "dba", "dba");
        VirtModel baseModel = new VirtModel(graph);

/*			STEP 2			*/
/*		Load data to Virtuoso		*/
        graph.clear();

        baseModel.read("http://topbraid.org/examples/kennedysSPIN", "RDF/XML");
        performSurgery(baseModel);




/*			STEP 3			*/
/*		Select only from VirtGraph	*/
        Query sparql = QueryFactory.create("SELECT ?s ?p ?o WHERE { ?s ?p ?o }");

/*			STEP 4			*/
        VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (sparql, graph);

        ResultSet results = vqe.execSelect();
        while (results.hasNext()) {
            QuerySolution result = results.nextSolution();
            RDFNode graph_name = result.get("graph");
            RDFNode s = result.get("s");
            RDFNode p = result.get("p");
            RDFNode o = result.get("o");
            System.out.println(graph_name + " { " + s + " " + p + " " + o + " . }");
        }

        System.out.println("graph.getCount() = " + graph.getCount());
    }
    private static void performSurgery(final Model model) {
        final Resource substrExpr = lookup(model,
                "http://topbraid.org/examples/kennedysSPIN#getCurrentYear",
                SPIN.body, SP.where, RDF.rest, RDF.first, SP.expression);
        substrExpr.removeAll(SP.arg2);// was: 0
        substrExpr.addLiteral(SP.arg2, 1);
    }
    /**
     * Convenience function for navigating to a specific subproperty of a
     * resource. The indicated property must be a resource.
     *
     * @param model
     *            The model to navigate
     * @param uri
     *            The uri of the Resource to start from
     * @param properties
     *            The properties to follow.
     * @return The indicated property.
     */
    private static Resource lookup(final Model model, final String uri,
                                   final Property... properties) {
        Resource r = model.getResource(uri);
        for (final Property p : properties) {
            r = r.getPropertyResourceValue(p);
        }
        return r;
    }
}