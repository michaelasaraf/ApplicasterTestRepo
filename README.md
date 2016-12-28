# ApplicasterTestRepo
Contains twitter search framework for android including test project

SETUP:

To use the search library:

Navigate to twittersearchframework/build/outputs/aar and add the "twittersearchframework-release.aar" module to your project
See this on android aar module files and how to import it to android project: https://developer.android.com/studio/projects/android-library.html#AddDependency

Usage:

- from your activity first call TweetsSearcher.getInstance().init(this); to init service
- in order to search for a hashtag call: TweetsSearcher.getInstance().searchTweets(this, hashtag, TweetsListener);
  this will start an async call to twitter requesting the tweets containing hashtag

 TweetsListener is a listener which return callback from tweets search:
 public void onTweetsReceived(List<Tweet> tweets); // tweets received succ, return a list of tweets
 public void onTweetsFailedToFetch(String reasonStr);  // error while fetching tweets including an error desc
 public void onSearchStarted();  // tweets search started

- call TweetsSearcher.getInstance().isUserLoggedToTwitter(); to find if user is already logged in
- In case a call to get tweets is made and the user is not logged in he will be redirected to log in

Example project:

In order to use the example project:
- clone or download the repository
- Open the zip file (if downloading) in your computer
- From android studio click file -> open -> navigate to the folder path and click it

The example project contain the TweetsSearcher framework as a module dependency




