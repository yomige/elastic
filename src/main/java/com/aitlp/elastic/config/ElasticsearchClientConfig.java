package com.aitlp.elastic.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ResourceBundle;

@Configuration
public class ElasticsearchClientConfig {
    private static final String SCHEME = "http";

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("appconf");
        String host = resourceBundle.getString("elasticsearch.host");
        int port = Integer.parseInt(resourceBundle.getString("elasticsearch.port"));
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost(host, port, SCHEME)));
        return restHighLevelClient;
    }
}
