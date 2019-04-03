package edu.tongji.cims.kgt;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yue
 * @since 2019/3/27
 */
public class ClientTest {

    private static String uri = "http://neo4j:1234@localhost:7474";
    private static Client client = new Client(uri);

    public static void main(String[] args) throws IOException, OWLOntologyCreationException {
        String name = "test";
        Map<String, String> properties = new HashMap<>();
        properties.put("c", "2");
        properties.put("b", "1");
        mergeNodeTest(name, properties);
        mergeNodeTest(name);
        setNodeProperties(name, properties);
    }

    public static void mergeNodeTest(String name) throws IOException {
        System.out.println(client.mergeNode(name));
    }

    public static void mergeNodeTest(String name, Map<String, String> properties) throws IOException {
        System.out.println(client.mergeNode(name, properties));
    }

    public static void getNodePropertiesTest() throws IOException {
        Map<String, String> map = client.getNodeProperties("test");
        for (Map.Entry e : map.entrySet())
            System.out.println(e.getKey() + ": " + e.getValue());
    }

    public static void setNodeProperties(String name, Map<String, String> properties) throws IOException {
        System.out.println(client.setNodeProperties(name, properties));
    }

}
