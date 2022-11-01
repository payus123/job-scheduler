package com.blusalt.dbxpbackgroundservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class HTTPHelper {

    private final RestTemplate restTemplate;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public HTTPHelper(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public static URI buildURI(final String baseUrl, final Map<String, String> requestParameters) throws URISyntaxException {
        final URIBuilder uriBuilder = new URIBuilder(baseUrl);
        if (requestParameters != null) {
            requestParameters.forEach((key, value) -> {
                if (value != null && !value.isEmpty()) {
                    uriBuilder.setParameter(key, value);
                }
            });
        }
        return uriBuilder.build();
    }

    public static URI buildURIWithQueryParams(final String baseUrl, final Map<String, String> queryParams) throws URISyntaxException {
        final URIBuilder uriBuilder = new URIBuilder(baseUrl + resolveQueryParamPath(queryParams));
        return uriBuilder.build();
    }

    public static String resolveQueryParamPath(final Map<String, String> queryParams) {
        final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("");
        if (queryParams != null) {
            queryParams.forEach((key, value) -> {
                if (value != null && !value.isEmpty()) {
                    builder.queryParam(key, value);
                }
            });
        }
        return builder.toUriString();
    }

    public <T> T postRequest(String baseUrl, Object request, Class<T> responseType, HttpHeaders headers, String method) throws Exception {
        HttpEntity<Object> entity = new HttpEntity<>(request, headers);
        String response = null;
        final ResponseEntity<String> responseEntity = restTemplate.exchange(HTTPHelper.buildURI(baseUrl, null), HttpMethod.resolve(method), entity, String.class);
        response = responseEntity.getBody();
        if (String.class.equals(responseType)) {
            return (T) response;
        }
        return OBJECT_MAPPER.readValue(response, responseType);
    }

    public <T> T getRequest(String baseUrl, Class<T> responseType, HttpHeaders headers, Map<String, String> requestParams) throws Exception {
        Object response = null;
        URI uri = null;
        uri = HTTPHelper.buildURIWithQueryParams(baseUrl, requestParams);
        HttpEntity<Object> entity = new HttpEntity(headers);
        final ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        response = responseEntity.getBody();
        if (String.class.equals(responseType)) {
            return (T) response;
        }
        return (T) response;
    }

    public static HttpHeaders createHeaders(Map<String, String> additionalHeaderAttribute) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (additionalHeaderAttribute != null) {
            additionalHeaderAttribute.forEach(headers::set);
        }

        return headers;
    }
}
