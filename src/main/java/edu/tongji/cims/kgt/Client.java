package edu.tongji.cims.kgt;

import edu.tongji.cims.kgt.model.Graph;
import edu.tongji.cims.kgt.model.Neo4jResponse;
import edu.tongji.cims.kgt.model.Node;
import edu.tongji.cims.kgt.service.Neo4jService;
import edu.tongji.cims.kgt.service.OntologyService;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * a client to handle ontology and neo4j.
 * @author Yue Lin
 * @version 0.0.1
 */
public class Client {

    private Neo4jService neo4jService;
    private OntologyService ontologyService;

    /**
     *
     * @param uri the url to connect neo4j
     */
    public Client(String uri) {
        neo4jService = new Neo4jService(uri);
        ontologyService = new OntologyService(neo4jService);
    }

    /**
     *
     * @param name the name of node
     * @return
     * @throws IOException
     */
    public Boolean mergeNode(String name) throws IOException {
        return judge(neo4jService.mergeNode(name));
    }

    /**
     *
     * @param name
     * @param properties
     * @return
     * @throws IOException
     */
    public Boolean mergeNode(String name, Map<String, String> properties) throws IOException {
        return judge(neo4jService.mergeNode(name, properties));
    }

    /**
     * get all properties of a node.
     * @param name the name of node
     * @return the map of this node's properties
     * @throws IOException
     */
    public Map<String, String> getNodeProperties(String name) throws IOException {
        return neo4jService.getNodeProperties(name);
    }

    /**
     * set properties of a node.
     * @param name the name of node
     * @param properties the map of properties to set
     * @return true if properties are set successfully
     * @throws IOException
     */
    public Boolean setNodeProperties(String name, Map<String, String> properties) throws IOException {
        return judge(neo4jService.setNodeProperties(name, properties));
    }

    /**
     *
     * @param name the name of node
     * @return
     * @throws IOException
     */
    public List<Node> queryNodeInFuzzy(String name) throws IOException {
        return neo4jService.queryNodeInFuzzy(name);
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

    public Graph queryPath(String from, String to) {
        return null;
    }

    /**
     *
     * @param statement
     * @return
     * @throws IOException
     */
    public Neo4jResponse query(String statement) throws IOException {
        return neo4jService.handler(statement, new HashMap<>());
    }

    /**
     * parse a ontology file into neo4j.
     * @param file the ontology file
     * @return true if this ontology file is parsed successfully
     * @throws OWLOntologyCreationException
     */
    public Boolean parseOntology(File file) throws OWLOntologyCreationException {
        return ontologyService.parse(file);
    }

    /**
     *
     * @param neo4jResponse
     * @return
     */
    private Boolean judge(Neo4jResponse neo4jResponse) {
        return neo4jResponse.getErrors().size() == 0;
    }

}
