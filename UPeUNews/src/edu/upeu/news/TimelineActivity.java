package edu.upeu.news;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import edu.upeu.news.list.TweetAdapter;
import edu.upeu.news.model.Tweet;
import edu.upeu.news.utils.ConstantsUtils;
import edu.upeu.news.utils.TwitterUtils;

public class TimelineActivity extends Activity {

	private ListView lvTimeline;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);

		lvTimeline = (ListView) findViewById(R.id.lv_timeline);
//		new TweetSearch().execute();

		Button btn_submit = (Button) findViewById(R.id.btn_buscar);
		btn_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new TweetSearch().execute();
			}
		});
	}

	public void updateListView(ArrayList<Tweet> tweets) {
		lvTimeline
				.setAdapter(new TweetAdapter(this, R.layout.row_tweet, tweets));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.timeline, menu);
		return true;
	}

	class TweetSearch extends AsyncTask<Object, Void, ArrayList<Tweet>> {

		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			dialog = new ProgressDialog(TimelineActivity.this);
			dialog.setMessage(getResources().getString(
					R.string.label_tweet_search_loader));
			dialog.show();

		}

		@Override
		protected ArrayList<Tweet> doInBackground(Object... params) {

			ArrayList<Tweet> tweets = new ArrayList<Tweet>();
			final EditText input_name = (EditText) findViewById(R.id.txtbuscar);
			
			try {
				
				String name = input_name.getText().toString();

				

				String timeline = TwitterUtils
						.getTimelineForSearchTerm(name);

				JSONObject jsonResponse = new JSONObject(timeline);
				JSONArray jsonArray = jsonResponse.getJSONArray("statuses");

				JSONObject jsonObject;

				for (int i = 0; i < jsonArray.length(); i++) {
					jsonObject = (JSONObject) jsonArray.get(i);

					Tweet tweet = new Tweet();
					tweet.setName(jsonObject.getJSONObject("user").getString(
							"name"));
					tweet.setScreenName(jsonObject.getJSONObject("user")
							.getString("screen_name"));
					tweet.setProfileImageUrl(jsonObject.getJSONObject("user")
							.getString("profile_image_url"));
					tweet.setText(jsonObject.getString("text"));
					tweet.setCreatedAt(jsonObject.getString("created_at"));

					tweets.add(i, tweet);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return tweets;
		}

		/***
		 * Metodo que se ejecuta al finalizar la tarea
		 */
		@Override
		protected void onPostExecute(ArrayList<Tweet> tweets) {
			super.onPostExecute(tweets);
			// Desaparecemos el Progress dialog
			dialog.dismiss();

			if (tweets.isEmpty()) {
				Toast.makeText(
						TimelineActivity.this,
						getResources().getString(
								R.string.label_tweets_not_found),
						Toast.LENGTH_SHORT).show();
			} else {
				updateListView(tweets);
			}

		}
	}

}
