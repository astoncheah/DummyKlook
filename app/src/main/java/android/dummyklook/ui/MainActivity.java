package android.dummyklook.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.dummyklook.R;
import android.dummyklook.data.DummyDataLoader;
import android.dummyklook.data.ItemsContract;
import android.dummyklook.remote.Config;
import android.dummyklook.remote.UpdaterService;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>  {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MyPagerAdapter adapter1, adapter2, adapter3, adapter4, adapter5;
    private Cursor cursor;

    private int scrollCount = 0;
    private boolean isActionBarShown = true;
    private KenBurnsView appBarImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportLoaderManager().initLoader(0, null, this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitleEnabled(true);
                    //.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if(isShow) {
                    //collapsingToolbarLayout.setTitle(getString(R.string.the_best_app));
                    collapsingToolbarLayout.setTitleEnabled(false);
                    isShow = false;
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                launchSearchFragment();
            }
        });

        final LinearLayout actionBottomBar = (LinearLayout)findViewById(R.id.actionBottomBar);

        NestedScrollView nestedScrollView = (NestedScrollView)findViewById(R.id.nestedScrollView);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener(){
            @Override
            public void onScrollChange(NestedScrollView v,int scrollX,int scrollY,int oldScrollX,int oldScrollY){
                if(scrollY>oldScrollY){//down
                    scrollCount++;
                    if(scrollCount>3){
                        scrollCount = 0;
                        if(isActionBarShown){
                            isActionBarShown = false;
                            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                                Slide slide = new Slide();
                                slide.setSlideEdge(Gravity.BOTTOM);

                                TransitionManager.beginDelayedTransition(actionBottomBar, slide);
                            }
                            actionBottomBar.setVisibility(View.GONE);
                        }
                    }
                }else if(scrollY<oldScrollY){//up
                    scrollCount++;
                    if(scrollCount>3){
                        scrollCount = 0;
                        if(!isActionBarShown){
                            isActionBarShown = true;
                            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                                Slide slide = new Slide();
                                slide.setSlideEdge(Gravity.BOTTOM);

                                TransitionManager.beginDelayedTransition(actionBottomBar, slide);
                            }
                            actionBottomBar.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refresh();
                }
            }
        );

        appBarImage = (KenBurnsView)findViewById(R.id.app_bar_image);
    }

    private void refresh() {
        Intent i = new Intent(this, UpdaterService.class);
        startService(i);
    }
    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mRefreshingReceiver,
            new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
    }
    private boolean mIsRefreshing = false;
    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                updateRefreshingUI();
            }
        }
    };
    private void updateRefreshingUI() {
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
    }
    private void launchSearchFragment(){
        Intent i = new Intent(this,SearchActivity.class);
        startActivity(i);
        overridePendingTransition(R.animator.slide_in_left,R.animator.slide_stay);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id,Bundle args){
        return DummyDataLoader.newAll(this);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader,Cursor data){
        cursor = data;
        cursor.moveToFirst();
        if(cursor==null || cursor.getCount()==0) {
            refresh();
        }else{
            updateViews();
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader){
    }
    private void updateViews(){
        final String IMAGE_URL = Config.BASE_IMAGE_URL+cursor.getString(ItemsContract.Query.POSTER_URL);
        Picasso.with(this).load(IMAGE_URL).into(appBarImage);
        updateImage();

        ((TextView)findViewById(R.id.appBarTitle)).setText(getString(R.string.the_best_app));

        LinearLayout mainView = (LinearLayout)findViewById(R.id.cardViewGroup);

        ViewPager view1 = (ViewPager) findViewById(R.id.viewpagerPopular);
        ViewPager view2 = (ViewPager) findViewById(R.id.viewpagerTopRate);
        ViewPager view3 = (ViewPager) findViewById(R.id.viewpagerName);
        ViewPager view4 = (ViewPager) findViewById(R.id.viewpagerDate);
        ViewPager view5 = (ViewPager) findViewById(R.id.viewpagerRandom);

        adapter1 = new MyPagerAdapter(this.getSupportFragmentManager(),BlankFragment.SORT_POPULAR);
        adapter2 = new MyPagerAdapter(this.getSupportFragmentManager(),BlankFragment.SORT_TOP);
        adapter3 = new MyPagerAdapter(this.getSupportFragmentManager(),BlankFragment.SORT_NAME);
        adapter4 = new MyPagerAdapter(this.getSupportFragmentManager(),BlankFragment.SORT_DATE);
        adapter5 = new MyPagerAdapter(this.getSupportFragmentManager(),BlankFragment.SORT_OUR_PICK);

        view1.setAdapter(adapter1);
        view2.setAdapter(adapter2);
        view3.setAdapter(adapter3);
        view4.setAdapter(adapter4);
        view5.setAdapter(adapter5);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.BOTTOM);

            TransitionManager.beginDelayedTransition(mainView, slide);
            mainView.setVisibility(View.VISIBLE);
        }
    }
    private void updateImage(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                if(cursor!=null && cursor.isClosed()){
                    if(cursor.moveToNext()){
                        final String IMAGE_URL = Config.BASE_IMAGE_URL+cursor.getString(ItemsContract.Query.POSTER_URL);
                        Picasso.with(MainActivity.this).load(IMAGE_URL).into(appBarImage);
                        updateImage();
                    }
                }
            }
        },5000);
    }
    private class MyPagerAdapter extends FragmentStatePagerAdapter{
        private int sortType;
        private MyPagerAdapter(FragmentManager fm,int sortType) {
            super(fm);
            this.sortType = sortType;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {
            return BlankFragment.newInstance(sortType,position);
        }

        @Override
        public int getCount() {
            return 5;
        }
    }
}
