package edu.tongji.cims.kgt;

import edu.tongji.cims.kgt.service.Neo4jService;

import java.io.IOException;

/**
 * @author Yue
 * @since 2019/3/27
 */
public class Client {

    private Neo4jService neo4jService;

    public Client(String url) {
        neo4jService = new Neo4jService(url);
    }

    public void mergeNode(String name) throws IOException {
        System.out.println(neo4jService.mergeNode(name));
    }
}
