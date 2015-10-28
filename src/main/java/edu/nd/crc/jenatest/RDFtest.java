package edu.nd.crc.jenatest;

/**
 * Created by cvardema on 5/21/15.
 * Based from of example at:
 * https://semanticwebrecipes.wordpress.com/2014/07/09/up-and-running-with-jena-in-eclipse/
 */
import java.util.Iterator;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;

public class RDFtest extends Object{
    /** Namespace template for a toy example */
    private static final String URI_TEMPLATE = "http://example.com/travel#%s";

    /**
     * @param s
     *      a name to put in the namespace of our toy example
     * @return The full URI appended with s appended
     */
    private static final String uri(final String s){
        return String.format(URI_TEMPLATE, s);
    }

    public static void main(String args[]){
        //Create Model that has some fun facts about da Java
        Model model = ModelFactory.createDefaultModel();
        RDFtest.addTravelStuff(model);
        //Output header to stdout
        System.out.println("Our toy model:");
        // Prints model to std out in turtle N3
        model.write(System.out, "N3");
        // Sparql query string
        System.out.println("Find all paths from A to B in exactly two steps");
        dumpQueryResult(
                model,
                String.format(
                        "PREFIX tr: <%s> SELECT * WHERE {?a ?firstleg ?stopover . ?stopover ?secondleg ?b}",
                        uri("")));
        // PROV-O model with and without inference
        OntModel base = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        base.read("http://www.w3.org/ns/prov-o");
        OntModel inf = ModelFactory.createOntologyModel(
                OntModelSpec.OWL_MEM_MICRO_RULE_INF, base);
        // Create a Person in PROV
        Individual elmer = base.createIndividual("http://people/elmerfudd",
                base.getOntClass("http://www.w3.org/ns/prov#Person"));
        // Illustrate effect of inference
        System.out.println("RDF types in the model without inference:");
        listTypes(elmer);
        System.out.println("RDF types in the model with inference:");
        listTypes(inf.getIndividual("http://people/elmerfudd"));
    }

    private static void listTypes(final Individual individual) {
        for (Iterator<Resource> i = individual.listRDFTypes(false); i.hasNext();) {
            System.out.println(String.format("  %s has type %s", individual.getURI(),
                    i.next()));
        }
    }

    /**
     * Helper method for populating a Model with some toy Resources and
     * Properties.
     *
     * @param model
     *            The Model to populate.
     */
    private static final void addTravelStuff(final Model model) {
        model.setNsPrefix("tr", uri(""));
        final Property byTrain = model.createProperty(uri("ByTrain"));
        final Property ba = model.createProperty(uri("BritishAirways"));
        final Property af = model.createProperty(uri("AirFrance"));
        final Resource paris = model.createResource(uri("Paris"));
        final Resource lhr = model.createResource(uri("Heathrow"))
                .addProperty(ba, paris).addProperty(af, paris);
        final Resource lgw = model.createResource(uri("Gatwick")).addProperty(
                ba, paris);
        model.createResource(uri("London")).addProperty(byTrain, lhr)
                .addProperty(byTrain, lgw);
    }

    private static void dumpQueryResult(final Model model, final String queryString) {
        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();
        ResultSetFormatter.out(System.out, results, query);
        qe.close();
    }
}
