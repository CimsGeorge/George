package edu.tongji.cims.kgt.service;

import edu.tongji.cims.kgt.model.ClassPair;
import edu.tongji.cims.kgt.model.Cypher;
import edu.tongji.cims.kgt.model.Parameter;
import edu.tongji.cims.kgt.model.QueryProp;
import edu.tongji.cims.kgt.model.RelationEnum;
import edu.tongji.cims.kgt.util.Tokenizer;
import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Yue Lin
 * @version 0.0.1
 */

@Slf4j
public class OntologyService {

    private OWLOntology ontology;
    private OWLReasoner reasoner;
    private Neo4jService neo4jService;

    public OntologyService(Neo4jService neo4jService) {
        this.neo4jService = neo4jService;
    }

    public Boolean parse(File file) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        ontology = manager.loadOntologyFromOntologyDocument(file);
        reasoner = new StructuralReasoner(ontology, new SimpleConfiguration(), BufferingMode.BUFFERING);
        Stream<OWLClass> classes = ontology.classesInSignature(Imports.INCLUDED).filter(c -> !getObjectName(c).equals("owl:Thing"));
        Stream<ClassPair> classPairs = classes.map(c -> {
            try {
                return handleRelation(reasoner, c);
            } catch (IOException e) {
                // todo return
                e.printStackTrace();
            }
            // todo handle error
            return null;
        });
        classPairs.filter(Objects::nonNull).forEach(c -> {
            try {
                handleIndividual(ontology, reasoner, c.getClazz(), c.getName());
            } catch (IOException e) {
                // todo return
                e.printStackTrace();
            }
        });
        return true;
    }

    private ClassPair handleRelation(OWLReasoner reasoner, OWLClass owlClass) throws IOException {
        String className = getObjectName(owlClass);
        neo4jService.mergClass(className);
        NodeSet<OWLClass> superClasses = reasoner.getSuperClasses(owlClass, true);
        List<String> statements = new ArrayList<>();
        List<Parameter> parameters = new ArrayList<>();
        for (org.semanticweb.owlapi.reasoner.Node<OWLClass> parentOWLNode : superClasses) {
            OWLClass parent = parentOWLNode.getRepresentativeElement();
            String parentName = getObjectName(parent);
            if (!parentName.equals("owl:Thing")) {
                statements.add(Cypher.MERGE_NODE);
                parameters.add(new Parameter(new QueryProp(parentName).getProps()));
                statements.add(Cypher.MERGE_EDGE);
                parameters.add(new Parameter(new QueryProp(className, RelationEnum.SUB_CLASS.getName(), parentName).getProps()));
            }
        }
        if (statements.size() != 0)
            neo4jService.handler(statements, parameters);
        return new ClassPair(owlClass, className);
    }

    private void handleIndividual(OWLOntology ontology, OWLReasoner reasoner, OWLClass owlClass, String nodeName) throws IOException {
        List<String> statements = new ArrayList<>();
        List<Parameter> parameters = new ArrayList<>();
        for (org.semanticweb.owlapi.reasoner.Node<OWLNamedIndividual> individual : reasoner.getInstances(owlClass, true)) {
            OWLNamedIndividual indi = individual.getRepresentativeElement();
            String indiName = getObjectName(indi);
            statements.add(Cypher.MERGE_NODE);
            parameters.add(new Parameter(new QueryProp(indiName).getProps()));
            // todo
            statements.add(Cypher.MERGE_EDGE);
            parameters.add(new Parameter(new QueryProp(indiName, RelationEnum.INSTANCE.getName(), nodeName).getProps()));
            if (statements.size() != 0)
                neo4jService.handler(statements, parameters);

            Stream<OWLObjectProperty> objectProperty = ontology.objectPropertiesInSignature();
            objectProperty.forEach(o -> {
                try {
                    handleObjectProperty(reasoner, indi, indiName, o);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            Stream<OWLDataProperty> dataProperty = ontology.dataPropertiesInSignature();
            Map<String, String> props = new HashMap<>();
            dataProperty.forEach(d -> handleDataProperty(reasoner, indi, d, props));
            String name = Tokenizer.replaceUnderlineWithSpace(indiName);
            List<String> statementList = new ArrayList<>();
            List<Parameter> parameterList = new ArrayList<>();
            for (Map.Entry<String, String> e : props.entrySet()) {
                String statement = Cypher.setInstanceProperty(e.getKey());
                statementList.add(statement);
                parameterList.add(new Parameter(new QueryProp(name, e.getValue()).getProps()));
            }
            if (statementList.size() != 0)
                neo4jService.handler(statementList, parameterList);
        }
    }

    private void handleObjectProperty(OWLReasoner reasoner, OWLNamedIndividual indi, String indiName, OWLObjectProperty objectProperty) throws IOException {
        List<String> statements = new ArrayList<>();
        List<Parameter> parameters = new ArrayList<>();
        for (org.semanticweb.owlapi.reasoner.Node<OWLNamedIndividual> object: reasoner.getObjectPropertyValues(indi, objectProperty)) {
            String relType = getObjectName(objectProperty);
            String s = getObjectName(object.getRepresentativeElement());
            statements.add(Cypher.MERGE_NODE);
            parameters.add(new Parameter(new QueryProp(s).getProps()));
            statements.add(Cypher.MERGE_EDGE);
            parameters.add(new Parameter(new QueryProp(s, relType, indiName).getProps()));
        }
        if (statements.size() != 0)
            neo4jService.handler(statements, parameters);
    }

    private void handleDataProperty(OWLReasoner reasoner, OWLNamedIndividual indi, OWLDataProperty dataProperty, Map<String, String> props) {
        for (OWLLiteral object: reasoner.getDataPropertyValues(indi, dataProperty.asOWLDataProperty())) {
            String relType = getObjectName(dataProperty.asOWLDataProperty());
            String s = object.getLiteral();
            props.put(relType, s);
        }
    }

    private static String getObjectName(Object o) {
        String objectName = o.toString();
        if (objectName.contains("#"))
            objectName = objectName.substring(objectName.indexOf("#") + 1, objectName.lastIndexOf(">"));
        return objectName;
    }

}
