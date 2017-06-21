package android.dummyklook.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.dummyklook.R;
import android.dummyklook.data.DummyDataLoader;
import android.dummyklook.data.ItemsContract;
import android.dummyklook.remote.Config;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Date;

public class BlankFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int SORT_POPULAR    = 1;
    public static final int SORT_TOP        = 2;
    public static final int SORT_NAME       = 3;
    public static final int SORT_DATE       = 4;
    public static final int SORT_OUR_PICK   = 5;

    private static final String SORT_TYPE = "SORT_TYPE";
    private static final String POSITION = "POSITION";

    private Activity context;
    private Cursor mCursor;
    private int sortType;
    private int position;

    private View mRootView;
    private ImageView thumbnailView;
    private TextView title;
    private TextView releaseDate;
    private TextView rate;

    public BlankFragment(){}
    public static BlankFragment newInstance(int sortType,int position){
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putInt(SORT_TYPE,sortType);
        args.putInt(POSITION,position);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        context = getActivity();
        if(getArguments()!=null){
            sortType = getArguments().getInt(SORT_TYPE,SORT_POPULAR);
            position = getArguments().getInt(POSITION,0);
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        mRootView = inflater.inflate(R.layout.list_card_item, container, false);

        thumbnailView = (ImageView) mRootView.findViewById(R.id.thumbnail);
        title = (TextView) mRootView.findViewById(R.id.article_title);
        releaseDate = (TextView) mRootView.findViewById(R.id.releaseDate);
        rate = (TextView) mRootView.findViewById(R.id.rate);

        bindViews();
        return mRootView;
    }
    @Override
    public void onDetach(){
        super.onDetach();
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id,Bundle args){
        return DummyDataLoader.getCursorLoader(context,sortType);
    }
    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader,Cursor data){
        mCursor = data;
        if (mCursor != null && !mCursor.moveToFirst()) {
            mCursor.close();
            mCursor = null;
        }
        bindViews();
    }
    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader){
        mCursor = null;
        bindViews();
    }
    private void bindViews() {
        if (mRootView == null) {
            return;
        }
        if (mCursor != null) {
            mCursor.moveToPosition(position);
            mRootView.setVisibility(View.VISIBLE);

            long longDate = Long.valueOf(mCursor.getString(ItemsContract.Query.RELEASE_DATE));
            Date date = new Date(longDate);

            title.setText(mCursor.getString(ItemsContract.Query.TITLE));
            releaseDate.setText(date.toString());
            rate.setText(mCursor.getString(ItemsContract.Query.VOTE_AVERAGE)+"/10");

            final String IMAGE_URL = Config.BASE_IMAGE_URL+mCursor.getString(ItemsContract.Query.POSTER_URL);
            Picasso.with(context).load(IMAGE_URL).into(thumbnailView);
        } else {
            mRootView.setVisibility(View.GONE);
            title.setText("N/A" );
            releaseDate.setText("N/A" );
            rate.setText("N/A");
        }
    }
}
