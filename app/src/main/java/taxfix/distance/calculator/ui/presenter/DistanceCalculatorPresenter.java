package taxfix.distance.calculator.ui.presenter;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import taxfix.distance.calculator.data.retrofit.GeocodeApi;
import taxfix.distance.calculator.data.retrofit.RetrofitService;
import taxfix.distance.calculator.ui.view.DistanceCalculatorUtil;
import taxfix.distance.calculator.domain.GeocodeResponse;
import taxfix.distance.calculator.domain.Location;
import taxfix.distance.calculator.ui.view.DistanceCalculatorView;

import static taxfix.distance.calculator.data.retrofit.GeocodeApi.MAP_SERVICE_ENDPOINT;

public class DistanceCalculatorPresenter {

    private DistanceCalculatorView view;
    private Location homeAddress;
    private Location workAddress;

    public void onAttachView(@NonNull DistanceCalculatorView view) {
        this.view = view;
        view.showMessage("After typing the address, press keyboard enter key to continue...");
    }

    public void onDetachView() {
        this.view = null;
    }

    public void calculateDistance() {
        if (homeAddress == null || workAddress == null)
            return;
        double distanceInKilometers;
        distanceInKilometers = DistanceCalculatorUtil.calculateDistance(
                getHomePosition(),
                getWorkPosition())
                / 1000;
        view.hideKeyboard();
        view.showDistance(String.format("%.2f", distanceInKilometers) + " km");
    }

    private void initGeoCoding(final String address, final boolean isHomeAddress) {
        GeocodeApi geocodeApi = RetrofitService.createRetrofitService(GeocodeApi.class, MAP_SERVICE_ENDPOINT);
        geocodeApi.getGeoCodedData(address)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GeocodeResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public final void onError(Throwable e) {
                        view.hideProgress();
                        // error handling can be better
                        view.showMessage("Oops... Something went wrong. Please check your internet connection and try again.");
                    }

                    @Override
                    public final void onNext(GeocodeResponse response) {
                        view.hideProgress();
                        if (response.status.equals("OK")) {
                            onAddressFetchSuccess(address, response, isHomeAddress);
                        } else {
                            onAddressError(isHomeAddress);
                        }
                    }
                });
    }

    private void onAddressError(boolean isHomeAddress) {
        if (isHomeAddress) {
            view.showMessage("Home address is invalid.");
        } else {
            view.showMessage("Work address is invalid.");
        }
    }

    private void onAddressFetchSuccess(String address, GeocodeResponse response, boolean isHomeAddress) {
        Location location = response.results[0].geometry.location;
        view.addPin(location);
        // sending this to view to avoid Android context usage in presenter
        view.saveLocation(address);
        if (isHomeAddress) {
            homeAddress = location;
            view.showWorkAddressField();
        } else {
            workAddress = location;
            view.showCalculateButton();
        }
    }

    private boolean isValid(String address) {
        return address != null && !address.isEmpty();
    }

    private LatLng getHomePosition() {
        return new LatLng(Double.parseDouble(homeAddress.lat),
                Double.parseDouble(homeAddress.lng));
    }

    private LatLng getWorkPosition() {
        return new LatLng(Double.parseDouble(workAddress.lat),
                Double.parseDouble(workAddress.lng));
    }

    public void onHomeAddressEntered(String address) {
        if (!isValid(address)) {
            view.showMessage("Please enter a valid home address");
            return;
        }
        view.showProgress();
        initGeoCoding(address, true);
    }

    public void onWorkAddressEntered(String address) {
        if (!isValid(address)) {
            view.showMessage("Please enter a valid work address");
            return;
        }
        view.showProgress();
        initGeoCoding(address, false);
    }

    public void onKeyboardBackKeyPressed() {
        view.hideKeyboard();
        view.showMessage("Please use start over button from top right corner to start a new calculation...");
    }
}


