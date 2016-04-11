package com.tbclec.lrobot;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Bogdan on 11/04/2016.
 */
public class GoogleResponseListAdapter extends RecyclerView.Adapter<GoogleResponseListAdapter.ViewHolder> {

	public static class ViewHolder extends RecyclerView.ViewHolder {

		public TextView title;
		public TextView description;
		public TextView link;

		public ViewHolder(View v) {
			super(v);

			title = (TextView) v.findViewById(R.id.google_response_title);
			description = (TextView) v.findViewById(R.id.google_response_description);
			link = (TextView) v.findViewById(R.id.google_response_link);
		}
	}

	private Context context;
	private LayoutInflater inflater;

	private LinkedList<Message.GoogleResponse> items;

	public GoogleResponseListAdapter(Context context) {

		this.context = context;

		inflater = LayoutInflater.from(context);

		items = new LinkedList<>();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		// Create a new view
		View v = inflater.inflate(R.layout.google_response_item, parent, false);

		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {

		holder.title.setText(items.get(position).title);
		holder.description.setText(items.get(position).description);

		TextView linkTextView = holder.link;

		final String link = items.get(position).link;
		linkTextView.setText(link);
		linkTextView.setPaintFlags(linkTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

		linkTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openLink(link);
			}
		});
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	public void setItems(List<Message.GoogleResponse> items) {

		this.items.clear();
		this.items.addAll(items);
	}

	private void openLink(String link) {

		try {
			Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
			context.startActivity(myIntent);
		}
		catch (ActivityNotFoundException e) {
			Toast.makeText(context, "No application can handle this request. " +
					"Please install a web browser", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
}
