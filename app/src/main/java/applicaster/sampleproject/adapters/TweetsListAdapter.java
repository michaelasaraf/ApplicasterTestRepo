package applicaster.sampleproject.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import applicaster.sampleproject.R;
import applicaster.twittersearchframework.TweetsSearcher;
import applicaster.twittersearchframework.data_objects.Tweet;
import applicaster.twittersearchframework.interfaces.TweetsListener;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.User;

/**
 * Created by MichaelAs on 12/28/2016.
 */
public class TweetsListAdapter extends ArrayAdapter<Tweet> {
    private Context context;
    private List<Tweet> values;
    private int lastPosition = -1;

    public TweetsListAdapter(Context context, List<Tweet> values) {

        super(context, R.layout.tweet_row_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);



        View rowView = inflater.inflate(R.layout.tweet_row_item, parent, false);
        TextView user_name = (TextView) rowView.findViewById(R.id.user_name);
        TextView tweetText = (TextView) rowView.findViewById(R.id.tweetText);
        ImageView avatarIV = (ImageView) rowView.findViewById(R.id.avatarIV);
        RelativeLayout mainRL = (RelativeLayout) rowView.findViewById(R.id.mainRL);

        user_name.setText(values.get(position).getName());
        tweetText.setText(values.get(position).getText());

        try {
            // load image from url
            if (values.get(position).getImageURL() != null && !values.get(position).getImageURL().isEmpty() && !values.get(position).isImageLoaded()) {
                new getImageURLAsync(avatarIV,values.get(position)).execute();
            }else
            {
                avatarIV.setImageBitmap(values.get(position).getBitmap());
            }
            setAnimation(mainRL,position);
        }catch (Exception e)
        {
            Log.e(TweetsSearcher.TAG, "" + e.getMessage());
        }

        return rowView;
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        //If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }


    // DownloadJSON AsyncTask
    private class getImageURLAsync extends AsyncTask<Void, Integer, String> {

        ImageView imageView;
        Tweet tweet;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        getImageURLAsync(ImageView imageView, Tweet tweet)
        {
            this.imageView = imageView;
            this.tweet = tweet;
        }

        @Override
        protected  String doInBackground(Void... params) {

            try {
                URL url = new URL(tweet.getImageURL());
                final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bmp);
                                tweet.setIsImageLoaded(true);
                                tweet.setBitmap(bmp);
                            }
                        }
                );


            } catch (Exception e) {
                Log.e(TweetsSearcher.TAG, "" + e.getMessage());
                return "FLD" + e.getMessage();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {


        }
    }
}