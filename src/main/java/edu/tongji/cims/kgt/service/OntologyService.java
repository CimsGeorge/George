package edu.tongji.cims.kgt.service;

import edu.tongji.cims.kgt.model.neo4j.request.Batch;
import edu.tongji.cims.kgt.model.neo4j.request.Parameter;
import edu.tongji.cims.kgt.model.neo4j.response.Neo4jResponse;
import edu.tongji.cims.kgt.model.ontology.ComponentEnum;
import edu.tongji.cims.kgt.model.ontology.OWLNamedClass;
import edu.tongji.cims.kgt.model.ontology.RelationshipEnum;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner;
import org.semanticweb.owlapi.search.EntitySearcher;
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImplString;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * @author Yue Lin, RuHan Yang
 * @version 0.0.1
 */

public class OntologyService extends CypherService {

    private OWLOntology ontology;
    private OWLReasoner reasoner;
    private Neo4jService neo4jService;
    private Batch batch;

    public OntologyService(Neo4jService neo4jService) {
        this.neo4jService = neo4jService;
    }

    public Boolean parse(File file) throws OWLOntologyCreationException, IOException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        ontology = manager.loadOntologyFromOntologyDocument(file);
        reasoner = new StructuralReasoner(ontology, new SimpleConfiguration(), BufferingMode.BUFFERING);  // 推理机
        batch = new Batch();
        // 获取除了owl:thing之外的所有类
        Stream<OWLClass> classes = ontology.classesInSignature(Imports.INCLUDED).
                filter(c -> !getObjectName(c).equals(ComponentEnum.OWL_THING.getName()));
        classes.forEach(c -> {
            OWLNamedClass owlNamedClass = parseSubClassRelationShip(c);
            parseIndividual(owlNamedClass);
        });
        Neo4jResponse response = neo4jService.execute(batch);
        return response.getErrors().size() == 0;
    }

    private OWLNamedClass parseSubClassRelationShip(OWLClass clazz) {
        String className = getObjectName(clazz);
        parseAnnotation(ComponentEnum.CLASS, clazz, className);  // 解析类的annotation

        batch.statements.add(MERGE_CLASS);
        batch.parameters.add(new Parameter(className));

        NodeSet<OWLClass> superClassNodes = reasoner.getSuperClasses(clazz, true);
        for (Node<OWLClass> superClassNode : superClassNodes) {
            OWLClass superClass = superClassNode.getRepresentativeElement();
            String superClassName = getObjectName(superClass);
            // 过滤owl:thing类
            if (!superClassName.equals(ComponentEnum.OWL_THING.getName())) {
                batch.statements.add(MERGE_CLASS);
                batch.parameters.add(new Parameter(superClassName));
                batch.statements.add(MERGE_RELATIONSHIP);
                batch.parameters.add(new Parameter(superClassName, RelationshipEnum.SUB_CLASS.getName(), className));
            }
        }
        return new OWLNamedClass(className, clazz);
    }

    private void parseIndividual(OWLNamedClass clazz) {
        NodeSet<OWLNamedIndividual> individualNodes = reasoner.getInstances(clazz.getOwlClass(), true);
        for (Node<OWLNamedIndividual> individualNode : individualNodes) {
            OWLNamedIndividual individual = individualNode.getRepresentativeElement();
            String individualName = getObjectName(individual);
            batch.statements.add(MERGE_INDIVIDUAL);
            batch.parameters.add(new Parameter(individualName));
            batch.statements.add(MERGE_RELATIONSHIP);
            batch.parameters.add(new Parameter(clazz.getClassName(), RelationshipEnum.INDIVIDUAL.getName(), individualName));

            parseAnnotation(ComponentEnum.INDIVIDUAL, individual, individualName);  // 解析实例的annotation
            // 处理对象属性
            Stream<OWLObjectProperty> objectProperty = ontology.objectPropertiesInSignature();
            objectProperty.forEach(o -> parseObjectProperty(individualName, individual, o));
            // 处理数据属性
            Stream<OWLDataProperty> dataProperty = ontology.dataPropertiesInSignature();
            dataProperty.forEach(d -> parseDataProperty(individualName, individual, d));
        }
    }

    private void parseObjectProperty(String fromIndividualName, OWLNamedIndividual fromIndividual, OWLObjectProperty objectProperty) {
        NodeSet<OWLNamedIndividual> toIndividualNodes = reasoner.getObjectPropertyValues(fromIndividual, objectProperty);
        for (Node<OWLNamedIndividual> individualNode : toIndividualNodes) {
            String objectPropertyName = getObjectName(objectProperty);
            String toIndividualName = getObjectName(individualNode.getRepresentativeElement());

            batch.statements.add(MERGE_INDIVIDUAL);
            batch.parameters.add(new Parameter(toIndividualName));
            batch.statements.add(MERGE_RELATIONSHIP);
            batch.parameters.add(new Parameter(fromIndividualName, objectPropertyName, toIndividualName));

            parseAnnotation(ComponentEnum.RELATIONSHIP, objectProperty, fromIndividualName,objectPropertyName, toIndividualName);  // 解析对象属性的annotation
        }
    }

    private void parseDataProperty(String individualName, OWLNamedIndividual individual, OWLDataProperty dataProperty) {
        for (OWLLiteral object : reasoner.getDataPropertyValues(individual, dataProperty.asOWLDataProperty())) {
            String dataPropertyName = getObjectName(dataProperty.asOWLDataProperty());
            String dataPropertyValue = object.getLiteral();
            batch.statements.add(setProperty(ComponentEnum.INDIVIDUAL.getName(), dataPropertyName));
            batch.parameters.add(new Parameter(individualName, dataPropertyValue));
//            parseAnnotation(dataPropertyName, dataProperty);  // 解析数据属性的annotation
        }
    }

    private void parseAnnotation(ComponentEnum component, OWLEntity entity, String ...entityName) {
        Stream<OWLAnnotation> annotations = EntitySearcher.getAnnotations(entity, ontology);
        annotations.forEach(a -> {
            String annotationName = getObjectName(a.getProperty());
            if (annotationName.contains(":"))  // 去除annotation前缀
                annotationName = annotationName.substring(annotationName.indexOf(":") + 1);
            String annotationValue = a.getValue().asLiteral().orElse(new OWLLiteralImplString("")).getLiteral();
            batch.statements.add(setProperty(component.getName(), annotationName));
            if (component == ComponentEnum.RELATIONSHIP)
                batch.parameters.add(new Parameter(entityName[0], entityName[1], entityName[2], annotationValue));
            else
                batch.parameters.add(new Parameter(entityName[0], annotationValue));
        });
    }

    private static String getObjectName(Object o) {
        String objectName = o.toString();
        if (objectName.contains("#"))
            objectName = objectName.substring(objectName.indexOf("#") + 1, objectName.lastIndexOf(">"));
        return objectName;
    }

}
