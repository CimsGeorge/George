package edu.tongji.cims.kgt;

import java.io.IOException;

/**
 * @author Yue
 * @since 2019/3/27
 */
public class ClientTest {

    public static void main(String[] args) throws IOException {
        String url = "http://neo4j:1234@localhost:7474";
        Client client = new Client(url);
        client.mergeNode("test");
    }
}
