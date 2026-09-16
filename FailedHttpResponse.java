package meteordevelopment.meteorclient.utils.network;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.net.ssl.SSLSession;

public record FailedHttpResponse<T>(HttpRequest request, Exception exception) implements HttpResponse<T> {
   public int statusCode() {
      return 400;
   }

   public Optional<HttpResponse<T>> previousResponse() {
      return Optional.empty();
   }

   public HttpHeaders headers() {
      return HttpHeaders.of(Map.of(), (s1, s2) -> true);
   }

   public T body() {
      return null;
   }

   public Optional<SSLSession> sslSession() {
      return Optional.empty();
   }

   public URI uri() {
      return this.request.uri();
   }

   @Nullable
   public HttpClient.Version version() {
      return (HttpClient.Version)this.request.version().orElse((Object)null);
   }
}
