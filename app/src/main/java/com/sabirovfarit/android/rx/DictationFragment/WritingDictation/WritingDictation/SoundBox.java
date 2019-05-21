package com.sabirovfarit.android.rx.DictationFragment.WritingDictation.WritingDictation;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SoundBox {

    private static final String TAG = "SoundBox";
    private static final String WORD_SOUND = "word_sound";
    private static final int MAX_SOUNDS = 1;

    private AssetManager assetManager;
    private List<Sound> sounds = new ArrayList<>();
    private SoundPool soundPool;
    private int streemId;

    public SoundBox(Context context) {
        assetManager = context.getAssets();
        soundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);
        loadSound();
    }

    public void play(int sId) {
        Integer soundId = sId;
        if (soundId == null) {
            return;
        }
        streemId = soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    private void loadSound() {
        String[] soundNames;
        try {
            soundNames = assetManager.list(WORD_SOUND);
            Log.i(TAG, "loadSound: " + soundNames.length);
        } catch (IOException ioe) {
            Log.i(TAG, "loadSound: " + "Нет звуков!");
            return;
        }
        for (String soundName : soundNames) {
            try {
                String assetPath = WORD_SOUND + "/" + soundName;

                Sound sound = new Sound(assetPath);
                load(sound);
                sounds.add(sound);
                Log.i(TAG, "loadSound:sound.getSoundId() " + sound.getSoundId() + "  " + sound.getName() + " " + sound.getAssetPath());
            } catch (IOException e) {
                Log.i(TAG, "loadSound: " + "Не получилось загрузить звуки");
            }
        }
    }

    private void load(Sound sound) throws IOException {
        AssetFileDescriptor afd = assetManager.openFd(sound.getAssetPath());
        int soundId = soundPool.load(afd, 1);
        sound.setSoundId(soundId);
    }

    public void release() {
        soundPool.release();
    }

    public void resume() {
        soundPool.resume(streemId);
    }

    public void pause() {
        soundPool.pause(streemId);
    }

    public SoundPool getSoundPool() {
        return soundPool;
    }

    public List<Sound> getSounds() {
        return sounds;
    }
}
