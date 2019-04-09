package edu.tongji.cims.kgt.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import edu.tongji.cims.kgt.model.Graph;
import edu.tongji.cims.kgt.model.Link;
import edu.tongji.cims.kgt.model.Node;
import edu.tongji.cims.kgt.model.neo4j.request.Batch;
import edu.tongji.cims.kgt.model.neo4j.request.Neo4jRequest;
import edu.tongji.cims.kgt.model.neo4j.request.Parameter;
import edu.tongji.cims.kgt.model.neo4j.request.Statement;
import edu.tongji.cims.kgt.model.neo4j.response.Data;
import edu.tongji.cims.kgt.model.neo4j.response.Neo4jResponse;
import edu.tongji.cims.kgt.model.ontology.ComponentEnum;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Yue Lin
 * @version 0.0.1
 */

public class Neo4jService extends CypherService {

    /**
     * 请求Neo4j的httpClient连接池
     */
    private static class HttpClientConfig {
        private final static int MAX_TOTAL = 200;
        private final static int DEFAULT_MAX_PER_ROUTE = 100;
        private final static PoolingHttpClientConnectionManager CM = new PoolingHttpClientConnectionManager();
        private static CloseableHttpClient getHttpClient() {
            CM.setMaxTotal(MAX_TOTAL);
            CM.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
            return HttpClients.custom().setConnectionManager(CM).build();
        }
    }

    private String url;
    private CloseableHttpClient httpClient;

    public Neo4jService(String uri) {
        this.url = uri + "/db/data/transaction/commit";
        httpClient = HttpClientConfig.getHttpClient();
    }

    public Neo4jResponse saveClass(String name) throws IOException {
        return execute(MERGE_CLASS, name);
    }

    public Neo4jResponse saveClass(String name, Map<String, String> properties) throws IOException {
        return saveNodeWithProperty(ComponentEnum.CLASS, name, properties);
    }

    public Neo4jResponse saveSubClass(String superClassName, String subClassName) {
        return null;
    }

    public Neo4jResponse saveIndividual(String name) throws IOException {
        return execute(MERGE_INDIVIDUAL, name);
    }

    public Neo4jResponse saveIndividual(String name, Map<String, String> properties) throws IOException {
        return saveNodeWithProperty(ComponentEnum.INDIVIDUAL, name, properties);
    }

    public Neo4jResponse updateClassProperty(String name, Map<String, String> properties) throws IOException {
        return setProperty(ComponentEnum.CLASS, name, properties);
    }

    public Neo4jResponse updateIndividualProperty(String name, Map<String, String> properties) {
        return null;
    }

    public List<Node> getNodeInFuzzy(String name) throws IOException {
        name = ".*" + name + ".*";
        return composeFuzzyNodes(execute(QUERY_NODE_IN_FUZZY, name));
    }

    public Neo4jResponse removeClass(String name) throws IOException {
        return execute(DELETE_CLASS, name);
    }

    public Neo4jResponse removeIndividual(String name) {
        return null;
    }

    public Neo4jResponse removeAll() throws IOException {
        return execute(DELETE_ALL);
    }

    public List<Node> queryNodeInFuzzyByProperty(String propertyKey, String propertyValue) {
        return null;
    }

    public List<Node> queryNext(String name) throws IOException {
        execute(QUERY_NEXT, name);
        return null;
    }

    public Boolean containsNode(String name) throws IOException {
        return execute(CONTAINS_NODE, name).getResults().get(0).getData().size() > 0;
    }

    private Neo4jResponse saveNodeWithProperty(ComponentEnum component, String name, Map<String, String> properties) throws IOException {
        Batch batch = new Batch();
        if (component == ComponentEnum.CLASS)
            batch.statements.add(MERGE_CLASS);
        else if (component == ComponentEnum.INDIVIDUAL)
            batch.statements.add(MERGE_INDIVIDUAL);
        batch.parameters.add(new Parameter(name));
        addProperty2Batch(component, name, properties, batch);
        return execute(batch);
    }

    private Neo4jResponse setProperty(ComponentEnum component, String name, Map<String, String> properties) throws IOException {
        Batch batch = new Batch();
        addProperty2Batch(component, name, properties, batch);
        return execute(batch);
    }

    private void addProperty2Batch(ComponentEnum component, String name, Map<String, String> properties, Batch batch) {
        for (Map.Entry<String, String> e : properties.entrySet()) {
            batch.statements.add(setProperty(component.getName(), e.getKey()));
            batch.parameters.add(new Parameter(name, e.getValue()));
        }
    }

    public String getNodeType(String name) throws IOException {
        return null;
    }

    public Graph queryPath(String name, int degree) throws IOException {
        return composeGraph(execute(queryPath(degree), name));
    }

    public Graph queryShortestPath(String fromName, String toName) throws IOException {
        return composeGraph(execute(SHORTEST_PATH, fromName, toName));
    }

    public Neo4jResponse execute(String statement, String ...props) throws IOException {
        Batch batch = new Batch();
        batch.statements.add(statement);
        batch.parameters.add(new Parameter(props));
        return execute(batch);
    }

    Neo4jResponse execute(Batch batch) throws IOException {
        if (batch.statements.size() != 0)
            return execute(batch.statements, batch.parameters);
        return new Neo4jResponse();
    }

    private Graph composeGraph(Neo4jResponse neo4jResponse) {
        Graph graph = new Graph();
        List<Data> data = neo4jResponse.getResults().get(0).getData();
        Set<Node> nodeSet = new HashSet<>();
        Set<Link> linkSet = new HashSet<>();
        for (Data d : data)
            composeGraphHelper(d.getRow().toString(), nodeSet, linkSet);
        graph.setNodes(nodeSet);
        graph.setLinks(linkSet);
        return graph;
    }

    private void composeGraphHelper(String row, Set<Node> nodeSet, Set<Link> linkSet) {
        JSONArray arrayRow = JSON.parseArray(row);
        JSONArray nodes = arrayRow.getJSONArray(0);
        if (nodes.size() == 1) {
            nodeSet.add(new Node(nodes.getJSONObject(0).get("name").toString()));
            return;
        }
        JSONArray directions = arrayRow.getJSONArray(1);
        String cur, rel, next;
        int i = 0, j = 0;
        do {
            cur = nodes.getJSONObject(i).get("name").toString();
            rel = nodes.getJSONObject(i + 1).get("name").toString();
            next = nodes.getJSONObject(i + 2).get("name").toString();
            String dir = directions.getJSONObject(j).get("name").toString();
            nodeSet.add(new Node(cur));
            if (!cur.equals(dir)) {
                String t = cur;
                cur = next;
                next = t;
            }
            linkSet.add(new Link(cur, next, rel));
            i += 2;
            j++;
        } while (i + 2 < nodes.size());
        nodeSet.add(new Node(nodes.getJSONObject(nodes.size() - 1).get("name").toString()));
    }

    private List<Node> composeFuzzyNodes(Neo4jResponse neo4jResponse) {
        List<Data> data = neo4jResponse.getResults().get(0).getData();
        List<Node> nodes = new ArrayList<>();
        for (Data d : data) {
            JSONArray arrayRow = JSON.parseArray(d.getRow().toString());
            nodes.add(new Node(arrayRow.getJSONObject(0).get("name").toString()));
        }
        return nodes;
    }

    private Map<String, String> getNodeProperties(Neo4jResponse neo4jResponse) {
        Map<String, String> map = new HashMap<>();
        List<Data> data = neo4jResponse.getResults().get(0).getData();
        for (Data d : data) {
            JSONArray arrayRow = JSON.parseArray(d.getRow().toString());
            for (Object anArrayRow : arrayRow)
                map.putAll((Map<String, String>) JSON.parse(anArrayRow.toString()));
        }
        return map;
    }

    private Neo4jResponse getRelationship(String name, int dir) throws IOException {
        return execute(getRelationShip(dir), name);
    }

    private void composeRelationships(Neo4jResponse neo4jResponse, Set<String> relationship) {
        List<Data> data = neo4jResponse.getResults().get(0).getData();
        for (Data d : data) {
            JSONArray arrayRow = JSON.parseArray(d.getRow().toString());
            String relation = arrayRow.get(0).toString();
            relationship.add(relation);
        }
    }

    private Neo4jResponse execute(List<String> statementList, List<Parameter> parameterList) throws IOException {
        List<Statement> statements = new ArrayList<>();
        for (int i = 0; i < statementList.size(); i++)
            statements.add(new Statement(statementList.get(i), parameterList.get(i)));
        return post(new Neo4jRequest(statements));
    }

    private Neo4jResponse post(Neo4jRequest neo4jRequest) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("X-Stream", "true");
        httpPost.setHeader("Accept", "application/json;charset=UTF-8");
        httpPost.setHeader("Content-Type", "application/json");
        String reqBody = JSONObject.toJSONString(neo4jRequest);
        System.out.println(reqBody);
        httpPost.setEntity(new StringEntity(reqBody, "utf-8"));
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity entity = response.getEntity();
            String entityString = EntityUtils.toString(entity, "utf-8");
            System.out.println(entityString);
            return JSONObject.parseObject(entityString, Neo4jResponse.class);
        }
    }

}
