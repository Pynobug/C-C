package com.cc.item.es;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class EsTest {
    private RestHighLevelClient client;

    private static final String INDEX_NAME = "items";
    private static final String MAPPING_TEMPLATE =
            "{\n" +
                    "  \"mappings\": {\n" +
                    "    \"properties\": {\n" +
                    "      \"id\": {\n" +
                    "        \"type\": \"keyword\"\n" +
                    "      },\n" +
                    "      \"name\": {\n" +
                    "        \"type\": \"text\",\n" +
                    "        \"analyzer\": \"ik_max_word\"\n" +
                    "      },\n" +
                    "      \"price\": {\n" +
                    "        \"type\": \"integer\"\n" +
                    "      },\n" +
                    "      \"image\": {\n" +
                    "        \"type\": \"keyword\",\n" +
                    "        \"index\": false\n" +
                    "      },\n" +
                    "      \"category\": {\n" +
                    "        \"type\": \"keyword\"\n" +
                    "      },\n" +
                    "      \"brand\": {\n" +
                    "        \"type\": \"keyword\"\n" +
                    "      },\n" +
                    "      \"sold\": {\n" +
                    "        \"type\": \"integer\"\n" +
                    "      },\n" +
                    "      \"commentCount\": {\n" +
                    "        \"type\": \"integer\",\n" +
                    "        \"index\": false\n" +
                    "      },\n" +
                    "      \"updateTime\": {\n" +
                    "        \"type\": \"date\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

    @BeforeEach
    void setUp(){
        client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://192.168.245.128:9200")
        ));
    }

    @AfterEach
    void tearDown() throws IOException {
        if (client != null) {
            client.close();
        }
    }

    @Test
    void EsInit() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(INDEX_NAME);
        request.source(MAPPING_TEMPLATE, XContentType.JSON);
        client.indices().create(request, RequestOptions.DEFAULT);
    }
}
