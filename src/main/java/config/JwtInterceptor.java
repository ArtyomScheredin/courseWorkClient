package config;

import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.List;

public class JwtInterceptor
        implements ClientHttpRequestInterceptor {

    private final String token;

    public JwtInterceptor(String token) {
        this.token = token;
    }

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution) throws IOException {

        request.getHeaders().remove("Authorization");
        request.getHeaders().add("Authorization", "Bearer " + token);
        request.getHeaders().setAccept(List.of(MediaType.APPLICATION_JSON));
        ClientHttpResponse response = execution.execute(request, body);
        return response;
    }
}