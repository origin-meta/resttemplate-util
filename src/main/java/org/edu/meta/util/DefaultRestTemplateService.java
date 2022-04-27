package org.edu.meta.util;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * @author scott
 * @since 27.04.2022
 */
@Component("restTemplateService")
public class DefaultRestTemplateService extends AbstractRestTemplate {

    static final String SLASH = "/";

    private static final String URL_PATTERN = "http://127.0.0.1/{}";

    protected DefaultRestTemplateService() {
        super(Res.class);
    }

    @Override
    protected HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AuthorizationInfo.TOKEN_KEY, AuthorizationInfo.token);
        return headers;
    }

    @Override
    protected String generatePath(String path) {
        if (path.startsWith(SLASH)) {
            path = path.substring(1);
        }
        return String.format(URL_PATTERN, path);
    }

    static class AuthorizationInfo {
        final static String TOKEN_KEY = "token";

        private static String token = "test";

    }

}
