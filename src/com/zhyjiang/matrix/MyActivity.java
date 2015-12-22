package com.zhyjiang.matrix;

import android.app.Activity;
import android.os.Bundle;
import com.zhyjiang.matrix.view.GifView;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        GifView gifView = (GifView)findViewById(R.id.gif);
        gifView.setMovieResource(R.raw.number_rain);
    }
}
