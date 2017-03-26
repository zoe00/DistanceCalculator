package taxfix.distance.calculator.data.retrofit;

import retrofit.RestAdapter;

public class RetrofitService {

    public static <T> T createRetrofitService(final Class<T> clazz, final String endPoint) {
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(endPoint)
                .build();
        return restAdapter.create(clazz);
    }
}
