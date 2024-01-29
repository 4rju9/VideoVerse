package app.netlify.dev4rju9.videoVerse;

import android.view.View;

public class DoubleClickListener implements View.OnClickListener {
    private long doubleClickTimeLimitMills = 500;
    private Callback callback;
    private long lastClicked = -1L;

    public DoubleClickListener(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onClick(View v) {
        if (lastClicked == -1L) {
            lastClicked = System.currentTimeMillis();
        } else if (isDoubleClicked()) {
            callback.doubleClicked();
            lastClicked = -1L;
        } else {
            lastClicked = System.currentTimeMillis();
        }
    }

    private long getTimeDiff(long from, long to) {
        return to - from;
    }

    private boolean isDoubleClicked() {
        return getTimeDiff(lastClicked, System.currentTimeMillis()) <= doubleClickTimeLimitMills;
    }

    public interface Callback {
        void doubleClicked();
    }
}