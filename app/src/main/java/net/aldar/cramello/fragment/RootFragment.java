package net.aldar.cramello.fragment;

import android.support.v4.app.Fragment;

import net.aldar.cramello.services.backPressed.BackPressImpl;
import net.aldar.cramello.services.backPressed.OnBackPressListener;

public class RootFragment extends Fragment implements OnBackPressListener {

    @Override
    public boolean onBackPressed() {
        return new BackPressImpl(this).onBackPressed();
    }
}
