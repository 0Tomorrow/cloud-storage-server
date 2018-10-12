package com.tlf.cloud.storage.server.mq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Component;

@Component
public interface MqProcessor {

    String FILE_MERGE_INPUT = "file_merge_input";

    String FILE_MERGE_OUTPUT = "file_merge_output";

    @Input(FILE_MERGE_INPUT)
    SubscribableChannel fileMergeInput();
    
    @Output(FILE_MERGE_OUTPUT)
    SubscribableChannel fileMergeOutput();
}
