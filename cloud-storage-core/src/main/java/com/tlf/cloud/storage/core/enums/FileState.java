package com.tlf.cloud.storage.core.enums;

public enum  FileState {
    HANDSHAKE(1, "完成握手"),
    UPLOADING(2, "正在上传"),
    FINISH(3, "上传完成");

    private Integer code;
    private String msg;
    FileState(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
