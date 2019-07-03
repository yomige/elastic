package com.aitlp.elastic.service;

import com.aitlp.elastic.model.Attachment;
import com.alibaba.fastjson.JSONObject;
import com.base.model.Page;
import com.base.util.UUIDUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.*;
import java.util.*;

@Service
public class AttachmentService extends ElasticService {

    /**
     * 分页查询附件
     *
     * @param curPage
     * @param limit
     * @param attachment
     * @return
     */
    public Page<Attachment> list(int curPage, int limit, Attachment attachment) {
        List<Attachment> attachmentList = new ArrayList<>();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (StringUtils.isNotBlank(attachment.getFileName())) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("fileName", attachment.getFileName()));
        }

        if (!ObjectUtils.isEmpty(attachment.getAttachment())) {
            if (StringUtils.isNotBlank(attachment.getAttachment().get("content"))) {
                boolQueryBuilder.should(QueryBuilders.matchQuery("attachment.content", attachment.getAttachment().get("content")));
            }
        }

        List<String> highlightItems = Arrays.asList("fileName", "attachment.content");
        List<QueryBuilder> queryBuilders = new ArrayList<>();
        queryBuilders.add(boolQueryBuilder);
        SearchSourceBuilder sourceBuilder = createSearchSourceBuilder(queryBuilders, highlightItems);
        sourceBuilder.from(curPage - 1);
        sourceBuilder.size(limit);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("data_archives_attachment");
        searchRequest.source(sourceBuilder);

        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            RestStatus restStatus = searchResponse.status();
            if (restStatus == RestStatus.OK) {
                SearchHits searchHits = searchResponse.getHits();
                for (SearchHit hit : searchHits.getHits()) {
                    attachmentList.add(dealSearchResult(hit));
                }
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        return new Page(attachmentList);
    }

    /**
     * 附件建立索引
     *
     * @param fileFullPath
     * @param fileName
     * @return
     */
    public String indexAttachment(String fileFullPath, String fileName) {
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
            attachmentMap.put("fileName", fileName);
            String jsonString = JSONObject.toJSONString(attachmentMap);
            request = new IndexRequest("data_archives_attachment");
            request.id(UUIDUtil.uuid());
            request.setPipeline(singlePipline);
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

    public Attachment dealSearchResult(SearchHit hit) {
        String source = hit.getSourceAsString();
        Attachment attachment = JSONObject.parseObject(source, Attachment.class);
        attachment.setId(hit.getId());
        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
        if (highlightFields.containsKey("fileName")) {
            HighlightField highlight = highlightFields.get("fileName");
            if (highlight != null) {
                Text[] fragments = highlight.fragments();
                String fragmentString = fragments[0].string();
                attachment.setFileName(fragmentString);
            }
        }
        if (highlightFields.containsKey("attachment.content")) {
            HighlightField highlight = highlightFields.get("attachment.content");
            if (highlight != null) {
                Text[] fragments = highlight.fragments();
                String fragmentString = fragments[0].string();
                attachment.getAttachment().put("content", fragmentString);
            }
        }
        return attachment;
    }
}
