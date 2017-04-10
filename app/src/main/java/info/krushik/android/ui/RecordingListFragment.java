package info.krushik.android.ui;

import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import info.krushik.android.R;
import info.krushik.android.customview.PlayerVisualizerView;
import info.krushik.android.util.Helper;
import info.krushik.android.util.RecordingsLoaderTask;

public class RecordingListFragment extends Fragment implements OnRefreshListener {

    private static final float VISUALIZER_HEIGHT_DIP = 100f;
    private SwipeRefreshLayout swipeLayout;
    private ListView recordingsListView;
    private LinearLayout mLinearLayout;
    private info.krushik.android.customview.PlayerVisualizerView mVisualizerView;
    private Visualizer mVisualizer;
    private View rootView;
    private boolean SONGPAUSED;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.recording_list_fragment, container, false);

        ((MainActivity) getActivity()).RecordingNumber = 0;

        recordingsListView = (ListView) rootView.findViewById(R.id.listView_Recording);

        new RecordingsLoaderTask(swipeLayout, recordingsListView,
                getActivity()).execute(Helper.LOAD_RECORDINGS);

        recordingsListView.setFastScrollEnabled(true);

        // listen for when the music stream ends playing
        ((MainActivity) getActivity()).getmMediaPlayer().setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        // disable the visualizer as it's no longer
                        // needed

                        if (null != mVisualizer)
                            mVisualizer.setEnabled(false);

                        rootView.findViewById(R.id.btnPauseSlider).setVisibility(View.GONE);
                        rootView.findViewById(R.id.btnPlaySlider).setVisibility(View.VISIBLE);
                    }
                });

        mLinearLayout = (LinearLayout) rootView.findViewById(R.id.linearLayoutVisual);
        // Create a VisualizerView to display the audio waveform for the current settings
        mVisualizerView = new PlayerVisualizerView(getActivity());
        mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (VISUALIZER_HEIGHT_DIP * getResources().getDisplayMetrics().density)));
        mLinearLayout.addView(mVisualizerView);

        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(((MainActivity) getActivity()).getmMediaPlayer().getAudioSessionId());

        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                mVisualizerView.updateVisualizer(bytes);
            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);

        mVisualizer.setEnabled(true);

        rootView.findViewById(R.id.btnPauseSlider).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Helper.getHelperInstance().makeHepticFeedback(getActivity());

                if (null != mVisualizer)
                    mVisualizer.setEnabled(false);

                ((MainActivity) getActivity()).getmMediaPlayer().pause();

                SONGPAUSED = true;

                rootView.findViewById(R.id.btnPauseSlider).setVisibility(View.GONE);
                rootView.findViewById(R.id.btnPlaySlider).setVisibility(View.VISIBLE);
            }
        });

        rootView.findViewById(R.id.btnPlaySlider).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Helper.getHelperInstance().makeHepticFeedback(getActivity());

                if (SONGPAUSED) {
                    resumeSong();
                } else {
                    playSong(((MainActivity) getActivity()).RecordingNumber);
                }
            }
        });

        recordingsListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ((MainActivity) getActivity()).RecordingNumber = position;
                playSong(position);
            }
        });

        return rootView;
    }

    @Override
    public void onRefresh() {
        new RecordingsLoaderTask(swipeLayout, recordingsListView,
                getActivity()).execute(Helper.LOAD_RECORDINGS);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (null != mVisualizer) {
            mVisualizer.release();
        }
        mVisualizer = null;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (null != mVisualizer) {
            mVisualizer.release();
        }
        mVisualizer = null;
        ((MainActivity) getActivity()).getmMediaPlayer().reset();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private void playSong(int RecordingNumber) {
        MediaPlayer mMediaPlayer = ((MainActivity) getActivity()).getmMediaPlayer();

        if (null != mVisualizer)
            mVisualizer.setEnabled(true);

        if (null != mMediaPlayer && !((MainActivity) getActivity()).getRecordings().isEmpty()) {
            mMediaPlayer.reset();
            try {
                mMediaPlayer.setDataSource(((MainActivity) getActivity()).getRecordings().get(RecordingNumber).toString());

                mMediaPlayer.prepare();
                mMediaPlayer.start();

                rootView.findViewById(R.id.btnPauseSlider).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.btnPlaySlider).setVisibility(View.GONE);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void resumeSong() {
        if (null != mVisualizer)
            mVisualizer.setEnabled(true);

        ((MainActivity) getActivity()).getmMediaPlayer().start();
        rootView.findViewById(R.id.btnPauseSlider).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.btnPlaySlider).setVisibility(View.GONE);
        SONGPAUSED = false;
    }
}
