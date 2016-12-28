package applicaster.twittersearchframework.data_objects;

import android.graphics.Bitmap;

/**
 * Created by MichaelAs on 12/28/2016.
 */
public class Tweet {

    String name;
    String text;
    String imageURL;

    public boolean isImageLoaded() {
        return isImageLoaded;
    }

    public void setIsImageLoaded(boolean isImageLoaded) {
        this.isImageLoaded = isImageLoaded;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    boolean isImageLoaded;
    Bitmap bitmap;

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
