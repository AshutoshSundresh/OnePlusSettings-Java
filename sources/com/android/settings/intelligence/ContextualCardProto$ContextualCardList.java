package com.android.settings.intelligence;

import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.Parser;
import java.util.List;

public final class ContextualCardProto$ContextualCardList extends GeneratedMessageLite<ContextualCardProto$ContextualCardList, Builder> implements Object {
    public static final int CARD_FIELD_NUMBER = 1;
    private static final ContextualCardProto$ContextualCardList DEFAULT_INSTANCE;
    private static volatile Parser<ContextualCardProto$ContextualCardList> PARSER;
    private Internal.ProtobufList<ContextualCardProto$ContextualCard> card_ = GeneratedMessageLite.emptyProtobufList();

    private ContextualCardProto$ContextualCardList() {
    }

    public List<ContextualCardProto$ContextualCard> getCardList() {
        return this.card_;
    }

    private void ensureCardIsMutable() {
        if (!this.card_.isModifiable()) {
            this.card_ = GeneratedMessageLite.mutableCopy(this.card_);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void addCard(ContextualCardProto$ContextualCard contextualCardProto$ContextualCard) {
        if (contextualCardProto$ContextualCard != null) {
            ensureCardIsMutable();
            this.card_.add(contextualCardProto$ContextualCard);
            return;
        }
        throw null;
    }

    public static Builder newBuilder() {
        return (Builder) DEFAULT_INSTANCE.createBuilder();
    }

    public static final class Builder extends GeneratedMessageLite.Builder<ContextualCardProto$ContextualCardList, Builder> implements Object {
        /* synthetic */ Builder(ContextualCardProto$1 contextualCardProto$1) {
            this();
        }

        private Builder() {
            super(ContextualCardProto$ContextualCardList.DEFAULT_INSTANCE);
        }

        public Builder addCard(ContextualCardProto$ContextualCard contextualCardProto$ContextualCard) {
            copyOnWrite();
            ((ContextualCardProto$ContextualCardList) this.instance).addCard(contextualCardProto$ContextualCard);
            return this;
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.google.protobuf.GeneratedMessageLite
    public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
        switch (ContextualCardProto$1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
            case 1:
                return new ContextualCardProto$ContextualCardList();
            case 2:
                return new Builder(null);
            case 3:
                return GeneratedMessageLite.newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0001\u0000\u0001\u001b", new Object[]{"card_", ContextualCardProto$ContextualCard.class});
            case 4:
                return DEFAULT_INSTANCE;
            case 5:
                Parser<ContextualCardProto$ContextualCardList> parser = PARSER;
                if (parser == null) {
                    synchronized (ContextualCardProto$ContextualCardList.class) {
                        parser = PARSER;
                        if (parser == null) {
                            parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                            PARSER = parser;
                        }
                    }
                }
                return parser;
            case 6:
                return (byte) 1;
            case 7:
                return null;
            default:
                throw new UnsupportedOperationException();
        }
    }

    static {
        ContextualCardProto$ContextualCardList contextualCardProto$ContextualCardList = new ContextualCardProto$ContextualCardList();
        DEFAULT_INSTANCE = contextualCardProto$ContextualCardList;
        GeneratedMessageLite.registerDefaultInstance(ContextualCardProto$ContextualCardList.class, contextualCardProto$ContextualCardList);
    }
}
