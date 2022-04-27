package org.edu.meta.util;

import org.edu.meta.exception.RequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author scott
 * @since 20.04.2022
 */
public abstract class AbstractRestTemplate {

    @Autowired
    protected RestTemplate restTemplate;

    /**
     * 返回体
     */
    private Class<?> responseWrapper;

    protected AbstractRestTemplate(Class<?> responseWrapper) {
        this.responseWrapper = responseWrapper;
    }

    public void setResponseWrapper(Class<?> responseWrapper) {
        this.responseWrapper = responseWrapper;
    }

    protected void doPost(String path) {
        this.exchange(path, HttpMethod.POST, emptyMap(), null, null);
    }

    protected void doPost(String path, MultiValueMap<String, String> requestBody) {
        this.exchange(path, HttpMethod.POST, emptyMap(), requestBody, null);
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
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RequestException("网络请求异常");
        }
        return responseEntity != null ? responseEntity.cast(response.getBody()) : null;
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

}
