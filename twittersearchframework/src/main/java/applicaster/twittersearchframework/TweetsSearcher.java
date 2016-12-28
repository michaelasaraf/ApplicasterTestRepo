package applicaster.twittersearchframework;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.GpsStatus;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import applicaster.twittersearchframework.Activities.TwitterLoginActivity;
import applicaster.twittersearchframework.data_objects.Tweet;
import applicaster.twittersearchframework.interfaces.TweetsListener;
import applicaster.twittersearchframework.utils.NetworkUtils;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by MichaelAs on 12/27/2016.
 */
public class TweetsSearcher {

    private static TweetsSearcher instance;
    public static String TAG = TweetsSearcher.class.getSimpleName();
    private static SharedPreferences sharedPreferences; // shared prefrences for storing user tokens
    private String sharedPreferencesFileName = TweetsSearcher.class.getSimpleName();

    public static final String USER_ACCESS_TOKEN_KEY = "user_access_token";
    public static final String USER_SECRET_TOKEN_KEY = "user_secret_token";
    public static final String IS_USER_LOGGED_KEY = "is_user_logged";

    private final String  CONSUMER_KEY ="UcQhV1uHeIj82FFQCyHfH7RDX";
    private final String CONSUMER_SECRET ="sBuCKG01N0VJAF9BLazaKf9AdMOaGfp9qOyEUTmlZmqMVJw7JQ";

    private static String FLD_CODE_PREFIX = "FLD";
    private static String SUC_CODE = "SUC";

    private boolean isInit;


    private TweetsSearcher()
    {}

    // init searcher service with activity instance (in order to get sharedPreferences object)
    public void init(Activity context)
    {
        sharedPreferences = ((Activity) context).getSharedPreferences(sharedPreferencesFileName, Context.MODE_PRIVATE);
        isInit = true;
    }
    public static TweetsSearcher getInstance()
    {
        if(instance == null)
            instance = new TweetsSearcher();
        return instance;
    }
    // return true if user is logged false if not
    public boolean isUserLoggedToTwitter()
    {
        try {
            if (sharedPreferences != null)
                return sharedPreferences.getBoolean(IS_USER_LOGGED_KEY, false);
            else
                return false;
        }catch (Exception e)
        {
            return false;
        }
    }

    public void searchTweets(Activity activity,String hashtag, TweetsListener listener)
    {
        try {
            if(!isInit)
            {
                listener.onTweetsFailedToFetch("Service not init, call TweetsSearcher.getInstance().init(activity) before searching ");
            }
            if(NetworkUtils.isHaveNetworkConnection(activity))
            {
                if (!isLoggedToTwitter()) {
                    listener.onTweetsFailedToFetch("user not signed in, REDIRECTING TO TWITTER LOGIN");
                    Intent intent = new Intent(activity, TwitterLoginActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("mode", "login");
                    extras.putString("hashtag", hashtag);
                    intent.putExtras(extras);
                    activity.startActivity(intent);
                } else {
                    listener.onSearchStarted();
                    getTweetsWithHashTag(hashtag, listener);
                }
            }else
            {
                listener.onTweetsFailedToFetch("No internet connection, please connect and try again");
            }
        }catch (Exception e)
        {
            Log.e(TweetsSearcher.TAG,"" + e.getMessage());
        }
    }

    // check if the user is logged
    private boolean isLoggedToTwitter() {
        return sharedPreferences.getBoolean(IS_USER_LOGGED_KEY,false);
    }

    // search for tweets containing hashtag, also receive a listener which update the calling object
    private void getTweetsWithHashTag(String hashtag, TweetsListener listener) throws Exception {

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        // set app token
        configurationBuilder.setOAuthConsumerKey(CONSUMER_KEY);
        configurationBuilder.setOAuthConsumerSecret(CONSUMER_SECRET);
        Configuration configuration = configurationBuilder.build();
        TwitterFactory twitterFactory = new TwitterFactory(configuration);
        Twitter twitter = twitterFactory.getInstance();
        // set user token
        AccessToken accessToken = new AccessToken(sharedPreferences.getString(USER_ACCESS_TOKEN_KEY,""),sharedPreferences.getString(USER_SECRET_TOKEN_KEY,""));
        twitter.setOAuthAccessToken(accessToken);
        // get query
        Query query = new Query(hashtag);
        // start async search
        new getTweetsAsync(query,twitter,listener).execute();
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    //  AsyncTask
    private class getTweetsAsync extends AsyncTask<Void, Integer, String> {

        Query query;
        Twitter twitter;
        TweetsListener listener;

        // for returnning tweets
        List<Tweet> tweets;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        getTweetsAsync(Query query, Twitter twitter, TweetsListener listener)
        {
            this.query = query;
            this.twitter = twitter;
            this.listener = listener;
        }

        @Override
        protected  String doInBackground(Void... params) {

            try {
                QueryResult result = twitter.search(query);
                tweets  = new ArrayList<>();
                // for each status fill the array
                for (twitter4j.Status status : result.getTweets()) {
                    Tweet temp = new Tweet();
                    User user = status.getUser();
                    if(user.getProfileImageURL() != null)
                        temp.setImageURL(user.getProfileImageURL());
                    if(user.getName() != null)
                        temp.setName(user.getName());
                    else
                        temp.setName("unknown");
                    temp.setText(status.getText());
                    tweets.add(temp);
                    System.out.println("@" + status.getUser().getScreenName() + " : " + status.getText() + " : " + status.getGeoLocation());
                }
                return SUC_CODE;

            }catch (Exception e)
            {
                Log.e(TweetsSearcher.TAG, "" + e.getMessage());
                return "FLD" + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // case error occured - return the error code
            if(result.startsWith(FLD_CODE_PREFIX))
                listener.onTweetsFailedToFetch("error getting data from twitter please try again later");
            // case succ - return the tweets array
            else if(result.equals(SUC_CODE))
                listener.onTweetsReceived(tweets);

        }
    }
}
