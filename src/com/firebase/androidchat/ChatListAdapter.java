package com.firebase.androidchat;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.firebase.client.Query;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;

/**
 * User: greg
 * Date: 6/21/13
 * Time: 2:39 PM
 */

/**
 * This class is an example of how to use FirebaseListAdapter. It uses the
 * <code>Chat</code> class to encapsulate the data for each individual chat
 * message
 */
public class ChatListAdapter extends FirebaseListAdapter<Chat> implements
		YouTubePlayer.OnInitializedListener {

	// The username for this client. We use this to indicate which messages
	// originated from this user
	private VolleySingleton instanceOfVolley;
	private String url, videoID, username;
	private Response.Listener<JSONObject> listener;
	private Response.ErrorListener errorListener;
	private Context context;
	private Activity activity;

	public ChatListAdapter(Query ref, Activity activity, int layout,
			String username) {
		super(ref, Chat.class, layout, activity);
		this.username = username;
		this.activity = activity;
		this.context = activity.getApplicationContext();
	}

	/**
	 * Bind an instance of the <code>Chat</code> class to our view. This method
	 * is called by <code>FirebaseListAdapter</code> when there is a data
	 * change, and we are given an instance of a View that corresponds to the
	 * layout that we passed to the constructor, as well as a single
	 * <code>Chat</code> instance that represents the current data to bind.
	 * 
	 * @param view
	 *            A view instance corresponding to the layout we passed to the
	 *            constructor.
	 * @param chat
	 *            An instance representing the current state of a chat message
	 */
	@Override
	protected void populateView(View view, Chat chat) {
		// Map a Chat object to an entry in our listview
		String author = chat.getAuthor();
		TextView authorText = (TextView) view.findViewById(R.id.author);
		authorText.setText(author + ": ");
		instanceOfVolley = VolleySingleton.newInstance(context);

		final NetworkImageView imageView = (NetworkImageView) view
				.findViewById(R.id.networkImageView);
		final ImageLoader imageLoader = instanceOfVolley.getImageLoader();
		final TextView titleTextView = (TextView) view.findViewById(R.id.title);
		final TextView descriptionTextView = (TextView) view
				.findViewById(R.id.description);

		// If the message was sent by this user, color it differently
		if (author.equals(username)) {
			authorText.setTextColor(Color.RED);
		} else {
			authorText.setTextColor(Color.BLUE);
		}
		((TextView) view.findViewById(R.id.message)).setText(chat.getMessage());

		String message = chat.getMessage();
		int start = message.indexOf("((");
		if (start != -1) {
			String query = message.substring(start + 2, message.indexOf("))"));

			// the volley listener
			listener = new Response.Listener<JSONObject>() {

				@Override
				public void onResponse(JSONObject response) {
					try {
						JSONArray items = response.getJSONArray("items");
						JSONObject json = items.getJSONObject(0);
						JSONObject id = json.getJSONObject("id");
						JSONObject snippet = json.getJSONObject("snippet");
						JSONObject thumbnails = snippet
								.getJSONObject("thumbnails");
						JSONObject high = thumbnails.getJSONObject("high");
						String url = high.getString("url");
						String description = snippet.getString("description");
						String title = snippet.getString("title");
						titleTextView.setText(title);
						descriptionTextView.setText(description);
						imageView.setImageUrl(url, imageLoader);
						videoID = id.getString("videoId");
						imageView.setVisibility(View.VISIBLE);
						titleTextView.setVisibility(View.VISIBLE);
						descriptionTextView.setVisibility(View.VISIBLE);
						imageView.setTag(videoID);
						imageView
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View view) {
										String tag = view.getTag().toString();
										Intent intent = new Intent(activity,
												PlayActivity.class);
										intent.putExtra("id", tag);
										activity.startActivity(intent);
									}
								});
					} catch (JSONException ex) {
						ex.printStackTrace();
					}
				}
			};

			// the volley error listener
			errorListener = new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					error.printStackTrace();
				}
			};

			try {
				url = Config.URL + URLEncoder.encode(query, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			JsonObjectRequest req = new JsonObjectRequest(url, null, listener,
					errorListener);
			instanceOfVolley.addToRequestQueue(req);

		} else {
			imageView.setVisibility(View.GONE);
			titleTextView.setVisibility(View.GONE);
			descriptionTextView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult arg1) {

	}

	@Override
	public void onInitializationSuccess(Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		if (!wasRestored) {
			Log.d(" Video ID", videoID);
			player.loadVideo(videoID);
		}
	}
}
