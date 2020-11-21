package okhttp3.internal.cache;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.cache.CacheStrategy;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http.HttpMethod;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;
import okio.Timeout;

public final class CacheInterceptor implements Interceptor {
    final InternalCache cache;

    public CacheInterceptor(InternalCache internalCache) {
        this.cache = internalCache;
    }

    @Override // okhttp3.Interceptor
    public Response intercept(Interceptor.Chain chain) throws IOException {
        InternalCache internalCache = this.cache;
        Response response = internalCache != null ? internalCache.get(chain.request()) : null;
        CacheStrategy cacheStrategy = new CacheStrategy.Factory(System.currentTimeMillis(), chain.request(), response).get();
        Request request = cacheStrategy.networkRequest;
        Response response2 = cacheStrategy.cacheResponse;
        InternalCache internalCache2 = this.cache;
        if (internalCache2 != null) {
            internalCache2.trackResponse(cacheStrategy);
        }
        if (response != null && response2 == null) {
            Util.closeQuietly(response.body());
        }
        if (request == null && response2 == null) {
            Response.Builder builder = new Response.Builder();
            builder.request(chain.request());
            builder.protocol(Protocol.HTTP_1_1);
            builder.code(504);
            builder.message("Unsatisfiable Request (only-if-cached)");
            builder.body(Util.EMPTY_RESPONSE);
            builder.sentRequestAtMillis(-1);
            builder.receivedResponseAtMillis(System.currentTimeMillis());
            return builder.build();
        } else if (request == null) {
            Response.Builder newBuilder = response2.newBuilder();
            newBuilder.cacheResponse(stripBody(response2));
            return newBuilder.build();
        } else {
            try {
                Response proceed = chain.proceed(request);
                if (proceed == null && response != null) {
                }
                if (response2 != null) {
                    if (proceed.code() == 304) {
                        Response.Builder newBuilder2 = response2.newBuilder();
                        newBuilder2.headers(combine(response2.headers(), proceed.headers()));
                        newBuilder2.sentRequestAtMillis(proceed.sentRequestAtMillis());
                        newBuilder2.receivedResponseAtMillis(proceed.receivedResponseAtMillis());
                        newBuilder2.cacheResponse(stripBody(response2));
                        newBuilder2.networkResponse(stripBody(proceed));
                        Response build = newBuilder2.build();
                        proceed.body().close();
                        this.cache.trackConditionalCacheHit();
                        this.cache.update(response2, build);
                        return build;
                    }
                    Util.closeQuietly(response2.body());
                }
                Response.Builder newBuilder3 = proceed.newBuilder();
                newBuilder3.cacheResponse(stripBody(response2));
                newBuilder3.networkResponse(stripBody(proceed));
                Response build2 = newBuilder3.build();
                if (this.cache != null) {
                    if (HttpHeaders.hasBody(build2) && CacheStrategy.isCacheable(build2, request)) {
                        return cacheWritingResponse(this.cache.put(build2), build2);
                    }
                    if (HttpMethod.invalidatesCache(request.method())) {
                        try {
                            this.cache.remove(request);
                        } catch (IOException unused) {
                        }
                    }
                }
                return build2;
            } finally {
                if (response != null) {
                    Util.closeQuietly(response.body());
                }
            }
        }
    }

    private static Response stripBody(Response response) {
        if (response == null || response.body() == null) {
            return response;
        }
        Response.Builder newBuilder = response.newBuilder();
        newBuilder.body(null);
        return newBuilder.build();
    }

    private Response cacheWritingResponse(final CacheRequest cacheRequest, Response response) throws IOException {
        Sink body;
        if (cacheRequest == null || (body = cacheRequest.body()) == null) {
            return response;
        }
        final BufferedSource source = response.body().source();
        final BufferedSink buffer = Okio.buffer(body);
        AnonymousClass1 r2 = new Source(this) {
            /* class okhttp3.internal.cache.CacheInterceptor.AnonymousClass1 */
            boolean cacheRequestClosed;

            @Override // okio.Source
            public long read(Buffer buffer, long j) throws IOException {
                try {
                    long read = source.read(buffer, j);
                    if (read == -1) {
                        if (!this.cacheRequestClosed) {
                            this.cacheRequestClosed = true;
                            buffer.close();
                        }
                        return -1;
                    }
                    buffer.copyTo(buffer.buffer(), buffer.size() - read, read);
                    buffer.emitCompleteSegments();
                    return read;
                } catch (IOException e) {
                    if (!this.cacheRequestClosed) {
                        this.cacheRequestClosed = true;
                        cacheRequest.abort();
                    }
                    throw e;
                }
            }

            @Override // okio.Source
            public Timeout timeout() {
                return source.timeout();
            }

            @Override // okio.Source, java.io.Closeable, java.lang.AutoCloseable
            public void close() throws IOException {
                if (!this.cacheRequestClosed && !Util.discard(this, 100, TimeUnit.MILLISECONDS)) {
                    this.cacheRequestClosed = true;
                    cacheRequest.abort();
                }
                source.close();
            }
        };
        Response.Builder newBuilder = response.newBuilder();
        newBuilder.body(new RealResponseBody(response.headers(), Okio.buffer(r2)));
        return newBuilder.build();
    }

    private static Headers combine(Headers headers, Headers headers2) {
        Headers.Builder builder = new Headers.Builder();
        int size = headers.size();
        for (int i = 0; i < size; i++) {
            String name = headers.name(i);
            String value = headers.value(i);
            if ((!"Warning".equalsIgnoreCase(name) || !value.startsWith("1")) && (!isEndToEnd(name) || headers2.get(name) == null)) {
                Internal.instance.addLenient(builder, name, value);
            }
        }
        int size2 = headers2.size();
        for (int i2 = 0; i2 < size2; i2++) {
            String name2 = headers2.name(i2);
            if (!"Content-Length".equalsIgnoreCase(name2) && isEndToEnd(name2)) {
                Internal.instance.addLenient(builder, name2, headers2.value(i2));
            }
        }
        return builder.build();
    }

    static boolean isEndToEnd(String str) {
        return !"Connection".equalsIgnoreCase(str) && !"Keep-Alive".equalsIgnoreCase(str) && !"Proxy-Authenticate".equalsIgnoreCase(str) && !"Proxy-Authorization".equalsIgnoreCase(str) && !"TE".equalsIgnoreCase(str) && !"Trailers".equalsIgnoreCase(str) && !"Transfer-Encoding".equalsIgnoreCase(str) && !"Upgrade".equalsIgnoreCase(str);
    }
}
