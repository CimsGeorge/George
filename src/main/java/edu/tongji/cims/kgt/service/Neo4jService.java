package edu.tongji.cims.kgt.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import edu.tongji.cims.kgt.config.ClientConfig;
import edu.tongji.cims.kgt.model.Cypher;
import edu.tongji.cims.kgt.model.Data;
import edu.tongji.cims.kgt.model.Graph;
import edu.tongji.cims.kgt.model.Link;
import edu.tongji.cims.kgt.model.Neo4jResponse;
import edu.tongji.cims.kgt.model.Node;
import edu.tongji.cims.kgt.model.Parameter;
import edu.tongji.cims.kgt.model.QueryProp;
import edu.tongji.cims.kgt.model.RequestBody;
import edu.tongji.cims.kgt.model.Statement;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
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
public class Neo4jService {

    private String url;
    private CloseableHttpClient client;

    public Neo4jService(String url) {
        this.url = url + "/db/data/transaction/commit";
        client = ClientConfig.getClient();
    }

    public Neo4jResponse mergClass(String name) throws IOException {
        String statement = Cypher.MERGE_NODE;
        return handler(statement, new QueryProp(name).getProps());
    }

//    public Neo4jResponse mergeInstance(String name, Map<String, String> properties) {
//        String statement = Cypher
//    }

    public List<Node> queryNodeInFuzzy(String name) throws IOException {
        String statement = Cypher.QUERY_NODE_IN_FUZZY;
        name = ".*" + name + ".*";
        return composeFuzzyNodes(handler(statement, new QueryProp(name).getProps()));
    }

    public Boolean containsNode(String name) throws IOException {
        String statement = Cypher.CONTAINS_NODE;
        return handler(statement, new QueryProp(name).getProps()).getResults().get(0).getData().size() > 0;
    }

    public Map<String, String> getNodeProperties(String name) throws IOException {
        String statement = Cypher.QUERY_NODE;
        return getNodeProperties(handler(statement, new QueryProp(name).getProps()));
    }

    public Neo4jResponse setNodeProperties(String name, Map<String, String> properties) throws IOException {
        List<String> statements = new ArrayList<>();
        List<Parameter> parameters = new ArrayList<>();
        for (Map.Entry<String, String> e : properties.entrySet()) {
            String statement = Cypher.setInstanceProperty(e.getKey());
            statements.add(statement);
            parameters.add(new Parameter(new QueryProp(name, e.getValue()).getProps()));
        }
        return handler(statements, parameters);
    }

    public Graph queryPath(String name, int degree) throws IOException {
        String statement = Cypher.queryPath(degree);
        return composeGraph(handler(statement, new QueryProp(name).getProps()));
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

    public Neo4jResponse handler(String statement, Map<String, String> props) throws IOException {
        List<String> stringList = new ArrayList<>();
        stringList.add(statement);
        List<Parameter> parameterList = new ArrayList<>();
        parameterList.add(new Parameter(props));
        return handler(stringList, parameterList);
    }

    public Neo4jResponse handler(List<String> statementList, List<Parameter> parameterList) throws IOException {
        List<Statement> statements = new ArrayList<>();
        for (int i = 0; i < statementList.size(); i++)
            statements.add(new Statement(statementList.get(i), parameterList.get(i)));
        return post(new RequestBody(statements));
    }

    private Neo4jResponse post(RequestBody requestBody) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("X-Stream", "true");
        httpPost.setHeader("Accept", "application/json;charset=UTF-8");
        httpPost.setHeader("Content-Type", "application/json");
        String reqBody = JSONObject.toJSONString(requestBody);
        System.out.println(reqBody);
        httpPost.setEntity(new StringEntity(reqBody, "utf-8"));
        try (CloseableHttpResponse response = client.execute(httpPost)) {
            HttpEntity entity = response.getEntity();
            String entityString = EntityUtils.toString(entity, "utf-8");
            System.out.println(entityString);
            return JSONObject.parseObject(entityString, Neo4jResponse.class);
        }
    }
}
