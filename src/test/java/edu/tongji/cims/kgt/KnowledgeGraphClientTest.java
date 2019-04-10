package edu.tongji.cims.kgt;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author Yue
 * @since 2019/3/27
 */
public class KnowledgeGraphClientTest {

    private static String uri = "http://neo4j:1234@localhost:7474";
    private static KnowledgeGraphClient kgClient = new KnowledgeGraphClient(uri);

    public static void main(String[] args) throws IOException, OWLOntologyCreationException {
//        String name = "test1";
//        String superClassName = "super";
//        String subClassName = "subClass";
//        saveSubClassTest(superClassName, subClassName);
//        saveClassTest(name);
//        Map<String, String> properties = new HashMap<>();
//        properties.put("size", "2");
//        updateClassPropertyTest(name, properties);
//        removeAllTest();
        File file = new File("C:\\Users\\Yue\\Desktop\\test.owl");
        parseOntology(file);
    }

    public static void parseOntology(File file) throws OWLOntologyCreationException, IOException {
        System.out.println(kgClient.parseOntology(file));
    }

    public static void saveClassTest(String name) throws IOException {
        System.out.println(kgClient.saveClass(name));
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
