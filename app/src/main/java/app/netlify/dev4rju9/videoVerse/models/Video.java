package app.netlify.dev4rju9.videoVerse.models;

import android.net.Uri;

public class Video {

    String id, title, foderName, size, path;
    long duration = 0;
    Uri videoUri;

    public Video(String id, String title, String foderName, String size, String path, long duration, Uri videoUri) {
        this.id = id;
        this.title = title;
        this.foderName = foderName;
        this.size = size;
        this.path = path;
        this.duration = duration;
        this.videoUri = videoUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFoderName() {
        return foderName;
    }

    public void setFoderName(String foderName) {
        this.foderName = foderName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Uri getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(Uri videoUri) {
        this.videoUri = videoUri;
    }
}
