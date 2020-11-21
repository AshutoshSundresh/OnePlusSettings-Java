package com.bumptech.glide;

import android.content.ComponentCallbacks2;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.data.InputStreamRewinder;
import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.load.model.AssetUriLoader;
import com.bumptech.glide.load.model.ByteArrayLoader;
import com.bumptech.glide.load.model.ByteBufferEncoder;
import com.bumptech.glide.load.model.ByteBufferFileLoader;
import com.bumptech.glide.load.model.DataUrlLoader;
import com.bumptech.glide.load.model.FileLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.MediaStoreFileLoader;
import com.bumptech.glide.load.model.ResourceLoader;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.model.StringLoader;
import com.bumptech.glide.load.model.UnitModelLoader;
import com.bumptech.glide.load.model.UriLoader;
import com.bumptech.glide.load.model.UrlUriLoader;
import com.bumptech.glide.load.model.stream.HttpGlideUrlLoader;
import com.bumptech.glide.load.model.stream.HttpUriLoader;
import com.bumptech.glide.load.model.stream.MediaStoreImageThumbLoader;
import com.bumptech.glide.load.model.stream.MediaStoreVideoThumbLoader;
import com.bumptech.glide.load.model.stream.UrlLoader;
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableDecoder;
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableEncoder;
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.bumptech.glide.load.resource.bitmap.ByteBufferBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.ByteBufferBitmapImageDecoderResourceDecoder;
import com.bumptech.glide.load.resource.bitmap.DefaultImageHeaderParser;
import com.bumptech.glide.load.resource.bitmap.Downsampler;
import com.bumptech.glide.load.resource.bitmap.ExifInterfaceImageHeaderParser;
import com.bumptech.glide.load.resource.bitmap.InputStreamBitmapImageDecoderResourceDecoder;
import com.bumptech.glide.load.resource.bitmap.ResourceBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.StreamBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.UnitBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.VideoDecoder;
import com.bumptech.glide.load.resource.bytes.ByteBufferRewinder;
import com.bumptech.glide.load.resource.drawable.ResourceDrawableDecoder;
import com.bumptech.glide.load.resource.drawable.UnitDrawableDecoder;
import com.bumptech.glide.load.resource.file.FileDecoder;
import com.bumptech.glide.load.resource.gif.ByteBufferGifDecoder;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawableEncoder;
import com.bumptech.glide.load.resource.gif.GifFrameResourceDecoder;
import com.bumptech.glide.load.resource.gif.StreamGifDecoder;
import com.bumptech.glide.load.resource.transcode.BitmapBytesTranscoder;
import com.bumptech.glide.load.resource.transcode.BitmapDrawableTranscoder;
import com.bumptech.glide.load.resource.transcode.DrawableBytesTranscoder;
import com.bumptech.glide.load.resource.transcode.GifDrawableBytesTranscoder;
import com.bumptech.glide.manager.ConnectivityMonitorFactory;
import com.bumptech.glide.manager.RequestManagerRetriever;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.module.ManifestParser;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTargetFactory;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Glide implements ComponentCallbacks2 {
    private static volatile Glide glide;
    private static volatile boolean isInitializing;
    private final ArrayPool arrayPool;
    private final BitmapPool bitmapPool;
    private final ConnectivityMonitorFactory connectivityMonitorFactory;
    private final Engine engine;
    private final GlideContext glideContext;
    private final List<RequestManager> managers = new ArrayList();
    private final MemoryCache memoryCache;
    private final Registry registry;
    private final RequestManagerRetriever requestManagerRetriever;

    public interface RequestOptionsFactory {
        RequestOptions build();
    }

    public void onConfigurationChanged(Configuration configuration) {
    }

    public static Glide get(Context context) {
        if (glide == null) {
            GeneratedAppGlideModule annotationGeneratedGlideModules = getAnnotationGeneratedGlideModules(context.getApplicationContext());
            synchronized (Glide.class) {
                if (glide == null) {
                    checkAndInitializeGlide(context, annotationGeneratedGlideModules);
                }
            }
        }
        return glide;
    }

    private static void checkAndInitializeGlide(Context context, GeneratedAppGlideModule generatedAppGlideModule) {
        if (!isInitializing) {
            isInitializing = true;
            initializeGlide(context, generatedAppGlideModule);
            isInitializing = false;
            return;
        }
        throw new IllegalStateException("You cannot call Glide.get() in registerComponents(), use the provided Glide instance instead");
    }

    @Deprecated
    public static synchronized void init(Glide glide2) {
        synchronized (Glide.class) {
            if (glide != null) {
                tearDown();
            }
            glide = glide2;
        }
    }

    public static void init(Context context, GlideBuilder glideBuilder) {
        GeneratedAppGlideModule annotationGeneratedGlideModules = getAnnotationGeneratedGlideModules(context);
        synchronized (Glide.class) {
            if (glide != null) {
                tearDown();
            }
            initializeGlide(context, glideBuilder, annotationGeneratedGlideModules);
        }
    }

    public static synchronized void tearDown() {
        synchronized (Glide.class) {
            if (glide != null) {
                glide.getContext().getApplicationContext().unregisterComponentCallbacks(glide);
                glide.engine.shutdown();
            }
            glide = null;
        }
    }

    private static void initializeGlide(Context context, GeneratedAppGlideModule generatedAppGlideModule) {
        initializeGlide(context, new GlideBuilder(), generatedAppGlideModule);
    }

    private static void initializeGlide(Context context, GlideBuilder glideBuilder, GeneratedAppGlideModule generatedAppGlideModule) {
        Context applicationContext = context.getApplicationContext();
        List<GlideModule> emptyList = Collections.emptyList();
        if (generatedAppGlideModule == null || generatedAppGlideModule.isManifestParsingEnabled()) {
            emptyList = new ManifestParser(applicationContext).parse();
        }
        if (generatedAppGlideModule != null && !generatedAppGlideModule.getExcludedModuleClasses().isEmpty()) {
            Set<Class<?>> excludedModuleClasses = generatedAppGlideModule.getExcludedModuleClasses();
            Iterator<GlideModule> it = emptyList.iterator();
            while (it.hasNext()) {
                GlideModule next = it.next();
                if (excludedModuleClasses.contains(next.getClass())) {
                    if (Log.isLoggable("Glide", 3)) {
                        Log.d("Glide", "AppGlideModule excludes manifest GlideModule: " + next);
                    }
                    it.remove();
                }
            }
        }
        if (Log.isLoggable("Glide", 3)) {
            Iterator<GlideModule> it2 = emptyList.iterator();
            while (it2.hasNext()) {
                Log.d("Glide", "Discovered GlideModule from manifest: " + it2.next().getClass());
            }
        }
        glideBuilder.setRequestManagerFactory(generatedAppGlideModule != null ? generatedAppGlideModule.getRequestManagerFactory() : null);
        for (GlideModule glideModule : emptyList) {
            glideModule.applyOptions(applicationContext, glideBuilder);
        }
        if (generatedAppGlideModule != null) {
            generatedAppGlideModule.applyOptions(applicationContext, glideBuilder);
        }
        Glide build = glideBuilder.build(applicationContext);
        for (GlideModule glideModule2 : emptyList) {
            try {
                glideModule2.registerComponents(applicationContext, build, build.registry);
            } catch (AbstractMethodError e) {
                throw new IllegalStateException("Attempting to register a Glide v3 module. If you see this, you or one of your dependencies may be including Glide v3 even though you're using Glide v4. You'll need to find and remove (or update) the offending dependency. The v3 module name is: " + glideModule2.getClass().getName(), e);
            }
        }
        if (generatedAppGlideModule != null) {
            generatedAppGlideModule.registerComponents(applicationContext, build, build.registry);
        }
        applicationContext.registerComponentCallbacks(build);
        glide = build;
    }

    private static GeneratedAppGlideModule getAnnotationGeneratedGlideModules(Context context) {
        try {
            return (GeneratedAppGlideModule) Class.forName("com.bumptech.glide.GeneratedAppGlideModuleImpl").getDeclaredConstructor(Context.class).newInstance(context.getApplicationContext());
        } catch (ClassNotFoundException unused) {
            if (!Log.isLoggable("Glide", 5)) {
                return null;
            }
            Log.w("Glide", "Failed to find GeneratedAppGlideModule. You should include an annotationProcessor compile dependency on com.github.bumptech.glide:compiler in your application and a @GlideModule annotated AppGlideModule implementation or LibraryGlideModules will be silently ignored");
            return null;
        } catch (InstantiationException e) {
            throwIncorrectGlideModule(e);
            throw null;
        } catch (IllegalAccessException e2) {
            throwIncorrectGlideModule(e2);
            throw null;
        } catch (NoSuchMethodException e3) {
            throwIncorrectGlideModule(e3);
            throw null;
        } catch (InvocationTargetException e4) {
            throwIncorrectGlideModule(e4);
            throw null;
        }
    }

    private static void throwIncorrectGlideModule(Exception exc) {
        throw new IllegalStateException("GeneratedAppGlideModuleImpl is implemented incorrectly. If you've manually implemented this class, remove your implementation. The Annotation processor will generate a correct implementation.", exc);
    }

    Glide(Context context, Engine engine2, MemoryCache memoryCache2, BitmapPool bitmapPool2, ArrayPool arrayPool2, RequestManagerRetriever requestManagerRetriever2, ConnectivityMonitorFactory connectivityMonitorFactory2, int i, RequestOptionsFactory requestOptionsFactory, Map<Class<?>, TransitionOptions<?, ?>> map, List<RequestListener<Object>> list, boolean z, boolean z2, int i2, int i3) {
        ResourceDecoder resourceDecoder;
        ResourceDecoder resourceDecoder2;
        int i4 = Build.VERSION.SDK_INT;
        MemoryCategory memoryCategory = MemoryCategory.NORMAL;
        this.engine = engine2;
        this.bitmapPool = bitmapPool2;
        this.arrayPool = arrayPool2;
        this.memoryCache = memoryCache2;
        this.requestManagerRetriever = requestManagerRetriever2;
        this.connectivityMonitorFactory = connectivityMonitorFactory2;
        Resources resources = context.getResources();
        Registry registry2 = new Registry();
        this.registry = registry2;
        registry2.register(new DefaultImageHeaderParser());
        if (i4 >= 27) {
            this.registry.register(new ExifInterfaceImageHeaderParser());
        }
        List<ImageHeaderParser> imageHeaderParsers = this.registry.getImageHeaderParsers();
        ByteBufferGifDecoder byteBufferGifDecoder = new ByteBufferGifDecoder(context, imageHeaderParsers, bitmapPool2, arrayPool2);
        ResourceDecoder<ParcelFileDescriptor, Bitmap> parcel = VideoDecoder.parcel(bitmapPool2);
        if (!z2 || i4 < 28) {
            Downsampler downsampler = new Downsampler(this.registry.getImageHeaderParsers(), resources.getDisplayMetrics(), bitmapPool2, arrayPool2);
            resourceDecoder = new ByteBufferBitmapDecoder(downsampler);
            resourceDecoder2 = new StreamBitmapDecoder(downsampler, arrayPool2);
        } else {
            resourceDecoder2 = new InputStreamBitmapImageDecoderResourceDecoder();
            resourceDecoder = new ByteBufferBitmapImageDecoderResourceDecoder();
        }
        ResourceDrawableDecoder resourceDrawableDecoder = new ResourceDrawableDecoder(context);
        ResourceLoader.StreamFactory streamFactory = new ResourceLoader.StreamFactory(resources);
        ResourceLoader.UriFactory uriFactory = new ResourceLoader.UriFactory(resources);
        ResourceLoader.FileDescriptorFactory fileDescriptorFactory = new ResourceLoader.FileDescriptorFactory(resources);
        ResourceLoader.AssetFileDescriptorFactory assetFileDescriptorFactory = new ResourceLoader.AssetFileDescriptorFactory(resources);
        BitmapEncoder bitmapEncoder = new BitmapEncoder(arrayPool2);
        BitmapBytesTranscoder bitmapBytesTranscoder = new BitmapBytesTranscoder();
        GifDrawableBytesTranscoder gifDrawableBytesTranscoder = new GifDrawableBytesTranscoder();
        ContentResolver contentResolver = context.getContentResolver();
        Registry registry3 = this.registry;
        registry3.append(ByteBuffer.class, new ByteBufferEncoder());
        registry3.append(InputStream.class, new StreamEncoder(arrayPool2));
        registry3.append("Bitmap", ByteBuffer.class, Bitmap.class, resourceDecoder);
        registry3.append("Bitmap", InputStream.class, Bitmap.class, resourceDecoder2);
        registry3.append("Bitmap", ParcelFileDescriptor.class, Bitmap.class, parcel);
        registry3.append("Bitmap", AssetFileDescriptor.class, Bitmap.class, VideoDecoder.asset(bitmapPool2));
        registry3.append(Bitmap.class, Bitmap.class, UnitModelLoader.Factory.getInstance());
        registry3.append("Bitmap", Bitmap.class, Bitmap.class, new UnitBitmapDecoder());
        registry3.append(Bitmap.class, (ResourceEncoder) bitmapEncoder);
        registry3.append("BitmapDrawable", ByteBuffer.class, BitmapDrawable.class, new BitmapDrawableDecoder(resources, resourceDecoder));
        registry3.append("BitmapDrawable", InputStream.class, BitmapDrawable.class, new BitmapDrawableDecoder(resources, resourceDecoder2));
        registry3.append("BitmapDrawable", ParcelFileDescriptor.class, BitmapDrawable.class, new BitmapDrawableDecoder(resources, parcel));
        registry3.append(BitmapDrawable.class, (ResourceEncoder) new BitmapDrawableEncoder(bitmapPool2, bitmapEncoder));
        registry3.append("Gif", InputStream.class, GifDrawable.class, new StreamGifDecoder(imageHeaderParsers, byteBufferGifDecoder, arrayPool2));
        registry3.append("Gif", ByteBuffer.class, GifDrawable.class, byteBufferGifDecoder);
        registry3.append(GifDrawable.class, (ResourceEncoder) new GifDrawableEncoder());
        registry3.append(GifDecoder.class, GifDecoder.class, UnitModelLoader.Factory.getInstance());
        registry3.append("Bitmap", GifDecoder.class, Bitmap.class, new GifFrameResourceDecoder(bitmapPool2));
        registry3.append(Uri.class, Drawable.class, resourceDrawableDecoder);
        registry3.append(Uri.class, Bitmap.class, new ResourceBitmapDecoder(resourceDrawableDecoder, bitmapPool2));
        registry3.register(new ByteBufferRewinder.Factory());
        registry3.append(File.class, ByteBuffer.class, new ByteBufferFileLoader.Factory());
        registry3.append(File.class, InputStream.class, new FileLoader.StreamFactory());
        registry3.append(File.class, File.class, new FileDecoder());
        registry3.append(File.class, ParcelFileDescriptor.class, new FileLoader.FileDescriptorFactory());
        registry3.append(File.class, File.class, UnitModelLoader.Factory.getInstance());
        registry3.register(new InputStreamRewinder.Factory(arrayPool2));
        registry3.append(Integer.TYPE, InputStream.class, streamFactory);
        registry3.append(Integer.TYPE, ParcelFileDescriptor.class, fileDescriptorFactory);
        registry3.append(Integer.class, InputStream.class, streamFactory);
        registry3.append(Integer.class, ParcelFileDescriptor.class, fileDescriptorFactory);
        registry3.append(Integer.class, Uri.class, uriFactory);
        registry3.append(Integer.TYPE, AssetFileDescriptor.class, assetFileDescriptorFactory);
        registry3.append(Integer.class, AssetFileDescriptor.class, assetFileDescriptorFactory);
        registry3.append(Integer.TYPE, Uri.class, uriFactory);
        registry3.append(String.class, InputStream.class, new DataUrlLoader.StreamFactory());
        registry3.append(Uri.class, InputStream.class, new DataUrlLoader.StreamFactory());
        registry3.append(String.class, InputStream.class, new StringLoader.StreamFactory());
        registry3.append(String.class, ParcelFileDescriptor.class, new StringLoader.FileDescriptorFactory());
        registry3.append(String.class, AssetFileDescriptor.class, new StringLoader.AssetFileDescriptorFactory());
        registry3.append(Uri.class, InputStream.class, new HttpUriLoader.Factory());
        registry3.append(Uri.class, InputStream.class, new AssetUriLoader.StreamFactory(context.getAssets()));
        registry3.append(Uri.class, ParcelFileDescriptor.class, new AssetUriLoader.FileDescriptorFactory(context.getAssets()));
        registry3.append(Uri.class, InputStream.class, new MediaStoreImageThumbLoader.Factory(context));
        registry3.append(Uri.class, InputStream.class, new MediaStoreVideoThumbLoader.Factory(context));
        registry3.append(Uri.class, InputStream.class, new UriLoader.StreamFactory(contentResolver));
        registry3.append(Uri.class, ParcelFileDescriptor.class, new UriLoader.FileDescriptorFactory(contentResolver));
        registry3.append(Uri.class, AssetFileDescriptor.class, new UriLoader.AssetFileDescriptorFactory(contentResolver));
        registry3.append(Uri.class, InputStream.class, new UrlUriLoader.StreamFactory());
        registry3.append(URL.class, InputStream.class, new UrlLoader.StreamFactory());
        registry3.append(Uri.class, File.class, new MediaStoreFileLoader.Factory(context));
        registry3.append(GlideUrl.class, InputStream.class, new HttpGlideUrlLoader.Factory());
        registry3.append(byte[].class, ByteBuffer.class, new ByteArrayLoader.ByteBufferFactory());
        registry3.append(byte[].class, InputStream.class, new ByteArrayLoader.StreamFactory());
        registry3.append(Uri.class, Uri.class, UnitModelLoader.Factory.getInstance());
        registry3.append(Drawable.class, Drawable.class, UnitModelLoader.Factory.getInstance());
        registry3.append(Drawable.class, Drawable.class, new UnitDrawableDecoder());
        registry3.register(Bitmap.class, BitmapDrawable.class, new BitmapDrawableTranscoder(resources));
        registry3.register(Bitmap.class, byte[].class, bitmapBytesTranscoder);
        registry3.register(Drawable.class, byte[].class, new DrawableBytesTranscoder(bitmapPool2, bitmapBytesTranscoder, gifDrawableBytesTranscoder));
        registry3.register(GifDrawable.class, byte[].class, gifDrawableBytesTranscoder);
        this.glideContext = new GlideContext(context, arrayPool2, this.registry, new ImageViewTargetFactory(), requestOptionsFactory, map, list, engine2, z, i);
    }

    public BitmapPool getBitmapPool() {
        return this.bitmapPool;
    }

    public ArrayPool getArrayPool() {
        return this.arrayPool;
    }

    public Context getContext() {
        return this.glideContext.getBaseContext();
    }

    /* access modifiers changed from: package-private */
    public ConnectivityMonitorFactory getConnectivityMonitorFactory() {
        return this.connectivityMonitorFactory;
    }

    /* access modifiers changed from: package-private */
    public GlideContext getGlideContext() {
        return this.glideContext;
    }

    public void clearMemory() {
        Util.assertMainThread();
        this.memoryCache.clearMemory();
        this.bitmapPool.clearMemory();
        this.arrayPool.clearMemory();
    }

    public void trimMemory(int i) {
        Util.assertMainThread();
        for (RequestManager requestManager : this.managers) {
            requestManager.onTrimMemory(i);
        }
        this.memoryCache.trimMemory(i);
        this.bitmapPool.trimMemory(i);
        this.arrayPool.trimMemory(i);
    }

    public RequestManagerRetriever getRequestManagerRetriever() {
        return this.requestManagerRetriever;
    }

    private static RequestManagerRetriever getRetriever(Context context) {
        Preconditions.checkNotNull(context, "You cannot start a load on a not yet attached View or a Fragment where getActivity() returns null (which usually occurs when getActivity() is called before the Fragment is attached or after the Fragment is destroyed).");
        return get(context).getRequestManagerRetriever();
    }

    public static RequestManager with(Context context) {
        return getRetriever(context).get(context);
    }

    public Registry getRegistry() {
        return this.registry;
    }

    /* access modifiers changed from: package-private */
    public boolean removeFromManagers(Target<?> target) {
        synchronized (this.managers) {
            for (RequestManager requestManager : this.managers) {
                if (requestManager.untrack(target)) {
                    return true;
                }
            }
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public void registerRequestManager(RequestManager requestManager) {
        synchronized (this.managers) {
            if (!this.managers.contains(requestManager)) {
                this.managers.add(requestManager);
            } else {
                throw new IllegalStateException("Cannot register already registered manager");
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void unregisterRequestManager(RequestManager requestManager) {
        synchronized (this.managers) {
            if (this.managers.contains(requestManager)) {
                this.managers.remove(requestManager);
            } else {
                throw new IllegalStateException("Cannot unregister not yet registered manager");
            }
        }
    }

    public void onTrimMemory(int i) {
        trimMemory(i);
    }

    public void onLowMemory() {
        clearMemory();
    }
}
