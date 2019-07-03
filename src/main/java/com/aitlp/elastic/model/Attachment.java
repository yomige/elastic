package com.aitlp.elastic.model;

import lombok.Data;

import java.util.Map;

@Data
public class Attachment {
    private String id;
    private String data;
    private String fileName;
    private String filePath;
    private Map<String, String> attachment;
}
