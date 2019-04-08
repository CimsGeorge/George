package edu.tongji.cims.kgt;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yue
 * @since 2019/3/27
 */
public class KnowledgeGraphClientTest {

    private static String uri = "http://neo4j:1234@localhost:7474";
    private static KnowledgeGraphClient kgClient = new KnowledgeGraphClient(uri);

    public static void main(String[] args) throws IOException, OWLOntologyCreationException {
//        removeAll();
        File file = new File("C:\\Users\\Yue\\Desktop\\Ontology1480424544138.rdf");
        parseOntology(file);
    }

    public static void parseOntology(File file) throws OWLOntologyCreationException, IOException {
        kgClient.parseOntology(file);
    }

    public static void removeAll() throws IOException {
        System.out.println(kgClient.removeAll());
    }

    public static void mergeNodeTest(String name) throws IOException {
        System.out.println(kgClient.mergeNode(name));
    }

    public static void mergeNodeTest(String name, Map<String, String> properties) throws IOException {
        System.out.println(kgClient.mergeNode(name, properties));
    }

    public static void getNodePropertiesTest() throws IOException {
        Map<String, String> map = kgClient.getNodeProperties("test");
        for (Map.Entry e : map.entrySet())
            System.out.println(e.getKey() + ": " + e.getValue());
    }

    public static void setNodeProperties(String name, Map<String, String> properties) throws IOException {
        System.out.println(kgClient.setNodeProperties(name, properties));
    }

}
