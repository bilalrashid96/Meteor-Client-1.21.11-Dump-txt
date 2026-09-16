package meteordevelopment.meteorclient.utils.network;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodySubscribers;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;

public record JsonBodyHandler<T>(HttpResponse.BodySubscriber<InputStream> delegate, Gson gson, Type type) implements HttpResponse.BodySubscriber<T> {
   public static <T> HttpResponse.BodyHandler<T> ofJson(Gson gson, Type type) {
      return (responseInfo) -> new JsonBodyHandler(BodySubscribers.ofInputStream(), gson, type);
   }

   public CompletionStage<T> getBody() {
      return this.delegate.getBody().thenApply((in) -> in == null ? null : this.gson.fromJson(new InputStreamReader(in), this.type));
   }

   public void onSubscribe(Flow.Subscription subscription) {
      this.delegate.onSubscribe(subscription);
   }

   public void onNext(List<ByteBuffer> item) {
      this.delegate.onNext(item);
   }

   public void onError(Throwable throwable) {
      this.delegate.onError(throwable);
   }

   public void onComplete() {
      this.delegate.onComplete();
   }
}
