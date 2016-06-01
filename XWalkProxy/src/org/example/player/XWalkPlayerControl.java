package org.example.player;

import android.media.MediaPlayer;
import android.widget.MediaController;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.util.PlayerControl;

/**
 * Created by junweifu on 5/29/2016.
 */
public class XWalkPlayerControl implements MediaController.MediaPlayerControl {
    ExoPlayer exoPlayer;
    MediaPlayer mediaPlayer;

    public XWalkPlayerControl(ExoPlayer exoPlayer) {
        this.exoPlayer = exoPlayer;
    }

    public XWalkPlayerControl(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public boolean canPause() {
        return true;
    }

    public boolean canSeekBackward() {
        return true;
    }

    public boolean canSeekForward() {
        return true;
    }

    public int getAudioSessionId() {
        throw new UnsupportedOperationException();
    }

    public int getBufferPercentage() {
        if (exoPlayer != null) {
            return exoPlayer.getBufferedPercentage();
        } else {
            return 0;
        }
    }

    public int getCurrentPosition() {
        if (exoPlayer != null) {
            return exoPlayer.getDuration() == -1L ? 0 : (int) exoPlayer.getCurrentPosition();
        } else {
            return mediaPlayer.getDuration() == -1L ? 0 : mediaPlayer.getCurrentPosition();
        }
    }

    public int getDuration() {
        if (exoPlayer != null) {
            return exoPlayer.getDuration() == -1L ? 0 : (int) this.exoPlayer.getDuration();
        } else {
            return mediaPlayer.getDuration() == -1L ? 0 : mediaPlayer.getDuration();
        }
    }

    public boolean isPlaying() {
        if (exoPlayer != null) {
            return exoPlayer.getPlayWhenReady();
        } else {
            return mediaPlayer.isPlaying();
        }
    }

    public void start() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
        } else {
            mediaPlayer.start();
        }
    }

    public void pause() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
        } else {
            mediaPlayer.pause();
        }
    }

    public void seekTo(int timeMillis) {
        if (exoPlayer != null) {
            long seekPosition = this.exoPlayer.getDuration() == -1L ? 0L : (long) Math.min(Math.max(0, timeMillis), this.getDuration());
            this.exoPlayer.seekTo(seekPosition);
        } else {
            long seekPosition = mediaPlayer.getDuration() == -1L ? 0L : (long) Math.min(Math.max(0, timeMillis), this.getDuration());
            mediaPlayer.seekTo((int) seekPosition);
        }
    }

}
