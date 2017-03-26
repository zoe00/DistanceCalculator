package taxfix.distance.calculator.ui.view;

import taxfix.distance.calculator.domain.Location;

public interface DistanceCalculatorView {

    void showProgress();

    void hideProgress();

    void showMessage(String message);

    void addPin(Location location);

    void showWorkAddressField();

    void showCalculateButton();

    void showDistance(String distance);

    void hideKeyboard();

    void saveLocation(String location);
}
