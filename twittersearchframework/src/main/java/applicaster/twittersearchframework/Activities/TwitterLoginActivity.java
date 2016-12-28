package applicaster.twittersearchframework.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import applicaster.twittersearchframework.TweetsSearcher;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by MichaelAs on 12/28/2016.
 */
public class TwitterLoginActivity extends Activity {

    static private Twitter twitter;
    static private RequestToken requestToken;
    private final String TWITTER_CALLBACK_URL = "oauth://t4jsample";
    private final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";

    private static final int MODE_GET_REQUEST_TOKEN = 0;
    private static final int MODE_GET_ACCESS_TOKEN = 1;

    // app consumer keys
    private final String  CONSUMER_KEY ="UcQhV1uHeIj82FFQCyHfH7RDX";
    private final String CONSUMER_SECRET ="sBuCKG01N0VJAF9BLazaKf9AdMOaGfp9qOyEUTmlZmqMVJw7JQ";

    Context m_context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // case the activity is called for login purpose
        if(getIntent() != null && getIntent().getExtras() !=null) {
            Bundle extras = getIntent().getExtras();
            if(extras.getString("mode") != null && extras.getString("mode").equals("login"))
            {
                loginToTwitter();
            }
        }

        // case its a callback from twitter after logging
        if(getIntent().getData() != null && getIntent().getData().toString().startsWith(TWITTER_CALLBACK_URL)) {
            Uri uri = getIntent().getData();
            if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
                // oAuth verifier
                String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
                try {
                    // Get the access token
                    new getAccessTokenAsync(MODE_GET_ACCESS_TOKEN,verifier).execute();

                } catch (Exception e) {
                    // Check log for login errors
                    Log.e(TweetsSearcher.TAG, "" + e.getMessage());
                }
            }
        }

        finish();
    }

    public void loginToTwitter() {
        // Check if already logged in
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(CONSUMER_KEY);
        builder.setOAuthConsumerSecret(CONSUMER_SECRET);
        Configuration configuration = builder.build();
        TwitterFactory factory = new TwitterFactory(configuration);
        twitter = factory.getInstance();
        // start access token async request
        new getAccessTokenAsync(MODE_GET_REQUEST_TOKEN,"").execute();

    }

    //  AsyncTask to get access token
    private class getAccessTokenAsync extends AsyncTask<Void, Integer, String> {

        int mode;
        String verifier;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        getAccessTokenAsync(int mode, String verifier)
        {
            this.mode = mode;
            this.verifier = verifier;
        }

        @Override
        protected  String doInBackground(Void... params) {

            try {
                // if request token is needed
                if(mode == MODE_GET_REQUEST_TOKEN) {
                    requestToken = twitter.getOAuthRequestToken("oauth://t4jsample");
                    // start the login activity
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
                    // finish the helper activity

                }
                // if request token is needed
                else if(mode == MODE_GET_ACCESS_TOKEN)
                {
                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                    // update shared preferences with the user acees token
                    SharedPreferences.Editor editor = TweetsSearcher.getInstance().getSharedPreferences().edit();
                    editor.putString(TweetsSearcher.USER_ACCESS_TOKEN_KEY, accessToken.getToken());
                    editor.putString(TweetsSearcher.USER_SECRET_TOKEN_KEY, accessToken.getTokenSecret());
                    // Store login status - true
                    editor.putBoolean(TweetsSearcher.IS_USER_LOGGED_KEY, true);
                    editor.commit();
                }
            }catch (Exception e) {
                Log.e(TweetsSearcher.TAG, "" + e.getMessage());
            }
            return "";

        }

        @Override
        protected void onPostExecute(String result) {

        }
    }
}
