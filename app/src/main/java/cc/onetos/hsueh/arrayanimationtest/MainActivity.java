package cc.onetos.hsueh.arrayanimationtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import cc.onetos.hsueh.frameloader.FrameLoader;


public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;
    private ImageView mImageView2;
    private int[] mTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.imageview);
        mImageView2 = findViewById(R.id.imageview2);

        int a[] = DrawableArray.generateDrawableArray(this, "blink", 0, 53);
//        int b[] = DrawableArray.generateDrawableArray(this,"happy",1,13);
        int c[] = DrawableArray.generateDrawableArray(this, "sleeply", 1, 39);
        int d[] = DrawableArray.generateDrawableArray(this, "sorry", 1, 26);
        int f[] = DrawableArray.generateDrawableArray(this, "boshi", 1, 58);
        ArrayList<Integer> list = new ArrayList<>();
        int length = a.length + c.length + d.length + f.length;
        for (int i = 0; i < a.length; i++) {
            list.add(a[i]);
        }
//        for (int i = 0; i < b.length; i++) {
//            list.add(b[i]);
//        }
        for (int i = 0; i < c.length; i++) {
            list.add(c[i]);
        }
        for (int i = 0; i < d.length; i++) {
            list.add(d[i]);
        }
        for (int i = 0; i < f.length; i++) {
            list.add(f[i]);
        }
        mTest = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            mTest[i] = list.get(i);
        }


       FrameLoader.into(mImageView)
                .load(mTest)
                .setEndlessLoop(true)
                .setLoopCount(3)
                .play(200);


        FrameLoader.into(mImageView2)
                .load(mTest)
                .play(300);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FrameLoader.getProcessByTag(mImageView).stop();
//                FrameLoader.into(mImageView)
//                        .load(mTest)
//                        .setEndlessLoop(true)
//                        .setLoopCount(3)
//                        .play(200);
                FrameLoader.into(mImageView).stop();
            }
        });
        mImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLoader.into(mImageView2).stop();
            }
        });


    }

}
