package edu.tongji.cims.kgt.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import edu.tongji.cims.kgt.model.neo4j.Edge;
import edu.tongji.cims.kgt.model.neo4j.Graph;
import edu.tongji.cims.kgt.model.neo4j.Node;
import edu.tongji.cims.kgt.model.neo4j.request.Batch;
import edu.tongji.cims.kgt.model.neo4j.request.Neo4jRequest;
import edu.tongji.cims.kgt.model.neo4j.request.Parameter;
import edu.tongji.cims.kgt.model.neo4j.request.Statement;
import edu.tongji.cims.kgt.model.neo4j.response.Data;
import edu.tongji.cims.kgt.model.neo4j.response.Neo4jResponse;
import edu.tongji.cims.kgt.model.neo4j.response.Row;
import edu.tongji.cims.kgt.model.ontology.ComponentEnum;
import edu.tongji.cims.kgt.model.ontology.RelationshipEnum;
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
import java.util.stream.Collectors;

/**
 * @author Yue Lin, RuHan Yang
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

    public Neo4jResponse saveSubClass(String superClassName, String subClassName) throws IOException {
        return saveTriple(MERGE_CLASS, superClassName,
                MERGE_CLASS, subClassName,
                MERGE_RELATIONSHIP, RelationshipEnum.SUB_CLASS.getName());
    }

    public Neo4jResponse saveIndividual(String name) throws IOException {
        return execute(MERGE_INDIVIDUAL, name);
    }

    public Neo4jResponse saveIndividual(String name, Map<String, String> properties) throws IOException {
        return saveNodeWithProperty(ComponentEnum.INDIVIDUAL, name, properties);
    }

    public Neo4jResponse saveIndividual(String className, String individualName) throws IOException {
        return saveTriple(MERGE_CLASS, className,
                MERGE_INDIVIDUAL, individualName,
                MERGE_RELATIONSHIP, RelationshipEnum.INDIVIDUAL.getName());
    }

    public Neo4jResponse saveIndividualTriple(String fromIndividualName, String relationship, String toIndividualName) throws IOException {
        return saveTriple(MERGE_INDIVIDUAL, fromIndividualName,
                MERGE_INDIVIDUAL, toIndividualName,
                MERGE_RELATIONSHIP, relationship);
    }

    public Neo4jResponse updateClassProperty(String name, Map<String, String> properties) throws IOException {
        return setProperty(ComponentEnum.CLASS, name, properties);
    }

    public Neo4jResponse updateIndividualProperty(String name, Map<String, String> properties) throws IOException {
        return setProperty(ComponentEnum.INDIVIDUAL, name, properties);
    }

    public List<Node> getNodeByProperty(String propertyName, String propertyValue, Boolean fuzzy) throws IOException {
        if (fuzzy)
            propertyValue = ".*" + propertyValue + ".*";
        return composeNodeListFromRowAttribute(parseNeo4jResponseRow(
                execute(getNodeByProperty(propertyName, fuzzy), propertyValue)));
    }

    public List<Node> getIndividual(String className) throws IOException {
        return composeNodeListFromRowAttribute(parseNeo4jResponseRow(
                execute(GET_INDIVIDUAL, className)));
    }

    public String getNodeType(String name) throws IOException {
        List<Row> rows = parseNeo4jResponseRow(execute(GET_NODE_LABEL, name));
        if (rows.size() == 0) return "";
        return rows.get(0).getAttribute().get(0);
    }

    public Map<String, String> getNodeProperty(String name) throws IOException {
        List<Row> rows = parseNeo4jResponseRow(execute(GET_DATA_PROPERTY, name));
        if (rows.size() == 0) return new HashMap<>();
        return rows.get(0).getProperties();
    }

    public Neo4jResponse removeClass(String name) throws IOException {
        return execute(DELETE_CLASS, name);
    }

    public Neo4jResponse removeIndividual(String name) throws IOException {
        return execute(DELETE_INDIVIDUAL, name);
    }

    public Neo4jResponse removeAll() throws IOException {
        return execute(DELETE_ALL);
    }

    public List<Node> getNext(String name) throws IOException {
        return composeNodeListFromRowAttribute(parseNeo4jResponseRow(execute(QUERY_NEXT, name)));
    }

    public Boolean containsNode(String nodeName) throws IOException {
        return execute(CONTAINS_NODE, nodeName).getResults().get(0).getData().size() > 0;
    }

    public Graph getPath(String name, int degree) throws IOException {
        return composeGraph(execute(queryPath(degree), name));
    }

    public Graph getShortestPath(String fromName, String toName) throws IOException {
        return composeGraph(execute(SHORTEST_PATH, fromName, toName));
    }

    public Graph getAllNodesAndRelationships() throws IOException {
        return composeGraph(execute(GET_ALL));
    }

    public Neo4jResponse execute(String statement, String ...props) throws IOException {
        Batch batch = new Batch();
        batch.statements.add(statement);
        batch.parameters.add(new Parameter(props));
        return execute(batch);
    }

    Neo4jResponse execute(Batch batch) throws IOException {
        int size = batch.statements.size();
        int threshold = 200;  // 防止statement数量过多导致neo4j关闭socket
        if (batch.statements.size() != 0) {
            Neo4jResponse response = new Neo4jResponse();
            for (int i = 0; i < size; i += threshold) {
                if (i + threshold > size)
                    threshold = size - i;
                response = execute(batch.statements.subList(i, i + threshold), batch.parameters.subList(i, i + threshold));
                if (response.getErrors().size() != 0)
                    return response;
            }
            return response;
        }
        return new Neo4jResponse();
    }

    private Neo4jResponse saveTriple(String ...stateAndProp) throws IOException {
        Batch batch = new Batch();
        batch.statements.add(stateAndProp[0]);
        batch.parameters.add(new Parameter(stateAndProp[1]));
        batch.statements.add(stateAndProp[2]);
        batch.parameters.add(new Parameter(stateAndProp[3]));
        batch.statements.add(stateAndProp[4]);
        batch.parameters.add(new Parameter(stateAndProp[1], stateAndProp[5], stateAndProp[3]));
        return execute(batch);
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

    private Graph composeGraph(Neo4jResponse neo4jResponse) {
        Graph graph = new Graph();
        List<Data> data = neo4jResponse.getResults().get(0).getData();
        Set<Node> nodeSet = new HashSet<>();
        Set<Edge> edgeSet = new HashSet<>();
        for (Data d : data)
            composeGraphHelper(d.getRow().toString(), nodeSet, edgeSet);
        graph.setNodes(nodeSet);
        graph.setEdges(edgeSet);
        return graph;
    }

    // parseRow的特例
    private void composeGraphHelper(String row, Set<Node> nodeSet, Set<Edge> edgeSet) {
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
            edgeSet.add(new Edge(cur, next, rel));
            i += 2;
            j++;
        } while (i + 2 < nodes.size());
        nodeSet.add(new Node(nodes.getJSONObject(nodes.size() - 1).get("name").toString()));
    }

    private List<Node> composeNodeListFromRowAttribute(List<Row> rows) {
        return rows.stream().map(r -> new Node(r.getAttribute().get(0))).collect(Collectors.toList());
    }

    private List<Row> parseNeo4jResponseRow(Neo4jResponse neo4jResponse) {
        List<Row> rows = new ArrayList<>();
        List<Data> data = neo4jResponse.getResults().get(0).getData();
        for (Data d : data) {
            JSONArray rowContent = JSON.parseArray(d.getRow().toString());
            Row row = new Row();
            for (Object content : rowContent) {
                if (content instanceof String) {
                    row.getAttribute().add(content.toString());
                } else if (content instanceof JSONArray)
                    row.getAttribute().addAll((List<String>)JSON.parse(content.toString()));
                else
                    row.getProperties().putAll((Map<String, String>) JSON.parse(content.toString()));
            }
            rows.add(row);
        }
        return rows;
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
//        System.out.println(reqBody);
        httpPost.setEntity(new StringEntity(reqBody, "utf-8"));
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity entity = response.getEntity();
            String entityString = EntityUtils.toString(entity, "utf-8");
//            System.out.println(entityString);
            return JSONObject.parseObject(entityString, Neo4jResponse.class);
        }
    }

}
