package android.dummyklook.remote;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.dummyklook.R;
import android.dummyklook.data.ItemsContract;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.RemoteException;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class UpdaterService extends IntentService{
    private static final String TAG = "UpdaterService";

    public static final String BROADCAST_ACTION_STATE_CHANGE
            = "com.example.popular_movies.intent.action.STATE_CHANGE";
    public static final String EXTRA_REFRESHING
            = "com.example.popular_movies.intent.extra.REFRESHING";

    public UpdaterService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            Toast.makeText(this,R.string.no_connection,Toast.LENGTH_LONG).show();
            sendBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
            return;
        }

        sendBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, true));

        URL url1 = Config.POPULAR_URL;
        URL url2 = Config.TOP_RATED_URL;

        ArrayList<ContentProviderOperation> cpo = new ArrayList<>();
        Uri dirUri = ItemsContract.Items.buildDirUri();
        Cursor cursor = getContentResolver().query(dirUri,ItemsContract.Query.PROJECTION,null,null,null);
        // Delete all items
        cpo.add(ContentProviderOperation.newDelete(dirUri).build());

        try {
            addData(url1,cursor,dirUri,cpo);
            addData(url2,cursor,dirUri,cpo);
            getContentResolver().applyBatch(ItemsContract.CONTENT_AUTHORITY, cpo);
        }catch(RemoteException e){
            e.printStackTrace();
        }catch(OperationApplicationException e){
            e.printStackTrace();
        }finally{
            cursor.close();
        }
        sendBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
    }
    private void addData(URL url,Cursor cursor,Uri dirUri,ArrayList<ContentProviderOperation> cpo){
        Time time = new Time();
        try {
            JSONArray array = RemoteEndpointUtil.fetchJsonArray(url);
            if (array == null) {
                throw new JSONException("Invalid parsed item array" );
            }
            for (int i = 0; i < array.length(); i++) {
                ContentValues values = new ContentValues();
                JSONObject object = array.getJSONObject(i);
                values.put(ItemsContract.Items._ID, object.getString(ItemsContract.Items._ID));
                values.put(ItemsContract.Items.TITLE, object.getString(ItemsContract.Items.TITLE));
                values.put(ItemsContract.Items.VOTE_AVERAGE, object.getString(ItemsContract.Items.VOTE_AVERAGE));
                values.put(ItemsContract.Items.POPULARITY, object.getString(ItemsContract.Items.POPULARITY));
                time.parse3339(object.getString(ItemsContract.Items.RELEASE_DATE));
                values.put(ItemsContract.Items.RELEASE_DATE, time.toMillis(false));
                values.put(ItemsContract.Items.SAVE_AS_FAVORITE,(i%5));
                values.put(ItemsContract.Items.OVERVIEW, object.getString(ItemsContract.Items.OVERVIEW));
                values.put(ItemsContract.Items.POSTER_URL, object.getString(ItemsContract.Items.POSTER_URL));
                values.put(ItemsContract.Items.PHOTO_URL, object.getString(ItemsContract.Items.PHOTO_URL));
                cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
