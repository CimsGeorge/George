package edu.tongji.cims.kgt;

import edu.tongji.cims.kgt.model.neo4j.Graph;
import edu.tongji.cims.kgt.model.neo4j.Node;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yue
 * @since 2019/3/27
 */
public class KnowledgeGraphClientTest {

    private static String uri = "http://neo4j:1234@localhost:7474";
    private static KnowledgeGraphClient kgClient = new KnowledgeGraphClient(uri);

    public static void main(String[] args) throws IOException, OWLOntologyCreationException {
//        File file = new File("C:\\Users\\Yue\\Desktop\\shdq\\newWindEquipment.owl");
//        parseOntology(file);
        String name = "地理位置";
        int degree = 10;
        System.out.println(getPathTest(name, degree).toString());
    }

    public static Boolean saveIndividualTest(String fromIndividualName, String relationship, String toIndividualName) throws IOException {
        return kgClient.saveIndividual(fromIndividualName, relationship, toIndividualName);
    }

    public static Boolean saveIndividualTest(String name, Map<String, String> properties) throws IOException {
        return kgClient.saveIndividual(name, properties);
    }

    public static Boolean updateIndividualPropertyTest(String name, Map<String, String> properties) throws IOException {
        return kgClient.updateIndividualProperty(name, properties);
    }

    public static List<Node> getIndividualTest(String className) throws IOException {
        return kgClient.getIndividual(className);
    }

    public static Graph getPathTest(String name, int degree) throws IOException {
        return kgClient.getPath(name, degree);
    }

    public static Graph getAllNodesAndRelationshipsTest() throws IOException {
        Graph graph = kgClient.getAllNodesAndRelationships();
        System.out.println(graph.getNodes().size());
        return graph;
    }

    public static Map<String, String> getNodePropertyTest(String name) throws IOException {
        return kgClient.getNodeProperty(name);
    }

    public static String getNodeTypeTest(String name) throws IOException {
        return kgClient.getNodeType(name);
    }

    public static List<Node> getNextTest(String name) throws IOException {
        return kgClient.getNext(name);
    }

    public static List<Node> getNodeByPropertyTest(String propertyName, String propertyValue, Boolean fuzzy) throws IOException {
        return kgClient.getNodeByProperty(propertyName, propertyValue, fuzzy);
    }

    public static void parseOntology(File file) throws OWLOntologyCreationException, IOException {
        System.out.println(kgClient.parseOntology(file));
    }

    public static void saveClassTest(String name) throws IOException {
        System.out.println(kgClient.saveClass(name));
    }

    public static void saveIndividualTest(String name) throws IOException {
        System.out.println(kgClient.saveIndividual(name));
    }

    public static void saveClassTest(String name, Map<String, String> properties) throws IOException {
        System.out.println(kgClient.saveClass(name, properties));
    }

    public static void saveSubClassTest(String superClassName, String subClassName) throws IOException {
        System.out.println(kgClient.saveSubClass(superClassName, subClassName));
    }

    public static void updateClassPropertyTest(String name, Map<String, String> properties) throws IOException {
        System.out.println(kgClient.updateClassProperty(name, properties));
    }

    public static void removeAllTest() throws IOException {
        System.out.println(kgClient.removeAll());
    }

}
