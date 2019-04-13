package com.lontyu.dwjwap.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *  响应前端
 * @param <T>
 */
@JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
@ApiModel
public class BaseResponse<T> {
    @ApiModelProperty(value="编码 0：调用接口成功，1调用接口异常")
    private Integer code;
    @ApiModelProperty(value="消息")
    private String message;
    @ApiModelProperty(value="业务数据")
    private T      data;
    public static final int SUCCESS_CODE = 0;
    public static final int FAIL_CODE = 1;
    private int    totalCount;
    public BaseResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BaseResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public BaseResponse(Integer code, String msg) {
        this.code = code;
        this.message = msg;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.message = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code='" + code + '\'' +
                ", msg='" + message + '\'' +
                ", data=" + data +
                '}';
    }
    public boolean success() {
        return this.code != null && "1".equals(this.code);
    }

    public static BaseResponse buildFail(Integer code, String message) {
        return new BaseResponse(code, message);
    }

    public static <T> BaseResponse<T> buildSuccess(T data) {
        return new BaseResponse<>(SUCCESS_CODE, "OK.", data);
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
