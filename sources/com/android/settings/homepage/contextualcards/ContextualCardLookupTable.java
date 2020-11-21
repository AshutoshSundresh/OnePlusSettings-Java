package com.android.settings.homepage.contextualcards;

import android.util.Log;
import com.android.settings.homepage.contextualcards.ContextualCardLookupTable;
import com.android.settings.homepage.contextualcards.conditional.ConditionContextualCardController;
import com.android.settings.homepage.contextualcards.conditional.ConditionContextualCardRenderer;
import com.android.settings.homepage.contextualcards.conditional.ConditionFooterContextualCardRenderer;
import com.android.settings.homepage.contextualcards.conditional.ConditionHeaderContextualCardRenderer;
import com.android.settings.homepage.contextualcards.legacysuggestion.LegacySuggestionContextualCardController;
import com.android.settings.homepage.contextualcards.legacysuggestion.LegacySuggestionContextualCardRenderer;
import com.android.settings.homepage.contextualcards.slices.SliceContextualCardController;
import com.android.settings.homepage.contextualcards.slices.SliceContextualCardRenderer;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ContextualCardLookupTable {
    static final Set<ControllerRendererMapping> LOOKUP_TABLE = new TreeSet<ControllerRendererMapping>() {
        /* class com.android.settings.homepage.contextualcards.ContextualCardLookupTable.AnonymousClass1 */

        {
            add(new ControllerRendererMapping(3, ConditionContextualCardRenderer.VIEW_TYPE_HALF_WIDTH, ConditionContextualCardController.class, ConditionContextualCardRenderer.class));
            add(new ControllerRendererMapping(3, ConditionContextualCardRenderer.VIEW_TYPE_FULL_WIDTH, ConditionContextualCardController.class, ConditionContextualCardRenderer.class));
            add(new ControllerRendererMapping(2, LegacySuggestionContextualCardRenderer.VIEW_TYPE, LegacySuggestionContextualCardController.class, LegacySuggestionContextualCardRenderer.class));
            add(new ControllerRendererMapping(1, SliceContextualCardRenderer.VIEW_TYPE_FULL_WIDTH, SliceContextualCardController.class, SliceContextualCardRenderer.class));
            add(new ControllerRendererMapping(1, SliceContextualCardRenderer.VIEW_TYPE_HALF_WIDTH, SliceContextualCardController.class, SliceContextualCardRenderer.class));
            add(new ControllerRendererMapping(1, SliceContextualCardRenderer.VIEW_TYPE_STICKY, SliceContextualCardController.class, SliceContextualCardRenderer.class));
            add(new ControllerRendererMapping(5, ConditionFooterContextualCardRenderer.VIEW_TYPE, ConditionContextualCardController.class, ConditionFooterContextualCardRenderer.class));
            add(new ControllerRendererMapping(4, ConditionHeaderContextualCardRenderer.VIEW_TYPE, ConditionContextualCardController.class, ConditionHeaderContextualCardRenderer.class));
        }
    };

    /* access modifiers changed from: package-private */
    public static class ControllerRendererMapping implements Comparable<ControllerRendererMapping> {
        final int mCardType;
        final Class<? extends ContextualCardController> mControllerClass;
        final Class<? extends ContextualCardRenderer> mRendererClass;
        final int mViewType;

        ControllerRendererMapping(int i, int i2, Class<? extends ContextualCardController> cls, Class<? extends ContextualCardRenderer> cls2) {
            this.mCardType = i;
            this.mViewType = i2;
            this.mControllerClass = cls;
            this.mRendererClass = cls2;
        }

        public int compareTo(ControllerRendererMapping controllerRendererMapping) {
            return Comparator.comparingInt($$Lambda$ContextualCardLookupTable$ControllerRendererMapping$3JMPP5J3q92eA7mqQzroGGTxE.INSTANCE).thenComparingInt($$Lambda$ContextualCardLookupTable$ControllerRendererMapping$ZZwwcJixnwWLgKid21VQ51NUU0U.INSTANCE).compare(this, controllerRendererMapping);
        }
    }

    public static Class<? extends ContextualCardController> getCardControllerClass(int i) {
        for (ControllerRendererMapping controllerRendererMapping : LOOKUP_TABLE) {
            if (controllerRendererMapping.mCardType == i) {
                return controllerRendererMapping.mControllerClass;
            }
        }
        return null;
    }

    public static Class<? extends ContextualCardRenderer> getCardRendererClassByViewType(int i) throws IllegalStateException {
        List list = (List) LOOKUP_TABLE.stream().filter(new Predicate(i) {
            /* class com.android.settings.homepage.contextualcards.$$Lambda$ContextualCardLookupTable$gsQ9JPvW3zYVPc0k37lyIEDUnOY */
            public final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ContextualCardLookupTable.lambda$getCardRendererClassByViewType$0(this.f$0, (ContextualCardLookupTable.ControllerRendererMapping) obj);
            }
        }).collect(Collectors.toList());
        if (list == null || list.isEmpty()) {
            Log.w("ContextualCardLookup", "No matching mapping");
            return null;
        } else if (list.size() == 1) {
            return ((ControllerRendererMapping) list.get(0)).mRendererClass;
        } else {
            throw new IllegalStateException("Have duplicate VIEW_TYPE in lookup table.");
        }
    }

    static /* synthetic */ boolean lambda$getCardRendererClassByViewType$0(int i, ControllerRendererMapping controllerRendererMapping) {
        return controllerRendererMapping.mViewType == i;
    }
}
