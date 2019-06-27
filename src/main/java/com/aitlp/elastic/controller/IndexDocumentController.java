package com.aitlp.elastic.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/indexDocument")
public class IndexDocumentController {

    @Autowired
    private RestHighLevelClient client;

    @GetMapping(value = "/indexAttachmentToElasticSearch")
    public String indexAttachmentToElasticSearch(String fileFullPath) {
        String result = "error";
        InputStream inputStream;
        IndexRequest request;
        try {
            inputStream = new FileInputStream(new File(fileFullPath));
            byte[] fileByteStream = IOUtils.toByteArray(inputStream);
            String base64String = new String(Base64.getEncoder().encodeToString(fileByteStream).getBytes(), "UTF-8");
            inputStream.close();
            Map attachmentMap = new HashMap();
            attachmentMap.put("data", base64String);
            attachmentMap.put("fileName", "四个空格-https://www.4spaces.org");
            String jsonString = JSONObject.toJSONString(attachmentMap);
            request = new IndexRequest("data_archives_attachment");
            request.id(UUID.randomUUID().toString());
            request.setPipeline("single_attachment");
            request.source(jsonString, XContentType.JSON);
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            result = response.status().toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ElasticsearchException e) {
            e.getDetailedMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @GetMapping(value = "/deleteDocumentById")
    public String deleteDocumentById(String id, String index) {
        String result = "error";
        DeleteRequest deleteRequest;
        DeleteResponse deleteResponse;
        try {
            deleteRequest = new DeleteRequest(index, id);
            deleteRequest.timeout(TimeValue.timeValueMinutes(2));
            deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
            result = deleteResponse.getResult().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
