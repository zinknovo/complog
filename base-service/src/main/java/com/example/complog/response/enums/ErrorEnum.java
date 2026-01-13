package com.example.complog.response.enums;

public enum ErrorEnum {

    SUCCESS(200, "成功"),
    FAILED(300, "失败")

    ;
    /**
     * 构造方法
     */
    private final int code;
    private final String msg;

    ErrorEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 获取状态码
     *
     * @return Long
     * @author fzr
     */
    public int getCode() {
        return this.code;
    }

    /**
     * 获取提示
     *
     * @return String
     * @author fzr
     */
    public String getMsg() {
        return this.msg;
    }

}
