package com.tlf.cloud.storage.server.mq;

import com.tlf.cloud.storage.server.bo.MqSliceInfo;
import com.tlf.cloud.storage.server.service.FileService;
import com.tlf.commonlang.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class MqFileService {
    @Autowired
    MqProcessor mqProcessor;

    @Autowired
    FileService fileService;

    public void fileMergeOutput(MqSliceInfo sliceInfo) {
        String payload = JsonUtil.getJsonFromObject(sliceInfo);
        mqProcessor.fileMergeOutput().send(MessageBuilder.withPayload(payload).build());
    }

    @StreamListener(MqProcessor.FILE_MERGE_INPUT)
    public void fileMergeInput(Message<String> message) {
        if (null == message.getPayload()) {
            return;
        }
        String payload = message.getPayload();
        MqSliceInfo sliceInfo = JsonUtil.getObjectFromJson(payload, MqSliceInfo.class);
        fileService.merge(sliceInfo);
    }
}
