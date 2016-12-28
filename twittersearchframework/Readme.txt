Usage:

- from your activity first call TweetsSearcher.getInstance().init(this); to init service
- in order to search for a hashtag call: TweetsSearcher.getInstance().searchTweets(this, str, TweetsListener);

 TweetsListener is a listener which return callback from tweets search:
 public void onTweetsReceived(List<Tweet> tweets); // tweets received succ, return a list of tweets
 public void onTweetsFailedToFetch(String reasonStr);  // error while fetching tweets including an error desc
 public void onSearchStarted();  // tweets search started

- call TweetsSearcher.getInstance().isUserLoggedToTwitter(); to find if user is already logged in
- In case a call to get tweets is made and the user is not logged in he will be redirected to log in
