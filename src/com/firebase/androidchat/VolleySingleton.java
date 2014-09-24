package com.firebase.androidchat;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {

	private static VolleySingleton instance;
	private RequestQueue requestQueue;
	private ImageLoader imageLoader;

	private VolleySingleton(Context context) {
		
		this.requestQueue = Volley.newRequestQueue(context
				.getApplicationContext());

		this.imageLoader = new ImageLoader(requestQueue, new ImageCache() {

			private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(
					20);

			@Override
			public void putBitmap(String url, Bitmap bitmap) {
				cache.put(url, bitmap);
			}

			@Override
			public Bitmap getBitmap(String url) {
				return cache.get(url);
			}
		});
	}

	public static VolleySingleton newInstance(Context context) {
		if (instance == null) {
			instance = new VolleySingleton(context);
		}
		return instance;
	}

	public RequestQueue getRequestQueue() {
		return requestQueue;
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public <T> void addToRequestQueue(Request<T> request) {
		getRequestQueue().add(request);
	}
}
