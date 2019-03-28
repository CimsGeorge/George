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
public class ClientTest {

    private static String url = "http://neo4j:1234@localhost:7474";
    private static Client client = new Client(url);

    public static void main(String[] args) throws IOException, OWLOntologyCreationException {
        setNodePropertiesTest();
        getNodePropertiesTest();
    }

    public static void getNodePropertiesTest() throws IOException {
        Map<String, String> map = client.getNodeProperties("test");
        for (Map.Entry e : map.entrySet())
            System.out.println(e.getKey() + ": " + e.getValue());
    }

    public static void setNodePropertiesTest() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("c", "2");
        System.out.println(client.setNodeProperties("test", map));
    }
}
