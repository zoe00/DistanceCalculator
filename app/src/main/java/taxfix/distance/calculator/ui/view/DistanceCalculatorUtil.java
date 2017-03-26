package taxfix.distance.calculator.ui.view;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

public class DistanceCalculatorUtil {

    /**
     *
     * @param from starting point
     * @param to ending point
     * @return distance in meters
     */
    public static double calculateDistance(@NonNull LatLng from, @NonNull LatLng to){
        return SphericalUtil.computeDistanceBetween(from, to);
    }

}
