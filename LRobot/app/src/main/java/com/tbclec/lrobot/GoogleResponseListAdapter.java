package com.tbclec.lrobot;

import android.content.Context;
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

	public GoogleResponseListAdapter(Context context) {

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
		holder.link.setText(items.get(position).link);
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	// Update dataset
	public void setItems(List<Message.GoogleResponse> items) {

		this.items.clear();
		this.items.addAll(items);
	}
}
