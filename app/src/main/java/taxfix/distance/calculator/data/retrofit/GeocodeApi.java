package taxfix.distance.calculator.data.retrofit;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;
import taxfix.distance.calculator.domain.GeocodeResponse;

public interface GeocodeApi {

    String MAP_SERVICE_ENDPOINT = "http://maps.googleapis.com/maps/api";

    @GET("/geocode/json")
    Observable<GeocodeResponse> getGeoCodedData(@Query("address") String address);
}
