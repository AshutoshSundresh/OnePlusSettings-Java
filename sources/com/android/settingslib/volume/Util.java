package com.android.settingslib.volume;

import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.PlaybackState;

public class Util {
    private static final int[] AUDIO_MANAGER_FLAGS = {1, 16, 4, 2, 8, 2048, 128, 4096, 1024};
    private static final String[] AUDIO_MANAGER_FLAG_NAMES = {"SHOW_UI", "VIBRATE", "PLAY_SOUND", "ALLOW_RINGER_MODES", "REMOVE_SOUND_AND_VIBRATE", "SHOW_VIBRATE_HINT", "SHOW_SILENT_HINT", "FROM_KEY", "SHOW_UI_WARNINGS"};

    public static String logTag(Class<?> cls) {
        String str = "vol." + cls.getSimpleName();
        return str.length() < 23 ? str : str.substring(0, 23);
    }

    public static String mediaMetadataToString(MediaMetadata mediaMetadata) {
        if (mediaMetadata == null) {
            return null;
        }
        return mediaMetadata.getDescription().toString();
    }

    public static String playbackInfoToString(MediaController.PlaybackInfo playbackInfo) {
        if (playbackInfo == null) {
            return null;
        }
        return String.format("PlaybackInfo[vol=%s,max=%s,type=%s,vc=%s],atts=%s", Integer.valueOf(playbackInfo.getCurrentVolume()), Integer.valueOf(playbackInfo.getMaxVolume()), playbackInfoTypeToString(playbackInfo.getPlaybackType()), volumeProviderControlToString(playbackInfo.getVolumeControl()), playbackInfo.getAudioAttributes());
    }

    public static String playbackInfoTypeToString(int i) {
        if (i == 1) {
            return "LOCAL";
        }
        if (i == 2) {
            return "REMOTE";
        }
        return "UNKNOWN_" + i;
    }

    public static String playbackStateStateToString(int i) {
        if (i == 0) {
            return "STATE_NONE";
        }
        if (i == 1) {
            return "STATE_STOPPED";
        }
        if (i == 2) {
            return "STATE_PAUSED";
        }
        if (i == 3) {
            return "STATE_PLAYING";
        }
        return "UNKNOWN_" + i;
    }

    public static String volumeProviderControlToString(int i) {
        if (i == 0) {
            return "VOLUME_CONTROL_FIXED";
        }
        if (i == 1) {
            return "VOLUME_CONTROL_RELATIVE";
        }
        if (i == 2) {
            return "VOLUME_CONTROL_ABSOLUTE";
        }
        return "VOLUME_CONTROL_UNKNOWN_" + i;
    }

    public static String playbackStateToString(PlaybackState playbackState) {
        if (playbackState == null) {
            return null;
        }
        return playbackStateStateToString(playbackState.getState()) + " " + playbackState;
    }

    public static String audioManagerFlagsToString(int i) {
        return bitFieldToString(i, AUDIO_MANAGER_FLAGS, AUDIO_MANAGER_FLAG_NAMES);
    }

    protected static String bitFieldToString(int i, int[] iArr, String[] strArr) {
        if (i == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i2 = 0; i2 < iArr.length; i2++) {
            if ((iArr[i2] & i) != 0) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(strArr[i2]);
            }
            i &= ~iArr[i2];
        }
        if (i != 0) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append("UNKNOWN_");
            sb.append(i);
        }
        return sb.toString();
    }
}
