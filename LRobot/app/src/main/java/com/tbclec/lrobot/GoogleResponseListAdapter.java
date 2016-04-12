package com.tbclec.lrobot;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

	private LayoutInflater inflater;

	private LinkedList<Message.GoogleResponse> items;

	private ExternalIntentService externalIntentService;

	public GoogleResponseListAdapter(Context context) {

		inflater = LayoutInflater.from(context);

		items = new LinkedList<>();

		externalIntentService = new ExternalIntentService(context);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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
				externalIntentService.openLink(link);
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
}
