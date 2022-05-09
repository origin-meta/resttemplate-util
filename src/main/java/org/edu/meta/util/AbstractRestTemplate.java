package org.edu.meta.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.edu.meta.exception.RequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author scott
 * @since 20.04.2022
 */
public abstract class AbstractRestTemplate {

    private final static Logger logger = LoggerFactory.getLogger(AbstractRestTemplate.class);

    @Autowired
    protected RestTemplate restTemplate;

    /**
     * 返回体
     */
    protected Class<?> responseWrapper;

    @Autowired
    protected ObjectMapper objectMapper;


    protected AbstractRestTemplate(Class<?> responseWrapper) {
        this.responseWrapper = responseWrapper;
    }

    protected void doPost(String path) {
        this.exchange(path, HttpMethod.POST, emptyMap(), emptyMap(), null);
    }

    protected void doPost(String path, MultiValueMap<String, String> requestBody) {
        this.exchange(path, HttpMethod.POST, emptyMap(), requestBody, null);
    }

    protected <T> T doPost(String path, MultiValueMap<String, String> requestBody, Class<T> responseEntity) {
        return this.exchange(path, HttpMethod.POST, emptyMap(), requestBody, responseEntity);
    }

    protected <T> T doPost(String path, Class<T> responseEntity) {
        return this.exchange(path, HttpMethod.POST, emptyMap(), emptyMap(), responseEntity);
    }

    protected void doPut(String path, MultiValueMap<String, String> requestBody) {
        this.exchange(path, HttpMethod.PUT, emptyMap(), requestBody, null);
    }

    protected <T> T doGet(String path, Class<T> responseEntity) {
        return this.doGet(path, emptyMap(), responseEntity);
    }

    protected <T> T doGet(String path, MultiValueMap<String, String> requestParams, Class<T> responseEntity) {
        return this.exchange(path, HttpMethod.GET, requestParams, null, responseEntity);
    }

    /**
     * 针对请求重新调整接口
     *
     * @param path           访问路径
     * @param httpMethod     请求方式
     * @param requestParams  请求参数
     * @param requestBody    请求体
     * @param responseEntity 返回实体
     * @return 返回指定业务对象
     */
    protected <T> T exchange(String path, HttpMethod httpMethod,
                             MultiValueMap<String, String> requestParams,
                             MultiValueMap<String, String> requestBody,
                             Class<T> responseEntity) {
        return this.exchange(path, httpMethod, getHttpHeaders(), requestParams, requestBody, responseEntity);
    }


    /**
     * 针对请求重新调整接口
     *
     * @param path           访问路径
     * @param httpMethod     请求方式
     * @param httpHeaders    请求头信息
     * @param requestParams  请求参数
     * @param requestBody    请求体
     * @param responseEntity 返回实体
     * @return 返回指定业务对象
     */
    protected <T> T exchange(String path, HttpMethod httpMethod,
                             HttpHeaders httpHeaders,
                             MultiValueMap<String, String> requestParams,
                             MultiValueMap<String, String> requestBody,
                             Class<T> responseEntity) {
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(requestBody, httpHeaders);
        String requestPath = generatePath(path);
        ResponseEntity<?> response = restTemplate.exchange(requestPath, httpMethod, httpEntity, responseWrapper, requestParams);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RequestException("请求异常");
        }
        if (responseEntity == null) {
            return null;
        } else {
            Object body = resolveEntity(response.getBody());
            return objectMapper.convertValue(body, responseEntity);
        }
    }


    public MultiValueMap<String, String> emptyMap() {
        return new LinkedMultiValueMap<>();
    }

    /**
     * 获取请求header头
     */
    protected abstract HttpHeaders getHttpHeaders();

    /**
     * 请求路径
     */
    protected abstract String generatePath(String path);

    /**
     * 解析实体
     */
    protected abstract Object resolveEntity(Object data);

}
