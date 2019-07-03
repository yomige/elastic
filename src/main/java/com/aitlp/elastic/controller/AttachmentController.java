package com.aitlp.elastic.controller;

import com.aitlp.elastic.model.Attachment;
import com.aitlp.elastic.service.AttachmentService;
import com.base.model.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/attachment")
public class AttachmentController {
    @Autowired
    private AttachmentService attachmentService;

    @GetMapping(value = "/list")
    public Page<Attachment> list(int curPage, int limit, Attachment attachment) {
        return attachmentService.list(curPage, limit, attachment);
    }

    @GetMapping(value = "/indexAttachment")
    public String indexAttachment(String fileFullPath, String fileName) {
        return attachmentService.indexAttachment(fileFullPath, fileName);
    }
}
