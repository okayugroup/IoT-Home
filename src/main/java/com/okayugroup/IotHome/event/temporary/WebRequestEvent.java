package com.okayugroup.IotHome.event.temporary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okayugroup.IotHome.event.EventResult;
import com.okayugroup.IotHome.event.TemporaryEvent;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class WebRequestEvent<T> extends TemporaryEvent<T> {
    protected WebRequestEvent(String name, String... args) {
        super("HTTPリクエスト", name, args);
    }
    protected String url;
    protected int timeout;
    protected Map<String, String> headers;
    protected final ObjectMapper objectMapper = new ObjectMapper();
    protected String content;


    @Override
    public @NotNull String @NotNull [] getArgs() {
        String value = "{}";
        try {
            value = objectMapper.writeValueAsString(headers);
        } catch (JsonProcessingException ignored) { }
        return new String[]{url, Long.toString(timeout), value, content};

    }
    @Override
    public WebRequestEvent<T> setArgs(String... args) {
        url = args.length > 0 ? args[0] : null;
        timeout = args.length > 1 ? Integer.parseInt(args[1]) : 300;
        try {
            headers = args.length > 2 ? objectMapper.readValue(args[2], new TypeReference<>(){}) : Map.of();
        } catch (JsonProcessingException e) {
            headers = Map.of();
        }
        content = args.length > 3 ? args[3] : null;
        return this;
    }


    @Override
    public abstract WebRequestEvent<T> getCopy();
    public static class GetRequest extends WebRequestEvent<String> {
        public GetRequest(String... args) {
            super("GETリクエスト", args);
        }

        @Override
        public EventResult<String> execute(@Nullable EventResult<?> previousResult) {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet request = new HttpGet(url);
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    request.addHeader(entry.getKey(), entry.getValue());
                }
                request.setConfig(RequestConfig.copy(RequestConfig.DEFAULT).setConnectionRequestTimeout(timeout).build());
                HttpResponse response = httpClient.execute(request);
                int responseCode = response.getStatusLine().getStatusCode();
                System.out.println("Response Code: " + responseCode);
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    // レスポンスの内容を文字列として取得
                    String result = EntityUtils.toString(entity);
                    return new EventResult<>(null, result);
                } else {
                    return new EventResult<>(null, null);
                }
            } catch (Exception e) {
                return new EventResult<>(e, null);
            }
        }

        @Override
        public GetRequest getCopy() {
            return new GetRequest();
        }
    }
    public static class PostRequest extends WebRequestEvent<String> {
        public PostRequest(String... args) {
            super("POSTリクエスト", args);
        }

        @Override
        public EventResult<String> execute(EventResult<?> previousResult) {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost request = new HttpPost(url);
                request.setEntity(new StringEntity(content));
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    request.addHeader(entry.getKey(), entry.getValue());
                }
                request.setConfig(RequestConfig.copy(RequestConfig.DEFAULT).setConnectionRequestTimeout(timeout).build());
                HttpResponse response = httpClient.execute(request);
                int responseCode = response.getStatusLine().getStatusCode();
                System.out.println("Response Code: " + responseCode);
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    // レスポンスの内容を文字列として取得
                    String result = EntityUtils.toString(entity);
                    return new EventResult<>(null, result);
                } else {
                    return new EventResult<>(null, null);
                }
            } catch (Exception e) {
                return new EventResult<>(e, null);
            }
        }

        @Override
        public PostRequest getCopy() {
            return new PostRequest();
        }
    }
}
