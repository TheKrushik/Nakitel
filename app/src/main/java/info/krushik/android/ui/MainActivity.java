package info.krushik.android.ui;

import java.util.ArrayList;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import info.krushik.android.R;

public class MainActivity extends FragmentActivity {

    MediaPlayer mMediaPlayer;
    private ArrayList<String> recordings = new ArrayList<String>();
    public int RecordingNumber;


    public MediaPlayer getmMediaPlayer() {
        return mMediaPlayer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mMediaPlayer = new MediaPlayer();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new RecordAudioFragment());
        fragmentTransaction.addToBackStack("RecordAudioFragment");
        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer = null;
        }
    }

    public ArrayList<String> getRecordings() {
        return recordings;
    }

    public void setRecordings(ArrayList<String> recordings) {
        this.recordings = recordings;
    }

}
