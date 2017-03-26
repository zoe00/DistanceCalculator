package taxfix.distance.calculator.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class LocationHistoryDataSource {

    private static final String LOCATION_SHARED_PREFERENCE = "locations";

    public static void saveLocation(Activity context, String location) {
        if(getPreviousLocations(context).contains(location))
            return;
        SharedPreferences sharedPref = context.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        // saving as csv
        editor.putString(LOCATION_SHARED_PREFERENCE, getPreviousLocations(context) + "," + location);
        editor.apply();
    }

    private static String getPreviousLocations(Activity context) {
        SharedPreferences sharedPref = context.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(LOCATION_SHARED_PREFERENCE, "");
    }

    public static String[] getHistory(Activity activity){
        return getPreviousLocations(activity).split(",");
    }
}
