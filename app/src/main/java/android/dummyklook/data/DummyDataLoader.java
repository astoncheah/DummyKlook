package android.dummyklook.data;

import android.content.Context;
import android.dummyklook.ui.BlankFragment;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

/**
 * Helper for loading a list of articles or a single article.
 */
public class DummyDataLoader extends CursorLoader{
    public static DummyDataLoader newAll(Context context) {
        return new DummyDataLoader(context, ItemsContract.Items.buildDirUri());
    }
    public static CursorLoader getCursorLoader(Context context,int sortType) {
        switch(sortType){
            default:
                return sortPopular(context);
            case BlankFragment.SORT_TOP:
                return sortTopRate(context);
            case BlankFragment.SORT_NAME:
                return sortName(context);
            case BlankFragment.SORT_DATE:
                return sortDate(context);
            case BlankFragment.SORT_OUR_PICK:
                return sortRandom(context);
        }
    }
    private static CursorLoader sortPopular(Context context) {
        CursorLoader cursorLoader = new CursorLoader(
            context,
            ItemsContract.Items.buildDirUri(),
            ItemsContract.Query.PROJECTION,
            null,
            null,
            ItemsContract.Items.POPULARITY+" DESC");
        return cursorLoader;
    }
    private static CursorLoader sortTopRate(Context context) {
        CursorLoader cursorLoader = new CursorLoader(
            context,
            ItemsContract.Items.buildDirUri(),
            ItemsContract.Query.PROJECTION,
            null,
            null,
            ItemsContract.Items.VOTE_AVERAGE+" DESC");
        return cursorLoader;
    }
    private static CursorLoader sortName(Context context) {
        CursorLoader cursorLoader = new CursorLoader(
            context,
            ItemsContract.Items.buildDirUri(),
            ItemsContract.Query.PROJECTION,
            null,
            null,
            ItemsContract.Items.TITLE+" ASC");
        return cursorLoader;
    }
    private static CursorLoader sortDate(Context context) {
        CursorLoader cursorLoader = new CursorLoader(
            context,
            ItemsContract.Items.buildDirUri(),
            ItemsContract.Query.PROJECTION,
            null,
            null,
            ItemsContract.Items.RELEASE_DATE+" DESC");
        return cursorLoader;
    }
    private static CursorLoader sortRandom(Context context) {
        CursorLoader cursorLoader = new CursorLoader(
            context,
            ItemsContract.Items.buildDirUri(),
            ItemsContract.Query.PROJECTION,
            null,
            null,
            ItemsContract.Items.SAVE_AS_FAVORITE+" DESC");
        return cursorLoader;
    }
    private DummyDataLoader(Context context, Uri uri) {
        super(context, uri, ItemsContract.Query.PROJECTION, null, null, ItemsContract.Items.DEFAULT_SORT);
    }
}
