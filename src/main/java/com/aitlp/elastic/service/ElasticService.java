package com.aitlp.elastic.service;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.List;

@Service
public class ElasticService {
    @Autowired
    public RestHighLevelClient client;

    @Value("${elasticsearch.single-pipline}")
    public String singlePipline;

    /**
     * 根据ID删除文档
     *
     * @param id    文档ID
     * @param index 索引
     * @return
     */
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

    /**
     * 构建SearchSourceBuilder
     *
     * @param queryBuilders     查询构建项
     * @param highlightItems 高亮项
     * @return
     */
    public SearchSourceBuilder createSearchSourceBuilder(List<QueryBuilder> queryBuilders, List<String> highlightItems) {
        SearchSourceBuilder searchBuilder = new SearchSourceBuilder();
        if (!ObjectUtils.isEmpty(queryBuilders)) {
            // 查询
            for (QueryBuilder queryBuilder : queryBuilders) {
                searchBuilder.query(queryBuilder);
            }
            // 设置高亮
            if (!ObjectUtils.isEmpty(highlightItems)) {
                searchBuilder.highlighter(createHighlightBuilder(highlightItems));
            }
        }
        return searchBuilder;
    }

    /**
     * 构建高亮字段
     *
     * @param highlightItems
     * @return
     */
    public HighlightBuilder createHighlightBuilder(List<String> highlightItems) {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        if (!ObjectUtils.isEmpty(highlightItems)) {
            for (String highlightItem : highlightItems) {
                highlightBuilder.field(new HighlightBuilder.Field(highlightItem));
            }
        }
        highlightBuilder.preTags("<span class=\"highlight\">");
        highlightBuilder.postTags("</span>");
        return highlightBuilder;
    }
}
