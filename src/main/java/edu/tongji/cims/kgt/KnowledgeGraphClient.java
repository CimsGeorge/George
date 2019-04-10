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
 * A knowledge graph client to handle ontology and neo4j.
 * @author Yue Lin
 * @version 0.0.1
 */

public class KnowledgeGraphClient {

    private Neo4jService neo4jService;
    private OntologyService ontologyService;

    /**
     *
     * @param uri the uri to connect neo4j
     */
    public KnowledgeGraphClient(String uri) {
        neo4jService = new Neo4jService(uri);
        ontologyService = new OntologyService(neo4jService);
    }

    /**
     *
     * @param name
     * @return
     * @throws IOException
     */
    public Boolean saveClass(String name) throws IOException {
        return judge(neo4jService.saveClass(name));
    }

    /**
     *
     * @param name
     * @param properties
     * @return
     * @throws IOException
     */
    public Boolean saveClass(String name, Map<String, String> properties) throws IOException {
        return judge(neo4jService.saveClass(name, properties));
    }

    public Boolean saveSubClass(String superClassName, String subClassName) throws IOException {
        return judge(neo4jService.saveSubClass(superClassName, subClassName));
    }

    /**
     *
     * @param name
     * @return
     * @throws IOException
     */
    public Boolean saveIndividual(String name) throws IOException {
        return judge(neo4jService.saveIndividual(name));
    }

    /**
     *
     * @param name
     * @param properties
     * @return
     * @throws IOException
     */
    public Boolean saveIndividual(String name, Map<String, String> properties) throws IOException {
        return judge(neo4jService.saveIndividual(name, properties));
    }

    /**
     *
     * @param name
     * @param properties
     * @return
     * @throws IOException
     */
    public Boolean updateClassProperty(String name, Map<String, String> properties) throws IOException {
        return judge(neo4jService.updateClassProperty(name, properties));
    }

    /**
     *
     * @param name
     * @param properties
     * @return
     * @throws IOException
     */
    public Boolean updateIndividualProperty(String name, Map<String, String> properties) throws IOException {
        return judge(neo4jService.updateIndividualProperty(name, properties));
    }

    /**
     *
     * @param propertyKey
     * @param propertyValue
     * @return
     */
    public List<Node> getNodeByProperty(String propertyName, String propertyValue) {
        return null;
    }

    /**
     *
     * @param propertyKey
     * @param propertyValue
     * @return
     * @throws IOException
     */
    public List<Node> queryNodeInFuzzyByProperty(String propertyName, String propertyValue) throws IOException {
        return neo4jService.queryNodeInFuzzyByProperty(propertyName, propertyValue);
    }

    /**
     *
     * @param name
     * @return
     * @throws IOException
     */
    public List<Node> queryNext(String name) throws IOException {
        return neo4jService.queryNext(name);
    }

    /**
     *
     * @param name
     * @return
     * @throws IOException
     */
    public String getNodeType(String name) throws IOException {
        return neo4jService.getNodeType(name);
    }

    /**
     *
     * @param name the name of node
     * @return
     * @throws IOException
     */
    public Boolean containsNode(String name) throws IOException {
        return neo4jService.containsNode(name);
    }

    /**
     *
     * @param name the name of node
     * @param degree
     * @return
     * @throws IOException
     */
    public Graph queryPath(String name, int degree) throws IOException {
        return neo4jService.queryPath(name, degree);
    }

    /**
     *
     * @param fromName
     * @param toName
     * @return
     * @throws IOException
     */
    public Graph queryShortestPath(String fromName, String toName) throws IOException {
        return neo4jService.queryShortestPath(fromName, toName);
    }

    public Boolean deleteNode(String name) {
        return true;
    }

    public Boolean deleteTriple(String fromName, String relName, String toName) {
        return true;
    }

    public Boolean removeAll() throws IOException {
        return judge(neo4jService.removeAll());
    }

    /**
     *
     * @param statement
     * @return
     * @throws IOException
     */
    public Neo4jResponse execute(String statement) throws IOException {
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
