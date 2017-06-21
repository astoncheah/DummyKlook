package android.dummyklook.data;

import android.net.Uri;

public class ItemsContract {
	public static final String CONTENT_AUTHORITY = "android.dummyklook";
	public static final Uri BASE_URI = Uri.parse("content://android.dummyklook");

	interface ItemsColumns {
		/** Type: INTEGER PRIMARY KEY AUTOINCREMENT */
		String _ID = "id";
		String TITLE = "title";
        String VOTE_AVERAGE = "vote_average";
		String POPULARITY = "popularity";
		String RELEASE_DATE = "release_date";
		String SAVE_AS_FAVORITE = "save_as_favorite";
		String OVERVIEW = "overview";
		String POSTER_URL = "poster_path";
		String PHOTO_URL = "backdrop_path";
	}

	public static class Items implements ItemsColumns {
		public static final String CONTENT_TYPE 				= "vnd.android.cursor.dir/vnd.android.dummyklook.items";
		public static final String CONTENT_ITEM_TYPE 			= "vnd.android.cursor.item/vnd.android.dummyklook.items";
        public static final String DEFAULT_SORT = RELEASE_DATE + " DESC";

		/** Matches: /items/ */
		public static Uri buildDirUri() {
			return BASE_URI.buildUpon().appendPath(ItemsProvider.Tables.ITEMS).build();
		}

		/** Matches: /items/[_id]/ */
		public static Uri buildItemUri(long _id) {
			return BASE_URI.buildUpon().appendPath(ItemsProvider.Tables.ITEMS).appendPath(Long.toString(_id)).build();
		}
        /** Read item ID item detail URI. */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(1));
        }
	}

	public interface Query {
		String[] PROJECTION = {
			ItemsContract.Items._ID,
			ItemsContract.Items.TITLE,
			ItemsContract.Items.VOTE_AVERAGE,
			ItemsContract.Items.POPULARITY,
			ItemsContract.Items.RELEASE_DATE,
			ItemsContract.Items.SAVE_AS_FAVORITE,
			ItemsContract.Items.OVERVIEW,
			ItemsContract.Items.POSTER_URL,
			ItemsContract.Items.PHOTO_URL,
		};

		int _ID             = 0;
		int TITLE           = 1;
		int VOTE_AVERAGE    = 2;
		int POPULARITY      = 3;
		int RELEASE_DATE    = 4;
		int FAVORITE        = 5;
		int OVERVIEW        = 6;
		int POSTER_URL      = 7;
		int PHOTO_URL       = 8;
	}
}
