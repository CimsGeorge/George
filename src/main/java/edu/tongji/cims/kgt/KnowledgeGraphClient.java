package edu.tongji.cims.kgt;

import edu.tongji.cims.kgt.model.neo4j.Graph;
import edu.tongji.cims.kgt.model.neo4j.Node;
import edu.tongji.cims.kgt.model.neo4j.response.Neo4jResponse;
import edu.tongji.cims.kgt.service.Neo4jService;
import edu.tongji.cims.kgt.service.OntologyService;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The knowledge graph client based on ontology and neo4j.
 * @author Yue Lin, RuHan Yang
 * @version 0.0.1
 */

public class KnowledgeGraphClient {

    private Neo4jService neo4jService;
    private OntologyService ontologyService;

    /**
     * uri template: protocol://neo4j.username:neo4j.password@host:port.
     * @param uri the uri to connect neo4j
     */
    public KnowledgeGraphClient(String uri) {
        neo4jService = new Neo4jService(uri);
        ontologyService = new OntologyService(neo4jService);
    }

    /**
     * save a class with name.
     * @param name the class name
     * @return true if class is saved successfully
     * @throws IOException
     */
    public Boolean saveClass(String name) throws IOException {
        return judge(neo4jService.saveClass(name));
    }

    /**
     * save a class with name and its properties.
     * @param name the class name
     * @param properties the class properties
     * @return true if class is saved successfully
     * @throws IOException
     */
    public Boolean saveClass(String name, Map<String, String> properties) throws IOException {
        return judge(neo4jService.saveClass(name, properties));
    }

    /**
     * save a superClassName's subClass.
     * @param superClassName the superClass name
     * @param subClassName the subClass name
     * @return true if subClass is saved successfully
     * @throws IOException
     */
    public Boolean saveSubClass(String superClassName, String subClassName) throws IOException {
        return judge(neo4jService.saveSubClass(superClassName, subClassName));
    }

    /**
     * save a individual with name and its properties.
     * @param name the individual name
     * @return true if individual is saved successfully
     * @throws IOException
     */
    public Boolean saveIndividual(String name) throws IOException {
        return judge(neo4jService.saveIndividual(name));
    }

    /**
     * save a individual with name and its properties.
     * @param name the individual name
     * @param properties the individual properties
     * @return true if individual is saved successfully
     * @throws IOException
     */
    public Boolean saveIndividual(String name, Map<String, String> properties) throws IOException {
        return judge(neo4jService.saveIndividual(name, properties));
    }

    /**
     * save a individual triple
     * @param fromIndividualName the source individual name
     * @param relationship the relationship name
     * @param toIndividualName the target individual name
     * @return true if individual triple is saved successfully
     * @throws IOException
     */
    public Boolean saveIndividual(String fromIndividualName, String relationship, String toIndividualName) throws IOException {
        return judge(neo4jService.saveIndividualTriple(fromIndividualName, relationship, toIndividualName));
    }

    /**
     * update a class's property.
     * @param name the class name
     * @param properties the properties to set
     * @return true if properties are set successfully
     * @throws IOException
     */
    public Boolean updateClassProperty(String name, Map<String, String> properties) throws IOException {
        return judge(neo4jService.updateClassProperty(name, properties));
    }

    /**
     * update a individual's property.
     * @param name the individual name
     * @param properties the properties to set
     * @return true if properties are set successfully
     * @throws IOException
     */
    public Boolean updateIndividualProperty(String name, Map<String, String> properties) throws IOException {
        return judge(neo4jService.updateIndividualProperty(name, properties));
    }

    /**
     * get nodes by property.
     * @param propertyName the property name
     * @param propertyValue the property value
     * @param fuzzy fuzzy query or not
     * @return nodes
     * @throws IOException
     */
    public List<Node> getNodeByProperty(String propertyName, String propertyValue, Boolean fuzzy) throws IOException {
        return neo4jService.getNodeByProperty(propertyName, propertyValue, fuzzy);
    }

    /**
     * get next nodes of a node.
     * @param name the node name
     * @return next nodes
     * @throws IOException
     */
    public List<Node> getNext(String name) throws IOException {
        return neo4jService.getNext(name);
    }

    /**
     * get individuals of a class.
     * @param className the class name
     * @return the class's individuals
     * @throws IOException
     */
    public List<Node> getIndividual(String className) throws IOException {
        return neo4jService.getIndividual(className);
    }

    /**
     * get node's type.
     * @param name the node name
     * @return the node's type
     * @throws IOException
     */
    public String getNodeType(String name) throws IOException {
        return neo4jService.getNodeType(name);
    }

    /**
     * get a node's properties.
     * @param name the node name
     * @return the node's properties
     * @throws IOException
     */
    public Map<String, String> getNodeProperty(String name) throws IOException {
        return neo4jService.getNodeProperty(name);
    }

    /**
     * determine if a node exists.
     * @param name the node name
     * @return true if the node exists
     * @throws IOException
     */
    public Boolean containsNode(String name) throws IOException {
        return neo4jService.containsNode(name);
    }

    /**
     * get the path with specific degree from a node.
     * @param name the node name
     * @param degree the path depth
     * @return a graph object
     * @throws IOException
     */
    public Graph getPath(String name, int degree) throws IOException {
        return neo4jService.getPath(name, degree);
    }

    /**
     * get the shortest path between fromNode to toNode.
     * @param fromName the source node name
     * @param toName the target node name
     * @return a graph object
     * @throws IOException
     */
    public Graph getShortestPath(String fromName, String toName) throws IOException {
        return neo4jService.getShortestPath(fromName, toName);
    }

    /**
     * get all nodes and relationships.
     * @return a graph object
     * @throws IOException
     */
    public Graph getAllNodesAndRelationships() throws IOException {
        return neo4jService.getAllNodesAndRelationships();
    }

    /**
     * remove a class.
     * @param name the class name
     * @return true if the class is removed successfully
     * @throws IOException
     */
    public Boolean removeClass(String name) throws IOException {
        return judge(neo4jService.removeClass(name));
    }

    /**
     * remove a individual.
     * @param name the individual name
     * @return true if the individual is removed successfully
     * @throws IOException
     */
    public Boolean removeIndividual(String name) throws IOException {
        return judge(neo4jService.removeIndividual(name));
    }

    /**
     * remove all nodes.
     * @return true if all nodes are removed successfully
     * @throws IOException
     */
    public Boolean removeAll() throws IOException {
        return judge(neo4jService.removeAll());
    }

    /**
     * 暂时不开放
     * @param statement
     * @return
     * @throws IOException
     */
    private Neo4jResponse execute(String statement) throws IOException {
        return neo4jService.execute(statement);
    }

    /**
     * parse a ontology file into neo4j.
     * @param file the ontology file
     * @return true if this ontology file is parsed successfully
     * @throws OWLOntologyCreationException
     */
    public Boolean parseOntology(File file) throws OWLOntologyCreationException, IOException {
        return ontologyService.parse(file);
    }

    private Boolean judge(Neo4jResponse neo4jResponse) {
        return neo4jResponse.getErrors().size() == 0;
    }

}
