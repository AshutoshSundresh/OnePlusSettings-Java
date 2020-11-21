package com.oneplus.settings.packageuninstaller;

import android.os.AsyncTask;
import android.util.AtomicFile;
import android.util.SparseArray;
import android.util.Xml;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* access modifiers changed from: package-private */
public class EventResultPersister {
    private static final String LOG_TAG = "EventResultPersister";
    private int mCounter;
    private boolean mIsPersistScheduled;
    private boolean mIsPersistingStateValid;
    private final Object mLock = new Object();
    private final SparseArray<EventResult> mResults = new SparseArray<>();
    private final AtomicFile mResultsFile;

    public int getNewId() throws OutOfIdsException {
        int i;
        synchronized (this.mLock) {
            if (this.mCounter != Integer.MAX_VALUE) {
                this.mCounter++;
                writeState();
                i = this.mCounter - 1;
            } else {
                throw new OutOfIdsException();
            }
        }
        return i;
    }

    private static void nextElement(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        int next;
        do {
            next = xmlPullParser.next();
            if (next == 2) {
                return;
            }
        } while (next != 1);
    }

    private static int readIntAttribute(XmlPullParser xmlPullParser, String str) {
        return Integer.parseInt(xmlPullParser.getAttributeValue(null, str));
    }

    private static String readStringAttribute(XmlPullParser xmlPullParser, String str) {
        return xmlPullParser.getAttributeValue(null, str);
    }

    EventResultPersister(File file) {
        new SparseArray();
        AtomicFile atomicFile = new AtomicFile(file);
        this.mResultsFile = atomicFile;
        this.mCounter = -2147483647;
        try {
            FileInputStream openRead = atomicFile.openRead();
            try {
                XmlPullParser newPullParser = Xml.newPullParser();
                newPullParser.setInput(openRead, StandardCharsets.UTF_8.name());
                nextElement(newPullParser);
                while (newPullParser.getEventType() != 1) {
                    String name = newPullParser.getName();
                    if ("results".equals(name)) {
                        this.mCounter = readIntAttribute(newPullParser, "counter");
                    } else if ("result".equals(name)) {
                        int readIntAttribute = readIntAttribute(newPullParser, "id");
                        int readIntAttribute2 = readIntAttribute(newPullParser, "status");
                        int readIntAttribute3 = readIntAttribute(newPullParser, "legacyStatus");
                        String readStringAttribute = readStringAttribute(newPullParser, "statusMessage");
                        if (this.mResults.get(readIntAttribute) == null) {
                            this.mResults.put(readIntAttribute, new EventResult(readIntAttribute2, readIntAttribute3, readStringAttribute));
                        } else {
                            throw new Exception("id " + readIntAttribute + " has two results");
                        }
                    } else {
                        throw new Exception("unexpected tag");
                    }
                    nextElement(newPullParser);
                }
                if (openRead != null) {
                    openRead.close();
                    return;
                }
                return;
            } catch (Throwable th) {
                th.addSuppressed(th);
            }
            throw th;
        } catch (Exception unused) {
            this.mResults.clear();
            writeState();
        }
    }

    private void writeState() {
        synchronized (this.mLock) {
            this.mIsPersistingStateValid = false;
            if (!this.mIsPersistScheduled) {
                this.mIsPersistScheduled = true;
                AsyncTask.execute(new Runnable() {
                    /* class com.oneplus.settings.packageuninstaller.$$Lambda$EventResultPersister$zHzPUvQ151m1efiCPydr8fc75IA */

                    public final void run() {
                        EventResultPersister.this.lambda$writeState$0$EventResultPersister();
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x00aa  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00be A[SYNTHETIC] */
    /* renamed from: lambda$writeState$0 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$writeState$0$EventResultPersister() {
        /*
        // Method dump skipped, instructions count: 207
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.packageuninstaller.EventResultPersister.lambda$writeState$0$EventResultPersister():void");
    }

    /* access modifiers changed from: private */
    public class EventResult {
        public final int legacyStatus;
        public final String message;
        public final int status;

        private EventResult(EventResultPersister eventResultPersister, int i, int i2, String str) {
            this.status = i;
            this.legacyStatus = i2;
            this.message = str;
        }
    }

    class OutOfIdsException extends Exception {
        OutOfIdsException() {
        }
    }
}
