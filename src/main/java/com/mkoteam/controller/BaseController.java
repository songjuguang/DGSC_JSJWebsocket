package com.mkoteam.controller;

import com.alibaba.fastjson.JSONObject;
import com.mkoteam.until.MKOResponse;
import com.mkoteam.until.MKOResponseCode;
import com.mkoteam.until.MKOResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kevin on 2/18/17.
 */
public class BaseController {
    

    public MKOResponse makeResponse(MKOResponseCode response, Object data) {
        MKOResponse result = new MKOResponse();
        result.put("code", response.getCode());
        result.put("response", data);
        result.put("msg", response.getDesc());
        return result;
    }

    public MKOResponse makeResponse(MKOResponseCode response, Object data, String desc) {
        MKOResponse result = new MKOResponse();
        result.put("code", response.getCode());
        result.put("response", data);
        if (desc != null)
            result.put("msg", desc);
        return result;
    }


    public MKOResponse makeSuccessResponse(Object data) {
        return this.makeResponse(MKOResponseCode.Success, data);
    }

    public MKOResponse makeResponseByMKOResult(MKOResult mkoResult) {
        MKOResponse result = new MKOResponse();
        if (mkoResult.success)
            result.put("code", MKOResponseCode.Success.getCode());
        else
            result.put("code", MKOResponseCode.BusinessError);
        if (mkoResult.getMessage() != null)
            result.put("msg", mkoResult.getMessage());
        return result;
    }

    public MKOResponse makeBussessErrorResponse(String desc) {
        return this.makeResponse(MKOResponseCode.BusinessError, null, desc);
    }

    public MKOResponse makeParamsLackResponse(String desc) {
        return this.makeResponse(MKOResponseCode.ParamsLack, null, desc);
    }


    public Object listToString(List list, int page, int pageCount, int count, int countNumber) {
        Map<String, Object> map = new HashMap<String, Object>();
        // 当前页数
        map.put("page", page);
        // 总页数
        map.put("pageCount", pageCount);
        // 当前页数数量
        map.put("count", count);
        // 总数量
        map.put("countNumber", countNumber);
        // 数据
        map.put("datas", list);
        return map;
    }

    public Object convertPageResult(Page data) {

        Map<String, Object> map = new HashMap<String, Object>();
        // 当前页数
        map.put("page", data.getNumber() + 1);
        // 总页数
        map.put("pageCount", data.getTotalPages());
        // 当前页数数量
        map.put("count", data.getSize());
        // 总数量
        map.put("countNumber", data.getTotalElements());
        // 数据
        map.put("datas", data.getContent());

        return map;
    }




}
