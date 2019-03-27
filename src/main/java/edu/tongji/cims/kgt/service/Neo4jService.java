package edu.tongji.cims.kgt.service;

import com.alibaba.fastjson.JSONObject;
import edu.tongji.cims.kgt.config.ClientConfig;
import edu.tongji.cims.kgt.model.Cypher;
import edu.tongji.cims.kgt.model.Neo4jResponse;
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
import java.util.List;
import java.util.Map;

/**
 * @author Yue
 * @since 2019/3/27
 */
public class Neo4jService {

    private String url;
    private CloseableHttpClient client;

    public Neo4jService(String url) {
        this.url = url + "/db/data/transaction/commit";
        client = ClientConfig.getClient();
    }

    public Neo4jResponse mergeNode(String name) throws IOException {
        String statement = Cypher.MERGE_NODE;
        return handler(statement, new QueryProp(name).getProps());
    }

    private Neo4jResponse handler(String statement, Map<String, String> props) throws IOException {
        List<String> stringList = new ArrayList<>();
        stringList.add(statement);
        List<Parameter> parameterList = new ArrayList<>();
        parameterList.add(new Parameter(props));
        return handler(stringList, parameterList);
    }

    private Neo4jResponse handler(List<String> statementList, List<Parameter> parameterList) throws IOException {
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
