package edu.nd.crc.jenatest;

/**
 * Created by cvardema on 5/21/15.
 * SPIN API example based on Post at:
 * https://semanticwebrecipes.wordpress.com/2014/07/09/using-the-spin-api-in-your-eclipse-project/
 */

import java.util.List;

import org.topbraid.spin.constraints.ConstraintViolation;
import org.topbraid.spin.constraints.SPINConstraints;
import org.topbraid.spin.inference.SPINExplanations;
import org.topbraid.spin.inference.SPINInferences;
import org.topbraid.spin.system.SPINLabels;
import org.topbraid.spin.system.SPINModuleRegistry;
import org.topbraid.spin.util.JenaUtil;
import org.topbraid.spin.vocabulary.SP;
import org.topbraid.spin.vocabulary.SPIN;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
public class SPINtest {
    /** SPARQL WHERE clause for selecting properties of Arnie */
    private static final String GOVERNATORS_RELATIONS = "kennedys:ArnoldSchwarzenegger ?r ?x";

    public static void main(String[] args){
        // Load spin dependent stuff
        SPINModuleRegistry.get().init();
        // Load main file
        Model baseModel = ModelFactory.createDefaultModel();
        baseModel.read("http://topbraid.org/examples/kennedysSPIN");
        // Fix a bug in the (current) online version of the Kennedy example
        performSurgery(baseModel);
        // Create OntModel with imports
        OntModel ontModel = JenaUtil.createOntologyModel(OntModelSpec.OWL_MEM,
                baseModel);
        // Print known data on Arnie
        System.out.println("Basic facts on the Governator:");
        SPINutil.runSelect(ontModel, GOVERNATORS_RELATIONS);
        // Create and add Model for inferred triples
        Model newTriples = ModelFactory.createDefaultModel();
        ontModel.addSubModel(newTriples);
        newTriples.setNsPrefixes(baseModel.getNsPrefixMap()); // Improve readability of newTriples.write(System.out, "N3")
        // Register locally defined functions
        SPINModuleRegistry.get().registerAll(ontModel, null);
        // Run all inferences
        SPINExplanations explain = new SPINExplanations();
        SPINInferences.run(ontModel, newTriples, explain, null, false, null);
        // Print out inferences based on SPIN model
        System.out
                .println("Carefully derived conclusions about the Governator:");
        SPINutil.runSelect(newTriples, GOVERNATORS_RELATIONS);
        System.out.println("How SPIN arrived at the Governator's age:");
        System.out
                .println(explain.getText(newTriples
                        .getProperty(
                                newTriples
                                        .getResource("http://topbraid.org/examples/kennedys#ArnoldSchwarzenegger"),
                                newTriples
                                        .getProperty("http://topbraid.org/examples/kennedysSPIN#age"))
                        .asTriple()));
        // Run all constraints
        List<ConstraintViolation> cvs = SPINConstraints.check(ontModel, null);
        System.out.println("Constraint violations:");
        for (ConstraintViolation cv : cvs) {
            System.out.println(" - at "
                    + SPINLabels.get().getLabel(cv.getRoot()) + ": "
                    + cv.getMessage());
        }
        // Run constraints on a single instance only
        Resource person = cvs.get(0).getRoot();
        List<ConstraintViolation> localCVS = SPINConstraints
                .check(person, null);
        System.out.println("Constraint violations for "
                + SPINLabels.get().getLabel(person) + ": " + localCVS.size());

    }

    /**
     * Fix a bug in the current online version of the data: getCurrentYear()
     * calls afn:now and retrieves substr(0,4) from the result as the current
     * year, but this yields "201". Change it to substr(1,4), as in the file
     * distributed with TopBraid, and I get "2014".
     */
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
