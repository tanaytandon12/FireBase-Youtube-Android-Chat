package com.firebase.androidchat;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

public class PlayActivity extends YouTubeBaseActivity  implements YouTubePlayer.OnInitializedListener{
	private String videoId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_activity);
		Intent intent = getIntent();
		videoId = intent.getStringExtra("id");
		YouTubePlayerView youTubePlayer = (YouTubePlayerView) findViewById(R.id.youtube_view);
		youTubePlayer.initialize(Config.DEVELOPER_KEY, this);
	}

	@Override
	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult arg1) {
		
	}

	@Override
	public void onInitializationSuccess(Provider provider, YouTubePlayer player,
			boolean wasRestored) {
		if (!wasRestored) { 
			player.cueVideo(videoId);
		}
	}
}
