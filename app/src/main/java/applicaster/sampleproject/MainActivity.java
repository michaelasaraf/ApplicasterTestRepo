package applicaster.sampleproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import applicaster.sampleproject.adapters.TweetsListAdapter;
import applicaster.twittersearchframework.Activities.TwitterLoginActivity;
import applicaster.twittersearchframework.TweetsSearcher;
import applicaster.twittersearchframework.data_objects.Tweet;
import applicaster.twittersearchframework.interfaces.TweetsListener;

public class MainActivity extends Activity {

    EditText hashtagET;
    ListView tweetsLV;
    Button login_searchBT;

    // for adapter usage
    List<Tweet> tweets;
    TweetsListAdapter adapter;

    ProgressDialog progress;

    Context m_context;
    // check if the user is logged in

    private final String TAG = MainActivity.this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_context = this;

        // get views ref
        hashtagET = (EditText)findViewById(R.id.hashtagET);
        tweetsLV = (ListView)findViewById(R.id.tweetsLV);
        login_searchBT = (Button)findViewById(R.id.login_searchBT);
        setUpListView();

        // init the tweeter service
        TweetsSearcher.getInstance().init(this);


    }

    private void setUpListView() {

        // our adapter instance
        tweets = new ArrayList<>();
        adapter = new TweetsListAdapter(this,tweets);
        // set adapter to the tweets list
        tweetsLV.setAdapter(adapter);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void searchTweets(View view) {

        String str = hashtagET.getText().toString();
        // if search str is empty and user is logged in
        if((str == null || str.isEmpty()))
            Toast.makeText(this,getString(R.string.please_enter_hashtag_to_search),Toast.LENGTH_SHORT).show();
        else
        {
            // dont need to put # in search term
            if(str.startsWith("#"))
                Toast.makeText(this,getString(R.string.no_need_for_asterik_sign_it_will_be_added_automaticlly),Toast.LENGTH_SHORT).show();

            progress = ProgressDialog.show(this, "searching for #" + str + " tweets",getString(R.string.please_wait), true);

            adapter.clear();
            str = "#" + str;
            TweetsSearcher.getInstance().searchTweets(this, str, new TweetsListener() {
                // called when tweets received succ
                @Override
                public void onTweetsReceived(List<Tweet> tweets) {
                    updateListViewWithTweets(tweets);
                    if(progress != null)
                        progress.dismiss();
                    if(tweets == null || tweets.size() == 0) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(m_context, getString(R.string.no_tweets_found), Toast.LENGTH_LONG).show();
                                    }
                                }
                        );
                    }
                }
                // called when error occured while fetching tweetss
                @Override
                public void onTweetsFailedToFetch(final String reasonStr) {
                    // error occured while fetching tweets!
                    Log.e(TAG, reasonStr);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(
                            new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(m_context, reasonStr, Toast.LENGTH_LONG).show();
                                    if(progress != null)
                                        progress.dismiss();
                                }
                            }
                    );
                }
                @Override
                public void onSearchStarted() {

                }
            });
        }
    }
    // called when new tweets arrive
    private void updateListViewWithTweets(List<Tweet> tweets) {
        adapter.clear();
        adapter.addAll(tweets);
        // run on ui thread and update list
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(
                new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();  // notify the adapter so it will display the new items
                    }
                }
        );
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        isUserLogged = TweetsSearcher.getInstance().isUserLoggedToTwitter();
//        if(isUserLogged)
//        {
//            hashtagET.setText(getString(R.string.enter_hashtag_to_search));
//            hashtagET.setEnabled(true);
//            login_searchBT.setText(getString(R.string.search));
//        }
//        else
//        {
//            hashtagET.setHint(getString(R.string.user_not_logged_in));
//            hashtagET.setEnabled(false);
//            login_searchBT.setText(getString(R.string.login));
//        }
//    }
}
