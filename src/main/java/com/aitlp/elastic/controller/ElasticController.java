package com.aitlp.elastic.controller;

import com.aitlp.elastic.service.ElasticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/elastic")
public class ElasticController {

    @Autowired
    private ElasticService elasticService;

    @GetMapping(value = "/deleteDocumentById")
    public String deleteDocumentById(String id, String index) {
        return elasticService.deleteDocumentById(id, index);
    }

}
