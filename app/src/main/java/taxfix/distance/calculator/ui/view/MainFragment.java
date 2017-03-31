package taxfix.distance.calculator.ui.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import taxfix.distance.calculator.R;
import taxfix.distance.calculator.data.LocationHistoryDataSource;
import taxfix.distance.calculator.domain.Location;
import taxfix.distance.calculator.ui.presenter.DistanceCalculatorPresenter;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MainFragment extends Fragment implements OnMapReadyCallback, DistanceCalculatorView {

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.mapView)
    MapView mapView;

    @BindView(R.id.homeAddressEditText)
    AutoCompleteTextView homeAddress;

    @BindView(R.id.workAddressEditText)
    AutoCompleteTextView workAddress;

    @BindView(R.id.calculateButton)
    Button calculateButton;

    @BindView(R.id.distanceTextView)
    TextView distanceTextView;

    private static final float ZOOM_LEVEL = 10.5f;
    private static final float TILT_VALUE = 25;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private GoogleMap map;
    private DistanceCalculatorPresenter presenter;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Intent intent = new PlaceAutocomplete.IntentBuilder(
                        PlaceAutocomplete.MODE_FULLSCREEN)
                        .build(getActivity());
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (GooglePlayServicesRepairableException e) {
            } catch (GooglePlayServicesNotAvailableException e) {
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        ButterKnife.bind(this, view);
        initView(savedInstanceState);
        setupLocationAutoComplete();
        hideKeyboard();
        return view;
    }

    private void setupLocationAutoComplete() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (getActivity(),
                        android.R.layout.simple_list_item_1,
                        LocationHistoryDataSource.getHistory(getActivity()));
        homeAddress.setAdapter(adapter);
        workAddress.setAdapter(adapter);
    }

    private void initView(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        presenter = new DistanceCalculatorPresenter();
//        homeAddress.setOnKeyListener(keyListener);
//        workAddress.setOnKeyListener(keyListener);
        homeAddress.setOnClickListener(clickListener);
        workAddress.setOnClickListener(clickListener);
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.onAttachView(this);
    }

    @Override
    public void onDestroyView() {
        presenter.onDetachView();
        super.onDestroyView();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(GONE);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void addPin(Location location) {
        LatLng latLng = new LatLng(Double.parseDouble(location.lat),
                Double.parseDouble(location.lng));
        final CameraPosition cameraPosition =
                new CameraPosition.Builder().target(latLng)
                        .zoom(ZOOM_LEVEL)
                        .tilt(TILT_VALUE)
                        .build();
        map.addMarker(new MarkerOptions().position(latLng));
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void showWorkAddressField() {
        workAddress.setVisibility(VISIBLE);
    }

    @Override
    public void showCalculateButton() {
        calculateButton.setVisibility(VISIBLE);
    }

    @Override
    public void showDistance(String distance) {
        distanceTextView.setText(distance);
        distanceTextView.setVisibility(VISIBLE);
    }

    @Override
    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(workAddress.getWindowToken(), 0);
    }

    @Override
    public void saveLocation(String location) {
        LocationHistoryDataSource.saveLocation(getActivity(), location);
    }

    @OnClick(R.id.calculateButton)
    void calculateDistance() {
        presenter.calculateDistance();
    }

    @OnClick(R.id.restartFab)
    void startOver() {
        workAddress.setText("");
        homeAddress.setText("");
        workAddress.setVisibility(INVISIBLE);
        calculateButton.setVisibility(INVISIBLE);
        distanceTextView.setVisibility(INVISIBLE);
        // reset to get history
        setupLocationAutoComplete();
        map.clear();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                if(homeAddress.hasFocus()){
                    homeAddress.setText(place.getName());
                    presenter.onHomeAddressEntered(homeAddress.getText().toString());
                }else{
                    workAddress.setText(place.getName());
                    presenter.onWorkAddressEntered(workAddress.getText().toString());
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                Log.e("status", status+"");
            } else if (resultCode == RESULT_CANCELED) {
                Log.e("map", "user cancelled result");
            }
        }
    }

    @OnClick(R.id.mapView)
    void onMapClick() {
        hideKeyboard();
    }
}

