package com.zhq.executor.register.model;

import java.io.Serializable;
import java.util.Collections;

/**
 * @author: zhenghq
 * @date: 2021/3/11
 * @version: 1.0.0
 */
public class ResultDto implements Serializable {
    private static final long serialVersionUID = -1L;

    private String code;
    private String msg;
    private Object data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ResultDto() {
        super();
        this.code = "0";
        this.msg = "success";
        this.data = Collections.emptyMap();
    }

    @Override
    public String toString() {
        return "ResultDto{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public static ResultDto success(String code, Object data) {
        ResultDto resultDto = new ResultDto();
        resultDto.setCode(code);
        resultDto.setMsg("success");
        if (data != null) {
            resultDto.setData(data);
        }
        return resultDto;
    }

    public static ResultDto success(Object data) {
        ResultDto resultDto = new ResultDto();
        resultDto.setCode("0");
        resultDto.setMsg("success");
        if (data != null) {
            resultDto.setData(data);
        }
        return resultDto;
    }

    public static ResultDto success() {
        return success(null);
    }

    public static ResultDto error(String code, String msg) {
        ResultDto resultDto = new ResultDto();
        resultDto.setCode(code);
        resultDto.setMsg(msg);
        return resultDto;
    }
}
