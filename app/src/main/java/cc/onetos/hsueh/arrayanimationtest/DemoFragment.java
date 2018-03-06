package cc.onetos.hsueh.arrayanimationtest;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

/**
 * Created with Android Studio.
 *
 * @author Hsueh
 * @email hsueh@onetos.cc
 * @date 2017-12-26  10:41
 */

public class DemoFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    private static class MyHandler extends Handler{
        private WeakReference<DemoFragment> mReference;
        private MyHandler(DemoFragment fragment){
            mReference = new WeakReference<DemoFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {


        }
    }
}
