package net.aldar.cramello.services.backPressed;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import net.aldar.cramello.view.MainActivity;

import java.util.List;

import static net.aldar.cramello.view.MainActivity.ADDRESSES_FRAGMENT_TAG;
import static net.aldar.cramello.view.MainActivity.CONTACT_FRAGMENT_TAG;
import static net.aldar.cramello.view.MainActivity.HOME_TAG;
import static net.aldar.cramello.view.MainActivity.NOTIFICATIONS_FRAGMENT_TAG;
import static net.aldar.cramello.view.MainActivity.OFFERS_FRAGMENT_TAG;
import static net.aldar.cramello.view.MainActivity.ORDERS_FRAGMENT_TAG;
import static net.aldar.cramello.view.MainActivity.PROFILE_FRAGMENT_TAG;

/**
 * Created by Ahmed Moharm on 5/16/2017.
 */
public class BackPressImpl implements OnBackPressListener {

    private Fragment parentFragment;

    public BackPressImpl(Fragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    @Override
    public boolean onBackPressed() {

        if (parentFragment == null) return false;

        String tag = parentFragment.getTag();
        int childCount = parentFragment.getChildFragmentManager().getBackStackEntryCount();

        if (childCount == 0) {
            // it has no child Fragment
            // can not handle the onBackPressed task by itself

            if (tag.equals(ADDRESSES_FRAGMENT_TAG) || tag.equals(OFFERS_FRAGMENT_TAG) ||
                    tag.equals(ORDERS_FRAGMENT_TAG) || tag.equals(NOTIFICATIONS_FRAGMENT_TAG) ||
                    tag.equals(CONTACT_FRAGMENT_TAG) || tag.equals(PROFILE_FRAGMENT_TAG)) {
                MainActivity activity = (MainActivity) parentFragment.getActivity();
                MainActivity.mCurrentTag = HOME_TAG;
            }

            return false;

        } else {
            // get the child Fragment

            int indexOfFragment = 0;

            FragmentManager childFragmentManager = parentFragment.getChildFragmentManager();
            List<Fragment> fragments = childFragmentManager.getFragments();

            if (tag.equals(CONTACT_FRAGMENT_TAG)) {
                if (fragments.size() == 2)
                    indexOfFragment = 1;
                else if (fragments.size() == 1) {
                    childFragmentManager.popBackStackImmediate();
                    return false;
                }
            }

            OnBackPressListener childFragment = (OnBackPressListener) childFragmentManager.getFragments().get(indexOfFragment);

            // propagate onBackPressed method call to the child Fragment
            if (!childFragment.onBackPressed()) {
                // child Fragment was unable to handle the task
                // It could happen when the child Fragment is last last leaf of a chain
                // removing the child Fragment from stack
                childFragmentManager.popBackStackImmediate();

            }

            // either this Fragment or its child handled the task
            // either way we are successful and done here
            return true;
        }
    }
}
