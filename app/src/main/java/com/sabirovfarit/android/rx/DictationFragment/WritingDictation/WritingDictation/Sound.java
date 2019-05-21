package com.sabirovfarit.android.rx.DictationFragment.WritingDictation.WritingDictation;

import android.content.Intent;

public class Sound {
    private String assetPath;
    private String name;
    private Integer soundId;

    public Sound(String assetPath) {
        this.assetPath = assetPath;
        String[] components = assetPath.split("/");
        String filename = components[components.length - 1];
        name = filename.replace(".wav", "");
    }
    public String getAssetPath() {
        return assetPath;
    }
    public String getName() {
        return name;
    }

    public Integer getSoundId() {
        return soundId;
    }

    public void setSoundId(Integer soundId) {
        this.soundId = soundId;
    }
}
