package info.krushik.android.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import info.krushik.android.R;
import info.krushik.android.ui.MainActivity;

public class RecordingsListArrayAdapter extends ArrayAdapter<String> {

    public RecordingsListArrayAdapter(Context context, int resource, List<String> listOfRecordings) {
        super(context, resource, listOfRecordings);

        this.context = context;
        this.listOfRecordings = listOfRecordings;
    }

    private final Context context;
    private final List<String> listOfRecordings;
    private ViewHolder holder;

    private class ViewHolder {
        TextView recordingName;
        ImageView deleteSong;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.recording_list_item, parent, false);

            holder = new ViewHolder();

            holder.recordingName = (TextView) convertView.findViewById(R.id.textViewRecordingName);
            holder.recordingName.setSelected(true);

            holder.deleteSong = (ImageView) convertView.findViewById(R.id.delete_song);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.recordingName.setText(((MainActivity) context).getRecordings().get(position));

        holder.deleteSong.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.getHelperInstance().makeHepticFeedback(context);

                if (new File(((MainActivity) context).getRecordings().get(position)).delete()) {
                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_LONG).show();

                    ((MainActivity) context).getRecordings().remove(position);
                    notifyDataSetChanged();
                    ((MainActivity) context).getmMediaPlayer().reset();
                }
            }

        });

        return convertView;
    }
}
