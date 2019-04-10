package edu.tongji.cims.kgt;

import edu.tongji.cims.kgt.model.neo4j.response.Row;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.io.IOException;
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
//        String name = "test";
//        String name1 = "test1";
//        String name2 = "test2";
//        String superClassName = "super";
//        String subClassName = "subClass";
        saveSubClassTest("test", "test2");
//        saveClassTest(name);
//        Map<String, String> properties = new HashMap<>();
//        properties.put("size", "2");
//        saveClassTest(name, properties);
//        saveClassTest(name1, properties);
//        saveClassTest(name2, properties);
//        updateClassPropertyTest(name, properties);
//        removeAllTest();
//        File file = new File("C:\\Users\\Yue\\Desktop\\test.owl");
//        parseOntology(file);
//        System.out.println(getNodeByProperty("name", "t", true));
//        System.out.println(getNodeLabelTest("test"));
//        saveClassTest("test");
        System.out.println(getNextTest("test"));
    }

    public static List<Row> getNextTest(String name) throws IOException {
        return kgClient.getNext(name);
    }

    public static List<Row> getNodeLabelTest(String name) throws IOException {
        return kgClient.getNodeLabel(name);
    }

    public static List<Row> getNodeByProperty(String propertyName, String propertyValue, Boolean fuzzy) throws IOException {
        return kgClient.getNodeByProperty(propertyName, propertyValue, fuzzy);
    }

    public static void parseOntology(File file) throws OWLOntologyCreationException, IOException {
        System.out.println(kgClient.parseOntology(file));
    }

    public static void saveClassTest(String name) throws IOException {
        System.out.println(kgClient.saveClass(name));
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
