package com.tomcat.lib;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.tomcat.lib.interfaces.Lazy;

/**
 * 创建者：   TomCat0916
 * 创建时间:  2019/7/28
 * 功能描述：  TODO
 */
public abstract class FragmentUtils<T extends Fragment> {
    
    private String TAG = getNewFragment().getClass().getSimpleName();

    @NonNull
    public Lazy<T> getLazySingleton(@NonNull final FragmentManager fragmentManager) {
        return new Lazy<T>() {
            private T fragment;
            @Override
            public synchronized T get() {
                if (fragment == null) {
                    fragment = getFragment(fragmentManager);
                }
                return fragment;
            }
        };
    }

    private T getFragment(@NonNull final FragmentManager fragmentManager) {
        T fragment = findFragment(fragmentManager);
        if (fragment == null) {
            fragment = getNewFragment();
            fragmentManager
                    .beginTransaction()
                    .add(fragment, TAG)
                    .commitNow();
        }
        return fragment;
    }

    private T findFragment(@NonNull final FragmentManager fragmentManager) {
        return (T) fragmentManager.findFragmentByTag(TAG);
    }
    
    abstract T getNewFragment();
}
