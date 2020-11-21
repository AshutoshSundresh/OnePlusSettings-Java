package androidx.mediarouter.media;

import android.content.Context;
import android.media.MediaRoute2Info;
import android.media.MediaRouter2;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import androidx.mediarouter.media.MediaRouteProvider;
import androidx.mediarouter.media.MediaRouteProviderDescriptor;
import androidx.mediarouter.media.MediaRouter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/* access modifiers changed from: package-private */
public class MediaRoute2Provider extends MediaRouteProvider {
    final Callback mCallback;
    final Map<MediaRouter2.RoutingController, DynamicMediaRoute2Controller> mControllerMap = new ArrayMap();
    private final Handler mHandler;
    private final Executor mHandlerExecutor;
    final MediaRouter2 mMediaRouter2;
    private final MediaRouter2.RouteCallback mRouteCallback = new RouteCallback();
    private List<MediaRoute2Info> mRoutes = new ArrayList();
    private final MediaRouter2.TransferCallback mTransferCallback = new TransferCallback();

    static {
        Log.isLoggable("MR2Provider", 3);
    }

    MediaRoute2Provider(Context context, Callback callback) {
        super(context);
        this.mMediaRouter2 = MediaRouter2.getInstance(context);
        this.mCallback = callback;
        Handler handler = new Handler();
        this.mHandler = handler;
        Objects.requireNonNull(handler);
        this.mHandlerExecutor = new Executor(handler) {
            /* class androidx.mediarouter.media.$$Lambda$LfzJt661qZfn2w6SYHFbD3aMy0 */
            public final /* synthetic */ Handler f$0;

            {
                this.f$0 = r1;
            }

            public final void execute(Runnable runnable) {
                this.f$0.post(runnable);
            }
        };
    }

    @Override // androidx.mediarouter.media.MediaRouteProvider
    public void onDiscoveryRequestChanged(MediaRouteDiscoveryRequest mediaRouteDiscoveryRequest) {
        if (MediaRouter.getGlobalCallbackCount() > 0) {
            this.mMediaRouter2.registerRouteCallback(this.mHandlerExecutor, this.mRouteCallback, MediaRouter2Utils.toDiscoveryPreference(mediaRouteDiscoveryRequest));
            this.mMediaRouter2.registerTransferCallback(this.mHandlerExecutor, this.mTransferCallback);
            return;
        }
        this.mMediaRouter2.unregisterRouteCallback(this.mRouteCallback);
        this.mMediaRouter2.unregisterTransferCallback(this.mTransferCallback);
    }

    @Override // androidx.mediarouter.media.MediaRouteProvider
    public MediaRouteProvider.RouteController onCreateRouteController(String str) {
        return new MediaRoute2Controller(this, str, null);
    }

    @Override // androidx.mediarouter.media.MediaRouteProvider
    public MediaRouteProvider.RouteController onCreateRouteController(String str, String str2) {
        return new MediaRoute2Controller(this, str, str2);
    }

    @Override // androidx.mediarouter.media.MediaRouteProvider
    public MediaRouteProvider.DynamicGroupRouteController onCreateDynamicGroupRouteController(String str) {
        for (Map.Entry<MediaRouter2.RoutingController, DynamicMediaRoute2Controller> entry : this.mControllerMap.entrySet()) {
            DynamicMediaRoute2Controller value = entry.getValue();
            if (TextUtils.equals(str, value.mInitialMemberRouteId)) {
                return value;
            }
        }
        return null;
    }

    public void transferTo(String str) {
        MediaRoute2Info routeById = getRouteById(str);
        if (routeById == null) {
            Log.w("MR2Provider", "Specified route not found. routeId=" + str);
            return;
        }
        this.mMediaRouter2.transferTo(routeById);
    }

    /* access modifiers changed from: protected */
    public void refreshRoutes() {
        List<MediaRoute2Info> list = (List) this.mMediaRouter2.getRoutes().stream().distinct().filter($$Lambda$MediaRoute2Provider$VTgS4VAy5vSwKN7_lZ6W4L_NPw.INSTANCE).collect(Collectors.toList());
        if (!list.equals(this.mRoutes)) {
            this.mRoutes = list;
            MediaRouteProviderDescriptor.Builder builder = new MediaRouteProviderDescriptor.Builder();
            builder.setSupportsDynamicGroupRoute(true);
            builder.addRoutes((List) list.stream().map($$Lambda$853YVfaGw0G4oNUYI6Z1ujaq6k.INSTANCE).filter($$Lambda$jMO9OfSzscMxGho8zZuPtPiQlPo.INSTANCE).collect(Collectors.toList()));
            setDescriptor(builder.build());
        }
    }

    static /* synthetic */ boolean lambda$refreshRoutes$0(MediaRoute2Info mediaRoute2Info) {
        return !mediaRoute2Info.isSystemRoute();
    }

    /* access modifiers changed from: package-private */
    public MediaRoute2Info getRouteById(String str) {
        if (str == null) {
            return null;
        }
        for (MediaRoute2Info mediaRoute2Info : this.mRoutes) {
            if (TextUtils.equals(mediaRoute2Info.getId(), str)) {
                return mediaRoute2Info;
            }
        }
        return null;
    }

    static Messenger getMessengerFromRoutingController(MediaRouter2.RoutingController routingController) {
        Bundle controlHints;
        if (routingController == null || (controlHints = routingController.getControlHints()) == null) {
            return null;
        }
        return (Messenger) controlHints.getParcelable("androidx.mediarouter.media.KEY_MESSENGER");
    }

    static String getSessionIdForRouteController(MediaRouteProvider.RouteController routeController) {
        MediaRouter2.RoutingController routingController;
        if ((routeController instanceof DynamicMediaRoute2Controller) && (routingController = ((DynamicMediaRoute2Controller) routeController).mRoutingController) != null) {
            return routingController.getId();
        }
        return null;
    }

    private class RouteCallback extends MediaRouter2.RouteCallback {
        RouteCallback() {
        }

        public void onRoutesAdded(List<MediaRoute2Info> list) {
            MediaRoute2Provider.this.refreshRoutes();
        }

        public void onRoutesRemoved(List<MediaRoute2Info> list) {
            MediaRoute2Provider.this.refreshRoutes();
        }

        public void onRoutesChanged(List<MediaRoute2Info> list) {
            MediaRoute2Provider.this.refreshRoutes();
        }
    }

    /* access modifiers changed from: package-private */
    public static abstract class Callback {
        public abstract void onReleaseController(MediaRouteProvider.RouteController routeController);

        public abstract void onSelectFallbackRoute(int i);

        public abstract void onSelectRoute(String str, int i);

        Callback() {
        }
    }

    final class TransferCallback extends MediaRouter2.TransferCallback {
        TransferCallback() {
        }

        public void onTransfer(MediaRouter2.RoutingController routingController, MediaRouter2.RoutingController routingController2) {
            MediaRoute2Provider.this.mControllerMap.remove(routingController);
            if (routingController2 == MediaRoute2Provider.this.mMediaRouter2.getSystemController()) {
                MediaRoute2Provider.this.mCallback.onSelectFallbackRoute(3);
                return;
            }
            List selectedRoutes = routingController2.getSelectedRoutes();
            if (selectedRoutes.isEmpty()) {
                Log.w("MR2Provider", "Selected routes are empty. This shouldn't happen.");
                return;
            }
            String id = ((MediaRoute2Info) selectedRoutes.get(0)).getId();
            MediaRoute2Provider.this.mControllerMap.put(routingController2, new DynamicMediaRoute2Controller(MediaRoute2Provider.this, id, routingController2));
            MediaRoute2Provider.this.mCallback.onSelectRoute(id, 3);
        }

        public void onTransferFailure(MediaRoute2Info mediaRoute2Info) {
            Log.w("MR2Provider", "Transfer failed. requestedRoute=" + mediaRoute2Info);
        }

        public void onStop(MediaRouter2.RoutingController routingController) {
            MediaRoute2Provider.this.mCallback.onReleaseController(MediaRoute2Provider.this.mControllerMap.remove(routingController));
        }
    }

    private class MediaRoute2Controller extends MediaRouteProvider.RouteController {
        MediaRoute2Controller(MediaRoute2Provider mediaRoute2Provider, String str, String str2) {
        }
    }

    /* access modifiers changed from: private */
    public class DynamicMediaRoute2Controller extends MediaRouteProvider.DynamicGroupRouteController {
        final String mInitialMemberRouteId;
        final SparseArray<MediaRouter.ControlRequestCallback> mPendingCallbacks = new SparseArray<>();
        final MediaRouter2.RoutingController mRoutingController;
        final Messenger mServiceMessenger;

        @Override // androidx.mediarouter.media.MediaRouteProvider.DynamicGroupRouteController
        public void onAddMemberRoute(String str) {
        }

        @Override // androidx.mediarouter.media.MediaRouteProvider.DynamicGroupRouteController
        public void onRemoveMemberRoute(String str) {
        }

        @Override // androidx.mediarouter.media.MediaRouteProvider.DynamicGroupRouteController
        public void onUpdateMemberRoutes(List<String> list) {
        }

        DynamicMediaRoute2Controller(MediaRoute2Provider mediaRoute2Provider, String str, MediaRouter2.RoutingController routingController) {
            this.mInitialMemberRouteId = str;
            this.mRoutingController = routingController;
            Messenger messengerFromRoutingController = MediaRoute2Provider.getMessengerFromRoutingController(routingController);
            this.mServiceMessenger = messengerFromRoutingController;
            if (messengerFromRoutingController != null) {
                new Messenger(new ReceiveHandler());
            }
        }

        @Override // androidx.mediarouter.media.MediaRouteProvider.RouteController
        public void onSetVolume(int i) {
            MediaRouter2.RoutingController routingController = this.mRoutingController;
            if (routingController != null) {
                routingController.setVolume(i);
            }
        }

        @Override // androidx.mediarouter.media.MediaRouteProvider.RouteController
        public void onUpdateVolume(int i) {
            MediaRouter2.RoutingController routingController = this.mRoutingController;
            if (routingController != null) {
                routingController.setVolume(routingController.getVolume() + i);
            }
        }

        @Override // androidx.mediarouter.media.MediaRouteProvider.RouteController
        public void onRelease() {
            this.mRoutingController.release();
        }

        class ReceiveHandler extends Handler {
            ReceiveHandler() {
            }

            public void handleMessage(Message message) {
                String str;
                int i = message.what;
                int i2 = message.arg1;
                int i3 = message.arg2;
                Object obj = message.obj;
                Bundle peekData = message.peekData();
                MediaRouter.ControlRequestCallback controlRequestCallback = DynamicMediaRoute2Controller.this.mPendingCallbacks.get(i2);
                if (controlRequestCallback == null) {
                    Log.w("MR2Provider", "Pending callback not found for control request.");
                    return;
                }
                DynamicMediaRoute2Controller.this.mPendingCallbacks.remove(i2);
                if (i == 3) {
                    controlRequestCallback.onResult((Bundle) obj);
                } else if (i == 4) {
                    if (peekData == null) {
                        str = null;
                    } else {
                        str = peekData.getString("error");
                    }
                    controlRequestCallback.onError(str, (Bundle) obj);
                }
            }
        }
    }
}
