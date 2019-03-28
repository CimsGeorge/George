package edu.tongji.cims.kgt.config;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * @author Yue Lin
 * @version 0.0.1
 */
public class ClientConfig {

    private final static int MAX_TOTAL = 200;
    private final static int DEFAULT_MAX_PER_ROUTE = 100;
    private final static PoolingHttpClientConnectionManager CM = new PoolingHttpClientConnectionManager();

    public static CloseableHttpClient getClient() {
        CM.setMaxTotal(MAX_TOTAL);
        CM.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
        return HttpClients.custom().setConnectionManager(CM).build();
    }
}
