package com.android.settings.intelligence;

import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Parser;

public final class LogProto$SettingsLog extends GeneratedMessageLite<LogProto$SettingsLog, Builder> implements Object {
    public static final int ACTION_FIELD_NUMBER = 2;
    public static final int ATTRIBUTION_FIELD_NUMBER = 1;
    public static final int CHANGED_PREFERENCE_INT_VALUE_FIELD_NUMBER = 5;
    public static final int CHANGED_PREFERENCE_KEY_FIELD_NUMBER = 4;
    private static final LogProto$SettingsLog DEFAULT_INSTANCE;
    public static final int PAGE_ID_FIELD_NUMBER = 3;
    private static volatile Parser<LogProto$SettingsLog> PARSER = null;
    public static final int TIMESTAMP_FIELD_NUMBER = 6;
    private int action_;
    private int attribution_;
    private int bitField0_;
    private int changedPreferenceIntValue_;
    private String changedPreferenceKey_ = "";
    private int pageId_;
    private String timestamp_ = "";

    private LogProto$SettingsLog() {
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setAttribution(int i) {
        this.bitField0_ |= 1;
        this.attribution_ = i;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setAction(int i) {
        this.bitField0_ |= 2;
        this.action_ = i;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setPageId(int i) {
        this.bitField0_ |= 4;
        this.pageId_ = i;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setChangedPreferenceKey(String str) {
        if (str != null) {
            this.bitField0_ |= 8;
            this.changedPreferenceKey_ = str;
            return;
        }
        throw null;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setChangedPreferenceIntValue(int i) {
        this.bitField0_ |= 16;
        this.changedPreferenceIntValue_ = i;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setTimestamp(String str) {
        if (str != null) {
            this.bitField0_ |= 32;
            this.timestamp_ = str;
            return;
        }
        throw null;
    }

    public static Builder newBuilder() {
        return (Builder) DEFAULT_INSTANCE.createBuilder();
    }

    public static final class Builder extends GeneratedMessageLite.Builder<LogProto$SettingsLog, Builder> implements Object {
        /* synthetic */ Builder(LogProto$1 logProto$1) {
            this();
        }

        private Builder() {
            super(LogProto$SettingsLog.DEFAULT_INSTANCE);
        }

        public Builder setAttribution(int i) {
            copyOnWrite();
            ((LogProto$SettingsLog) this.instance).setAttribution(i);
            return this;
        }

        public Builder setAction(int i) {
            copyOnWrite();
            ((LogProto$SettingsLog) this.instance).setAction(i);
            return this;
        }

        public Builder setPageId(int i) {
            copyOnWrite();
            ((LogProto$SettingsLog) this.instance).setPageId(i);
            return this;
        }

        public Builder setChangedPreferenceKey(String str) {
            copyOnWrite();
            ((LogProto$SettingsLog) this.instance).setChangedPreferenceKey(str);
            return this;
        }

        public Builder setChangedPreferenceIntValue(int i) {
            copyOnWrite();
            ((LogProto$SettingsLog) this.instance).setChangedPreferenceIntValue(i);
            return this;
        }

        public Builder setTimestamp(String str) {
            copyOnWrite();
            ((LogProto$SettingsLog) this.instance).setTimestamp(str);
            return this;
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.google.protobuf.GeneratedMessageLite
    public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
        switch (LogProto$1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
            case 1:
                return new LogProto$SettingsLog();
            case 2:
                return new Builder(null);
            case 3:
                return GeneratedMessageLite.newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0006\u0000\u0001\u0001\u0006\u0006\u0000\u0000\u0000\u0001\u0004\u0000\u0002\u0004\u0001\u0003\u0004\u0002\u0004\b\u0003\u0005\u0004\u0004\u0006\b\u0005", new Object[]{"bitField0_", "attribution_", "action_", "pageId_", "changedPreferenceKey_", "changedPreferenceIntValue_", "timestamp_"});
            case 4:
                return DEFAULT_INSTANCE;
            case 5:
                Parser<LogProto$SettingsLog> parser = PARSER;
                if (parser == null) {
                    synchronized (LogProto$SettingsLog.class) {
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
        LogProto$SettingsLog logProto$SettingsLog = new LogProto$SettingsLog();
        DEFAULT_INSTANCE = logProto$SettingsLog;
        GeneratedMessageLite.registerDefaultInstance(LogProto$SettingsLog.class, logProto$SettingsLog);
    }
}
