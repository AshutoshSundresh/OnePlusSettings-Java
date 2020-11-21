package com.android.settings.homepage.contextualcards;

import com.android.settings.intelligence.ContextualCardProto$ContextualCard;
import com.android.settings.intelligence.ContextualCardProto$ContextualCardList;
import com.android.settings.slices.CustomSliceRegistry;
import com.google.android.settings.intelligence.libs.contextualcards.ContextualCardProvider;

public class SettingsContextualCardProvider extends ContextualCardProvider {
    @Override // com.google.android.settings.intelligence.libs.contextualcards.ContextualCardProvider
    public ContextualCardProto$ContextualCardList getContextualCards() {
        ContextualCardProto$ContextualCard.Builder newBuilder = ContextualCardProto$ContextualCard.newBuilder();
        newBuilder.setSliceUri(CustomSliceRegistry.CONTEXTUAL_WIFI_SLICE_URI.toString());
        newBuilder.setCardName(CustomSliceRegistry.CONTEXTUAL_WIFI_SLICE_URI.toString());
        newBuilder.setCardCategory(ContextualCardProto$ContextualCard.Category.IMPORTANT);
        ContextualCardProto$ContextualCard.Builder newBuilder2 = ContextualCardProto$ContextualCard.newBuilder();
        newBuilder2.setSliceUri(CustomSliceRegistry.BLUETOOTH_DEVICES_SLICE_URI.toString());
        newBuilder2.setCardName(CustomSliceRegistry.BLUETOOTH_DEVICES_SLICE_URI.toString());
        newBuilder2.setCardCategory(ContextualCardProto$ContextualCard.Category.IMPORTANT);
        ContextualCardProto$ContextualCard.Builder newBuilder3 = ContextualCardProto$ContextualCard.newBuilder();
        newBuilder3.setSliceUri(CustomSliceRegistry.LOW_STORAGE_SLICE_URI.toString());
        newBuilder3.setCardName(CustomSliceRegistry.LOW_STORAGE_SLICE_URI.toString());
        newBuilder3.setCardCategory(ContextualCardProto$ContextualCard.Category.IMPORTANT);
        ContextualCardProto$ContextualCard.Builder newBuilder4 = ContextualCardProto$ContextualCard.newBuilder();
        newBuilder4.setSliceUri(CustomSliceRegistry.BATTERY_FIX_SLICE_URI.toString());
        newBuilder4.setCardName(CustomSliceRegistry.BATTERY_FIX_SLICE_URI.toString());
        newBuilder4.setCardCategory(ContextualCardProto$ContextualCard.Category.IMPORTANT);
        String uri = CustomSliceRegistry.CONTEXTUAL_ADAPTIVE_SLEEP_URI.toString();
        ContextualCardProto$ContextualCard.Builder newBuilder5 = ContextualCardProto$ContextualCard.newBuilder();
        newBuilder5.setSliceUri(uri);
        newBuilder5.setCardName(uri);
        newBuilder5.setCardCategory(ContextualCardProto$ContextualCard.Category.DEFAULT);
        ContextualCardProto$ContextualCard.Builder newBuilder6 = ContextualCardProto$ContextualCard.newBuilder();
        newBuilder6.setSliceUri(CustomSliceRegistry.FACE_ENROLL_SLICE_URI.toString());
        newBuilder6.setCardName(CustomSliceRegistry.FACE_ENROLL_SLICE_URI.toString());
        newBuilder6.setCardCategory(ContextualCardProto$ContextualCard.Category.DEFAULT);
        ContextualCardProto$ContextualCard.Builder newBuilder7 = ContextualCardProto$ContextualCard.newBuilder();
        newBuilder7.setSliceUri(CustomSliceRegistry.DARK_THEME_SLICE_URI.toString());
        newBuilder7.setCardName(CustomSliceRegistry.DARK_THEME_SLICE_URI.toString());
        newBuilder7.setCardCategory(ContextualCardProto$ContextualCard.Category.IMPORTANT);
        ContextualCardProto$ContextualCardList.Builder newBuilder8 = ContextualCardProto$ContextualCardList.newBuilder();
        newBuilder8.addCard((ContextualCardProto$ContextualCard) newBuilder.build());
        newBuilder8.addCard((ContextualCardProto$ContextualCard) newBuilder2.build());
        newBuilder8.addCard((ContextualCardProto$ContextualCard) newBuilder3.build());
        newBuilder8.addCard((ContextualCardProto$ContextualCard) newBuilder4.build());
        newBuilder8.addCard((ContextualCardProto$ContextualCard) newBuilder5.build());
        newBuilder8.addCard((ContextualCardProto$ContextualCard) newBuilder6.build());
        newBuilder8.addCard((ContextualCardProto$ContextualCard) newBuilder7.build());
        return (ContextualCardProto$ContextualCardList) newBuilder8.build();
    }
}
