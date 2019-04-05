package net.aldar.cramello;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.aldar.cramello.apiHandler.BaseApi;
import net.aldar.cramello.apiHandler.BaseApiHandler;
import net.aldar.cramello.fragment.AddressesFragment;
import net.aldar.cramello.fragment.ContactUsFragment;
import net.aldar.cramello.fragment.MenuFragment;
import net.aldar.cramello.fragment.MyOrdersFragment;
import net.aldar.cramello.fragment.MyProfileFragment;
import net.aldar.cramello.fragment.NotificationsFragment;
import net.aldar.cramello.fragment.OffersFragment;
import net.aldar.cramello.model.Basket;
import net.aldar.cramello.model.SocialLink;
import net.aldar.cramello.model.response.UserData;
import net.aldar.cramello.model.response.governorate.Governorate;
import net.aldar.cramello.model.response.product.Product;
import net.aldar.cramello.model.response.product.ProductCategory;
import net.aldar.cramello.services.LocaleHelper;
import net.aldar.cramello.services.PrefsManger;
import net.aldar.cramello.services.Utils;
import net.aldar.cramello.services.backPressed.OnBackPressListener;
import net.aldar.cramello.view.DcDialog;
import net.aldar.cramello.view.listener.OnClickRetryBtn;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.aldar.cramello.App.KEY_OPEN_NOTIFICATIONS;
import static net.aldar.cramello.App.KEY_OPEN_ORDERS;
import static net.aldar.cramello.App.mMontserratRegular;
import static net.aldar.cramello.App.mRobotoRegular;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static String REQUEST_ACCEPT = "NOTIFICATION RECEIVED";
    public static final int REQUEST_PRODUCT_DETAIL = 11;

    public static final String HOME_TAG = "HOME";
    public static final String ADDRESSES_FRAGMENT_TAG = "AddressesFragmentTag";
    public static final String ADD_ADDRESS_FRAGMENT_TAG = "AddAddressFragmentTag";
    public static final String MENU_FRAGMENT_TAG = "MenuFragmentTag";
    public static final String OFFERS_FRAGMENT_TAG = "OffersFragmentTag";
    public static final String ORDERS_FRAGMENT_TAG = "OrdersFragmentTag";
    public static final String NOTIFICATIONS_FRAGMENT_TAG = "NotificationsFragmentTag";
    public static final String PROFILE_FRAGMENT_TAG = "ProfileFragmentTag";
    public static final String CONTACT_FRAGMENT_TAG = "ContactFragmentTag";
    public static final String BRANCHES_FRAGMENT_TAG = "BranchesFragmentTag";
    public static final String CHANGE_PW_FRAGMENT_TAG = "ChangePasswordFragmentTag";
    public static final String ORDER_DETAIL_FRAGMENT_TAG = "OrderDetailFragmentTag";

    private final int ERROR_GET_GOVERNORATE = 1;
    private final int ERROR_GET_CATEGORY = 2;
    private final int ERROR_GET_PRODUCT = 3;
    //    private final int ERROR_GET_MIN_ORDER = 4;
    private final int ERROR_GET_USER_DATA = 5;
    private final int ERROR_GET_BASKET = 6;
    private final int ERROR_GET_SOCIAL_LINKS = 7;

    public String mAppLanguage;
    public String mAuthToken;
    public int mUserId;
    public int mDrawerGravity;

    public static String mCurrentTag;

    private LinearLayout mHomeLayout;
    private TextView mHomeTv;

    private LinearLayout mMyOrdersLayout;
    private TextView mMyOrdersTv;

    private LinearLayout mOffersLayout;
    private TextView mOffersTv;

    private LinearLayout mNotificationLayout;
    private TextView mNotificationTv;
    private TextView mNotificationUnSeenCountTv;

    private LinearLayout mMyAddressesLayout;
    private TextView mMyAddressesTv;

    private LinearLayout mMyProfileLayout;
    private TextView mMyProfileTv;

    private LinearLayout mContactUsLayout;
    private TextView mContactUsTv;

    private LinearLayout mShareAppLayout;
    private TextView mShareAppTv;

    private Button mSignOutBtn;

    private ImageView mFacebookIv;
    private ImageView mTwitterIv;
    private ImageView mInstagramIv;
    private ImageView mSnapChatIv;
    private ImageView mYoutubeIv;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    //   -----------------

    private LinearLayout mMenuLayout;
    private ImageView mMenuIv;

    private LinearLayout mThreeDotsLayout;

    private LinearLayout mChooseAddressLayout;
    private TextView mChooseAddressTitle;
    private ImageView mChooseAddressArrowIv;

    private TextView mMyOrdersBtn;

    public LinearLayout mSpinKitLayout;

    public PrefsManger mPrefsManger;
    public BaseApi mServiceApi;
    public UserData mUserData;
//    public Double mMinOrder = 0.0;

    public List<Governorate> mGovernorateList;
    public List<ProductCategory> mCategoryList;
    public List<Product> mProductsList;

    private String mFacebookLink;
    private String mTwitterLink;
    private String mInstagramLink;
    private String mSnapChatLink;
    private String mYoutubeLink;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                checkUnSeenNotification();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        mPrefsManger = new PrefsManger(newBase);
        super.attachBaseContext(LocaleHelper.wrap(newBase, mPrefsManger.getAppLanguage()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mServiceApi = BaseApiHandler.setupBaseApi().create(BaseApi.class);
        mAppLanguage = mPrefsManger.getAppLanguage();
        mAuthToken = "Token ".concat(mPrefsManger.getLoginToken());
        mUserData = mPrefsManger.loadUserData();
        mUserId = mUserData.getId();

        mDrawerGravity = Utils.getDrawerGravity(mPrefsManger);

        setupNavigationView();
        setupHomeView();
    }

    private void setupNavigationView() {

        mDrawerLayout = findViewById(R.id.sideMenu_drawerLayout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        mNavigationView = findViewById(R.id.sideMenu_navigationView);

        mHomeLayout = mNavigationView.findViewById(R.id.sideMenu_homeLayout);
        mHomeTv = mNavigationView.findViewById(R.id.sideMenu_homeTv);
        mHomeTv.setTypeface(mRobotoRegular);
        mHomeLayout.setOnClickListener(this);

        mMyOrdersLayout = mNavigationView.findViewById(R.id.sideMenu_ordersLayout);
        mMyOrdersTv = mNavigationView.findViewById(R.id.sideMenu_ordersTv);
        mMyOrdersTv.setTypeface(mRobotoRegular);
        mMyOrdersLayout.setOnClickListener(this);

        mOffersLayout = mNavigationView.findViewById(R.id.sideMenu_offersLayout);
        mOffersTv = mNavigationView.findViewById(R.id.sideMenu_offersTv);
        mOffersTv.setTypeface(mRobotoRegular);
        mOffersLayout.setOnClickListener(this);

        mNotificationLayout = mNavigationView.findViewById(R.id.sideMenu_notificationsLayout);
        mNotificationTv = mNavigationView.findViewById(R.id.sideMenu_notificationsTv);
        mNotificationTv.setTypeface(mRobotoRegular);
        mNotificationUnSeenCountTv = mNavigationView.findViewById(R.id.sideMenu_notificationsCountTv);
        mNotificationUnSeenCountTv.setTypeface(mRobotoRegular);
        mNotificationLayout.setOnClickListener(this);

        mContactUsLayout = mNavigationView.findViewById(R.id.sideMenu_contactUsLayout);
        mContactUsTv = mNavigationView.findViewById(R.id.sideMenu_contactUsTv);
        mContactUsTv.setTypeface(mRobotoRegular);
        mContactUsLayout.setOnClickListener(this);

        mMyAddressesLayout = mNavigationView.findViewById(R.id.sideMenu_addressesLayout);
        mMyAddressesTv = mNavigationView.findViewById(R.id.sideMenu_addressesTv);
        mMyAddressesTv.setTypeface(mRobotoRegular);
        mMyAddressesLayout.setOnClickListener(this);

        mMyProfileLayout = mNavigationView.findViewById(R.id.sideMenu_profileLayout);
        mMyProfileTv = mNavigationView.findViewById(R.id.sideMenu_profileTv);
        mMyProfileTv.setTypeface(mRobotoRegular);
        mMyProfileLayout.setOnClickListener(this);

        mShareAppLayout = mNavigationView.findViewById(R.id.sideMenu_shareAppLayout);
        mShareAppTv = mNavigationView.findViewById(R.id.sideMenu_shareAppTv);
        mShareAppTv.setTypeface(mRobotoRegular);
        mShareAppLayout.setOnClickListener(this);

        mSignOutBtn = mNavigationView.findViewById(R.id.sideMenu_signOutBtn);
        mSignOutBtn.setTypeface(mMontserratRegular);
        mSignOutBtn.setOnClickListener(this);

        mFacebookIv = mNavigationView.findViewById(R.id.sideMenu_facebookIv);
        mFacebookIv.setOnClickListener(this);

        mTwitterIv = mNavigationView.findViewById(R.id.sideMenu_twitterIv);
        mTwitterIv.setOnClickListener(this);

        mInstagramIv = mNavigationView.findViewById(R.id.sideMenu_instagramIv);
        mInstagramIv.setOnClickListener(this);

        mSnapChatIv = mNavigationView.findViewById(R.id.sideMenu_snapIv);
        mSnapChatIv.setOnClickListener(this);

        mYoutubeIv = mNavigationView.findViewById(R.id.sideMenu_youtubeIv);
        mYoutubeIv.setOnClickListener(this);

    }

    private void setupHomeView() {

        mSpinKitLayout = findViewById(R.id.home_spinKit_layout);

        mMenuLayout = findViewById(R.id.home_activity_menuLayout);
        mMenuIv = findViewById(R.id.home_activity_menuIv);
        mMenuLayout.setOnClickListener(this);

        mThreeDotsLayout = findViewById(R.id.home_activity_langLayout);
        mThreeDotsLayout.setOnClickListener(this);

        mChooseAddressLayout = findViewById(R.id.home_activity_chooseAddressLayout);
        mChooseAddressTitle = findViewById(R.id.home_activity_chooseAddressTitle);
        mChooseAddressTitle.setTypeface(mMontserratRegular);
        mChooseAddressArrowIv = findViewById(R.id.home_activity_arrowIv);
        mChooseAddressLayout.setOnClickListener(this);

        mMyOrdersBtn = findViewById(R.id.home_activity_myOrdersBtn);
        mMyOrdersBtn.setTypeface(mMontserratRegular);
        mMyOrdersBtn.setOnClickListener(this);

        Utils.submitFlip(mMenuIv, mPrefsManger);

        if (mPrefsManger.getAppLanguage().contains("ar"))
            mChooseAddressArrowIv.setImageResource(R.drawable.ic_arrow_left);
        else
            mChooseAddressArrowIv.setImageResource(R.drawable.ic_arrow_right);

        mCurrentTag = HOME_TAG;
        getGovernorates();
    }

    private void getGovernorates() {
        if (Utils.isConnectionOn(this)) {
            mSpinKitLayout.setVisibility(View.VISIBLE);

            Call<List<Governorate>> call = mServiceApi.getGovernorates(mAuthToken);
            call.enqueue(new Callback<List<Governorate>>() {
                @Override
                public void onResponse(Call<List<Governorate>> call, Response<List<Governorate>> response) {
                    mSpinKitLayout.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            mGovernorateList = response.body();
                            getCategories();
                        } catch (Exception e) {
                            showDcDialog(ERROR_GET_GOVERNORATE);
                            Log.e("Governorate Ex / ", e.getMessage() + "");
                        }
                    } else {
                        Log.e("Governorate Code / ", "Code not Successful");
                        showDcDialog(ERROR_GET_GOVERNORATE);
                    }
                }

                @Override
                public void onFailure(Call<List<Governorate>> call, Throwable t) {
                    mSpinKitLayout.setVisibility(View.GONE);
                    Log.e("Governorate Fail / ", t.getMessage() + "");
                    showDcDialog(ERROR_GET_GOVERNORATE);
                }
            });
        } else
            Utils.makeAToast(MainActivity.this, getResources().getString(R.string.connection_offline));
    }

    private void getCategories() {
        mSpinKitLayout.setVisibility(View.VISIBLE);

        Call<List<ProductCategory>> call = mServiceApi.getCategories(mAuthToken);
        call.enqueue(new Callback<List<ProductCategory>>() {
            @Override
            public void onResponse(Call<List<ProductCategory>> call, Response<List<ProductCategory>> response) {
                mSpinKitLayout.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    try {
                        mCategoryList = response.body();
                        getProducts();
                    } catch (Exception e) {
                        showDcDialog(ERROR_GET_CATEGORY);
                        Log.e("Categories Ex / ", e.getMessage() + "");
                    }
                } else {
                    Log.e("Categories Code / ", "Code not Successful");
                    showDcDialog(ERROR_GET_CATEGORY);
                }
            }

            @Override
            public void onFailure(Call<List<ProductCategory>> call, Throwable t) {
                mSpinKitLayout.setVisibility(View.GONE);
                Log.e("Categories Fail / ", t.getMessage() + "");
                showDcDialog(ERROR_GET_CATEGORY);
            }
        });
    }

    private void getProducts() {
        mSpinKitLayout.setVisibility(View.VISIBLE);

        Call<List<Product>> call = mServiceApi.getProducts(mAuthToken);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                mSpinKitLayout.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    try {
                        mProductsList = response.body();
                        getSocialLinks();
                    } catch (Exception e) {
                        showDcDialog(ERROR_GET_PRODUCT);
                        Log.e("Products Ex / ", e.getMessage() + "");
                    }
                } else {
                    Log.e("Products Code / ", "Code not Successful");
                    showDcDialog(ERROR_GET_PRODUCT);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                mSpinKitLayout.setVisibility(View.GONE);
                Log.e("Products Fail / ", t.getMessage() + "");
                showDcDialog(ERROR_GET_PRODUCT);
            }
        });
    }

//    private void getMinOrder() {
//        mSpinKitLayout.setVisibility(View.VISIBLE);
//
//        Call<MinOrder> call = mServiceApi.getMinOrderValue(mAuthToken);
//        call.enqueue(new Callback<MinOrder>() {
//            @Override
//            public void onResponse(Call<MinOrder> call, Response<MinOrder> response) {
//                mSpinKitLayout.setVisibility(View.GONE);
//                if (response.isSuccessful()) {
//                    try {
//                        MinOrder minOrder = response.body();
//                        mMinOrder = minOrder.getAmount();
//                        getUserData();
//                    } catch (Exception e) {
//                        showDcDialog(ERROR_GET_MIN_ORDER);
//                        Log.e("MinOrder Ex / ", e.getMessage() + "");
//                    }
//                } else {
//                    Log.e("MinOrder Code / ", "Code not Successful");
//                    showDcDialog(ERROR_GET_MIN_ORDER);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MinOrder> call, Throwable t) {
//                mSpinKitLayout.setVisibility(View.GONE);
//                Log.e("MinOrder Fail / ", t.getMessage() + "");
//                showDcDialog(ERROR_GET_MIN_ORDER);
//            }
//        });
//    }

    private void getSocialLinks() {
        mSpinKitLayout.setVisibility(View.VISIBLE);

        Call<List<SocialLink>> call = mServiceApi.getSocialLinks(mAuthToken);
        call.enqueue(new Callback<List<SocialLink>>() {
            @Override
            public void onResponse(Call<List<SocialLink>> call, Response<List<SocialLink>> response) {
                mSpinKitLayout.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    try {
                        List<SocialLink> links = response.body();
                        assert links != null;
                        for (int l = 0; l < links.size(); l++) {
                            SocialLink link = links.get(l);
                            if (link.getCode() != null) {
                                switch (link.getCode()) {
                                    case "facebook":
                                        mFacebookLink = link.getUrl();
                                        break;
                                    case "twitter":
                                        mTwitterLink = link.getUrl();
                                        break;
                                    case "instagram":
                                        mInstagramLink = link.getUrl();
                                        break;
                                    case "snapchat-ghost":
                                        mSnapChatLink = link.getUrl();
                                        break;
                                    case "youtube":
                                        mYoutubeLink = link.getUrl();
                                        break;
                                }
                            }
                        }
                        getUserData();
                    } catch (Exception e) {
                        showDcDialog(ERROR_GET_SOCIAL_LINKS);
                        Log.e("SocialLinks Ex / ", e.getMessage() + "");
                    }
                } else {
                    Log.e("SocialLinks Code / ", "Code not Successful");
                    showDcDialog(ERROR_GET_SOCIAL_LINKS);
                }
            }

            @Override
            public void onFailure(Call<List<SocialLink>> call, Throwable t) {
                mSpinKitLayout.setVisibility(View.GONE);
                Log.e("SocialLinks Fail / ", t.getMessage() + "");
                showDcDialog(ERROR_GET_SOCIAL_LINKS);
            }
        });
    }

    private void getUserData() {
        mSpinKitLayout.setVisibility(View.VISIBLE);

        Call<UserData> call = mServiceApi.getUserData(mAuthToken, mUserId);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                mSpinKitLayout.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    try {
                        UserData userData = response.body();
                        mPrefsManger.saveUserData(userData);

                        if (mUserData.getBasketId() == null)
                            createNewBasket();

                    } catch (Exception e) {
                        showDcDialog(ERROR_GET_USER_DATA);
                        Log.e("UserData Ex / ", e.getMessage() + "");
                    }
                } else {
                    Log.e("UserData Code / ", "Code not Successful");
                    showDcDialog(ERROR_GET_USER_DATA);
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                mSpinKitLayout.setVisibility(View.GONE);
                Log.e("UserData Fail / ", t.getMessage() + "");
                showDcDialog(ERROR_GET_USER_DATA);
            }
        });
    }

    private void createNewBasket() {
        mSpinKitLayout.setVisibility(View.VISIBLE);

        Basket basket = new Basket();
        basket.setOwner(mUserId);

        Call<Basket> call = mServiceApi.createNewBasket(mAuthToken, basket);
        call.enqueue(new Callback<Basket>() {
            @Override
            public void onResponse(Call<Basket> call, Response<Basket> response) {
                mSpinKitLayout.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    try {
                        Basket basket = response.body();
                        mUserData.setBasketId(basket.getId());
                        mPrefsManger.saveUserData(mUserData);
                    } catch (Exception e) {
                        showDcDialog(ERROR_GET_BASKET);
                        Log.e("CBasket Ex / ", e.getMessage() + "");
                    }
                } else {
                    Log.e("CBasket Code / ", "Code not Successful");
                    showDcDialog(ERROR_GET_BASKET);
                }
            }

            @Override
            public void onFailure(Call<Basket> call, Throwable t) {
                mSpinKitLayout.setVisibility(View.GONE);
                Log.e("CBasket Fail / ", t.getMessage() + "");
                showDcDialog(ERROR_GET_BASKET);
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.sideMenu_homeLayout:
                if (!mCurrentTag.equals(HOME_TAG)) {
                    onCustomBack();
                    mCurrentTag = HOME_TAG;
                }
                closeDrawer();
                break;

            case R.id.home_activity_myOrdersBtn:
            case R.id.sideMenu_ordersLayout:
                if (!mCurrentTag.equals(ORDERS_FRAGMENT_TAG))
                    replaceFragment(new MyOrdersFragment(), ORDERS_FRAGMENT_TAG, true);
                closeDrawer();
                break;

            case R.id.sideMenu_offersLayout:
                if (!mCurrentTag.equals(OFFERS_FRAGMENT_TAG))
                    replaceFragment(new OffersFragment(), OFFERS_FRAGMENT_TAG, true);
                closeDrawer();
                break;

            case R.id.sideMenu_notificationsLayout:
                closeDrawer();
                if (!mCurrentTag.equals(NOTIFICATIONS_FRAGMENT_TAG))
                    replaceFragment(new NotificationsFragment(), NOTIFICATIONS_FRAGMENT_TAG, true);
                break;

            case R.id.sideMenu_contactUsLayout:
                if (!mCurrentTag.equals(CONTACT_FRAGMENT_TAG))
                    replaceFragment(new ContactUsFragment(), CONTACT_FRAGMENT_TAG, true);
                closeDrawer();
                break;

            case R.id.home_activity_chooseAddressLayout:
            case R.id.sideMenu_addressesLayout:
                if (!mCurrentTag.equals(ADDRESSES_FRAGMENT_TAG))
                    replaceFragment(new AddressesFragment(), ADDRESSES_FRAGMENT_TAG, true);
                closeDrawer();
                break;

            case R.id.sideMenu_profileLayout:
                if (!mCurrentTag.equals(PROFILE_FRAGMENT_TAG))
                    replaceFragment(new MyProfileFragment(), PROFILE_FRAGMENT_TAG, true);
                closeDrawer();
                break;

            case R.id.sideMenu_shareAppLayout:
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                    String link = "http://play.google.com/store/apps/details?id=" + getApplication().getPackageName();
                    String sAux = getResources().getString(R.string.share_txt).concat(" ").concat(link);
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, getResources().getString(R.string.share_title)));
                } catch (Exception e) {
                    Log.e("Share Error", String.valueOf(e));
                }
                closeDrawer();
                break;

            case R.id.sideMenu_signOutBtn:
                closeDrawer();
                signOut();
                break;

            case R.id.sideMenu_facebookIv:
                if (mFacebookLink != null)
                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(mFacebookLink)));
                closeDrawer();
                break;

            case R.id.sideMenu_twitterIv:
                if (mTwitterLink != null)
                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(mTwitterLink)));
                closeDrawer();
                break;

            case R.id.sideMenu_instagramIv:
                if (mInstagramLink != null)
                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(mInstagramLink)));
                closeDrawer();
                break;

            case R.id.sideMenu_snapIv:
                if (mSnapChatLink != null)
                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(mSnapChatLink)));
                closeDrawer();
                break;

            case R.id.sideMenu_youtubeIv:
                if (mYoutubeLink != null)
                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(mYoutubeLink)));
                closeDrawer();
                break;

            case R.id.home_activity_menuLayout:
                openDrawer();
                break;

            case R.id.home_activity_langLayout:
                setupLanguage();
                break;
        }
    }

    private void setupLanguage() {
        PopupMenu menu = new PopupMenu(MainActivity.this, mThreeDotsLayout);
        menu.getMenuInflater().inflate(R.menu.lang, menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String lang = null;
                switch (item.getItemId()) {
                    case R.id.arLang:
                        lang = "ar";
                        if (!mPrefsManger.getAppLanguage().equals(lang)) {
                            mPrefsManger.setAppLanguage(lang);
                            restartActivity();
                        }
                        return true;
                    case R.id.enLang:
                        lang = "en";
                        if (!mPrefsManger.getAppLanguage().equals(lang)) {
                            mPrefsManger.setAppLanguage(lang);
                            restartActivity();
                        }
                        return true;
                }
                return false;
            }
        });
        menu.show();
    }

    public void replaceFragment(Fragment fragment, String tag, boolean menu) {
        if (!mCurrentTag.equals(HOME_TAG)) {
            try {
                removeFragment(getCurrentFragment());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mCurrentTag = tag;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.home_activity_container_layout, fragment, tag).commit();

        if (menu)
            if (mDrawerLayout.isDrawerOpen(mDrawerGravity))
                closeDrawer();
    }

    public void removeFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove(fragment);
        trans.commit();
        manager.popBackStack();
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(mDrawerGravity);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mDrawerGravity);
    }

    @Override
    public void onBackPressed() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (mDrawerLayout.isDrawerOpen(mDrawerGravity)) {
            mDrawerLayout.closeDrawers();
        } else {
            onCustomBack();
        }
    }

    private void onCustomBack() {
        OnBackPressListener mCurrentFragment = (OnBackPressListener) getSupportFragmentManager().findFragmentByTag(mCurrentTag);

        if (mCurrentFragment != null) {
            if (!mCurrentFragment.onBackPressed())
                super.onBackPressed();
        } else
            super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PRODUCT_DETAIL:
                AddressesFragment addressesFragment = (AddressesFragment) getCurrentFragment();
                MenuFragment menuFragment = (MenuFragment) addressesFragment.getChildFragmentManager().getFragments().get(0);
                menuFragment.onActivityResult(requestCode, resultCode, data);
                break;

        }
    }

    private void restartActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void signOut() {
        mPrefsManger.clearUserData();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    public void showDcDialog(final int type) {
        DcDialog dcDialog = new DcDialog(this);
        dcDialog.setOnClickRetryBtnListener(new OnClickRetryBtn() {
            @Override
            public void retry() {
                switch (type) {
                    case ERROR_GET_GOVERNORATE:
                        getGovernorates();
                        break;
                    case ERROR_GET_CATEGORY:
                        getCategories();
                        break;
                    case ERROR_GET_PRODUCT:
                        getProducts();
                        break;
//                    case ERROR_GET_MIN_ORDER:
//                        getMinOrder();
//                        break;
                    case ERROR_GET_SOCIAL_LINKS:
                        getSocialLinks();
                        break;
                    case ERROR_GET_USER_DATA:
                        getUserData();
                        break;
                    case ERROR_GET_BASKET:
                        createNewBasket();
                        break;
                }
            }
        });
    }

    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentByTag(mCurrentTag);
    }

    @Override
    protected void onStart() {
        try {
            super.onStart();
            LocalBroadcastManager.getInstance(this).registerReceiver((mBroadcastReceiver), new IntentFilter(REQUEST_ACCEPT));

            if (getIntent().hasExtra(KEY_OPEN_ORDERS)) {
                boolean openOrders = getIntent().getExtras().getBoolean(KEY_OPEN_ORDERS, false);
                if (openOrders)
                    replaceFragment(new MyOrdersFragment(), ORDERS_FRAGMENT_TAG, false);
            }
//            else if (getIntent().hasExtra(KEY_PAYMENT_URL)) {
//                String paymentUrl = getIntent().getExtras().getString(KEY_PAYMENT_URL, null);
//                Intent intent = new Intent(this, PaymentActivity.class);
//                intent.putExtra(KEY_PAYMENT_URL, paymentUrl);
//                startActivity(intent);
//            }
            else if (getIntent().hasExtra(KEY_OPEN_NOTIFICATIONS)) {
                boolean showNotifications = getIntent().getExtras().getBoolean(KEY_OPEN_NOTIFICATIONS, false);
                if (showNotifications)
                    replaceFragment(new NotificationsFragment(), NOTIFICATIONS_FRAGMENT_TAG, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUnSeenNotification();
        if (mCurrentTag.equals(ADDRESSES_FRAGMENT_TAG)) {
            AddressesFragment addressesFragment = (AddressesFragment) getCurrentFragment();
            List<Fragment> fragments = addressesFragment.getChildFragmentManager().getFragments();
            if (!fragments.isEmpty()) {
                Fragment fragment = fragments.get(0);
                if (fragment instanceof MenuFragment) {
                    MenuFragment menuFragment = (MenuFragment) fragment;
                    menuFragment.updateCartCounter();
                }
            }
        }
    }

    @Override
    protected void onStop() {
        try {
            super.onStop();
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void checkUnSeenNotification() {
        if (mPrefsManger.getUnseenNotificationCount() == 0)
            mNotificationUnSeenCountTv.setVisibility(View.GONE);
        else {
            mNotificationUnSeenCountTv.setText(String.valueOf(mPrefsManger.getUnseenNotificationCount()));
            mNotificationUnSeenCountTv.setVisibility(View.VISIBLE);
        }
    }
}
