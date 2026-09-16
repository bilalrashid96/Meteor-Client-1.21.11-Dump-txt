package meteordevelopment.meteorclient.utils.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import meteordevelopment.meteorclient.utils.other.JsonDateDeserializer;

public class Http {
   public static final int SUCCESS = 200;
   public static final int BAD_REQUEST = 400;
   public static final int UNAUTHORIZED = 401;
   public static final int FORBIDDEN = 403;
   public static final int NOT_FOUND = 404;
   private static final HttpClient CLIENT = HttpClient.newBuilder().executor(Executors.newVirtualThreadPerTaskExecutor()).build();
   private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();

   public static Request get(String url) {
      return new Request(Http.Method.GET, url);
   }

   public static Request post(String url) {
      return new Request(Http.Method.POST, url);
   }

   private static enum Method {
      GET,
      POST;

      // $FF: synthetic method
      private static Method[] $values() {
         return new Method[]{GET, POST};
      }
   }

   public static class Request {
      private final HttpRequest.Builder builder;
      private Method method;
      private Consumer<Exception> exceptionHandler = Throwable::printStackTrace;

      private Request(Method method, String url) {
         try {
            this.builder = HttpRequest.newBuilder().uri(new URI(url)).header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");
            this.method = method;
         } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
         }
      }

      public Request header(String name, String value) {
         this.builder.header(name, value);
         return this;
      }

      public Request bearer(String token) {
         this.builder.header("Authorization", "Bearer " + token);
         return this;
      }

      public Request bodyString(String string) {
         this.builder.header("Content-Type", "text/plain");
         this.builder.method(this.method.name(), BodyPublishers.ofString(string));
         this.method = null;
         return this;
      }

      public Request bodyForm(String string) {
         this.builder.header("Content-Type", "application/x-www-form-urlencoded");
         this.builder.method(this.method.name(), BodyPublishers.ofString(string));
         this.method = null;
         return this;
      }

      public Request bodyJson(String string) {
         this.builder.header("Content-Type", "application/json");
         this.builder.method(this.method.name(), BodyPublishers.ofString(string));
         this.method = null;
         return this;
      }

      public Request bodyJson(Object object) {
         this.builder.header("Content-Type", "application/json");
         this.builder.method(this.method.name(), BodyPublishers.ofString(Http.GSON.toJson(object)));
         this.method = null;
         return this;
      }

      public Request ignoreExceptions() {
         this.exceptionHandler = (e) -> {
         };
         return this;
      }

      public Request exceptionHandler(Consumer<Exception> exceptionHandler) {
         this.exceptionHandler = exceptionHandler;
         return this;
      }

      private <T> HttpResponse<T> _sendResponse(String accept, HttpResponse.BodyHandler<T> responseBodyHandler) {
         this.builder.header("Accept", accept);
         if (this.method != null) {
            this.builder.method(this.method.name(), BodyPublishers.noBody());
         }

         HttpRequest request = this.builder.build();

         try {
            return Http.CLIENT.send(request, responseBodyHandler);
         } catch (InterruptedException | IOException e) {
            this.exceptionHandler.accept(e);
            return new FailedHttpResponse<T>(request, e);
         }
      }

      @Nullable
      private <T> T _send(String accept, HttpResponse.BodyHandler<T> responseBodyHandler) {
         HttpResponse<T> res = this.<T>_sendResponse(accept, responseBodyHandler);
         return (T)(res.statusCode() == 200 ? res.body() : null);
      }

      public void send() {
         this._send("*/*", BodyHandlers.discarding());
      }

      public HttpResponse<Void> sendResponse() {
         return this.<Void>_sendResponse("*/*", BodyHandlers.discarding());
      }

      @Nullable
      public InputStream sendInputStream() {
         return (InputStream)this._send("*/*", BodyHandlers.ofInputStream());
      }

      public HttpResponse<InputStream> sendInputStreamResponse() {
         return this.<InputStream>_sendResponse("*/*", BodyHandlers.ofInputStream());
      }

      @Nullable
      public String sendString() {
         return (String)this._send("*/*", BodyHandlers.ofString());
      }

      public HttpResponse<String> sendStringResponse() {
         return this.<String>_sendResponse("*/*", BodyHandlers.ofString());
      }

      @Nullable
      public Stream<String> sendLines() {
         return (Stream)this._send("*/*", BodyHandlers.ofLines());
      }

      public HttpResponse<Stream<String>> sendLinesResponse() {
         return this.<Stream<String>>_sendResponse("*/*", BodyHandlers.ofLines());
      }

      @Nullable
      public <T> T sendJson(Type type) {
         return (T)this._send("application/json", JsonBodyHandler.ofJson(Http.GSON, type));
      }

      public <T> HttpResponse<T> sendJsonResponse(Type type) {
         return this.<T>_sendResponse("*/*", JsonBodyHandler.ofJson(Http.GSON, type));
      }
   }
}
