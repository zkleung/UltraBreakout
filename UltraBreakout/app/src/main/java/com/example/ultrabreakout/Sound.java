package com.example.ultrabreakout;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;

/* A singleton class to handle all of the sound for the game.
 *
 * Sound is a singleton instance in order to enable sharing between all of the activities
 * within the app.
 *
 * Sound is split into two categories, background music and sound effects.
 * Background music is simply set on a screen and set to loop when the activity is started,
 * and this is handled by the MediaPlayer, which streams in the music.
 * Sound effects are manually called by the application when certain events happen, and these
 * are handled by the SoundPool, since they are smaller in size than the music and can
 * comfortably fit in the RAM.
 */
public class Sound {
    // Singleton instance.
    private static Sound instance;

    // MediaPlayer handles streaming in the music.
    private MediaPlayer mediaPlayer;

    // SoundPool handles all of the sound effects of the game.
    private SoundPool soundPool;

    // Currently loaded resource id for the music, so we know what music is
    // playing at any moment.
    private int resourceId;

    // Whether the MediaPlayer has finished buffering the music.
    private boolean prepared;

    private Sound() {
        // Set attributes for the sound pool, which signify that it will be used for
        // short audio files, attached to user events.
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build();

        resourceId = 0;
        prepared = false;
    }

    /* Play a certain audio file for the background music on repeat.
     *
     *
     */
    public void play_background(Context context, int background_music_id) {
        // Check to see if we're already playing the specified audio file.
        // If so, just continue playing it.
        if (mediaPlayer != null && resourceId == background_music_id) {
            if (prepared) {
                mediaPlayer.start();
            }
            return;
        }

        resourceId = background_music_id;

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        // Get the file descriptor for the specified audio file, and pass it in to the MediaPlayer.
        AssetFileDescriptor afd = context.getResources().openRawResourceFd(resourceId);
        prepared = false;
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        } catch (IOException e) {
            Log.e("Error", e.toString());
        }
        mediaPlayer.setLooping(true);

        // Set callback, so it starts playing the audio file when it finishes loading it in.
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                prepared = true;
            }
        });
        mediaPlayer.prepareAsync();
    }

    public static Sound getInstance() {
        if (instance == null) {
            instance = new Sound();
        }
        return instance;
    }

    public void resume() {
        if (prepared) {
            mediaPlayer.start();
        }
    }

    public void pause() {
        if (prepared) {
            mediaPlayer.pause();
        }
    }
}
