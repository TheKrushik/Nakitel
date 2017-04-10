package info.krushik.android.util;

import java.util.ArrayList;

import info.krushik.android.R;
import info.krushik.android.ui.MainActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;

public class RecordingsLoaderTask extends AsyncTask<String, Void, ArrayList<String>> {

    private Context context;
    private SwipeRefreshLayout swipeLayout;
    ListView recordingsListView;

    public RecordingsLoaderTask(SwipeRefreshLayout swipeLayout, ListView listView, Context context) {
        this.swipeLayout = swipeLayout;
        this.recordingsListView = listView;
        this.context = context;
    }

    @Override
    protected void onPostExecute(final ArrayList<String> listOfRecordings) {

        if (null != recordingsListView) {

            RecordingsListArrayAdapter arrayAdapter2 = new RecordingsListArrayAdapter(
                    context,
                    R.layout.recording_list_item,
                    ((MainActivity) context).getRecordings());

            recordingsListView.setAdapter(arrayAdapter2);
            recordingsListView.setFastScrollEnabled(true);
        }

        if (null != swipeLayout) {
            swipeLayout.setRefreshing(false);
        }

        super.onPostExecute(listOfRecordings);
    }

    @Override
    protected ArrayList<String> doInBackground(String... loadTask) {

        ((MainActivity) context).setRecordings(Helper.getHelperInstance().getAllRecordings());

        return ((MainActivity) context).getRecordings();
    }
}
