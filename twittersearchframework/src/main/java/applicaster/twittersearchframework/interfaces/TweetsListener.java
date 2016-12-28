package applicaster.twittersearchframework.interfaces;

import java.util.List;

import applicaster.twittersearchframework.data_objects.Tweet;

/**
 * Created by MichaelAs on 12/28/2016.
 */
public interface TweetsListener {


    public void onTweetsReceived(List<Tweet> tweets); // tweets received succ, return a list of tweets
    public void onTweetsFailedToFetch(String reasonStr);  // error while fetching tweets
    public void onSearchStarted();  // tweets search started
}
