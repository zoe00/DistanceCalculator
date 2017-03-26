package taxfix.distance.calculator.ui.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class MapsActivity extends FragmentActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        Fragment profileFragment = new MainFragment();
        fragmentTransaction
                .replace(android.R.id.content, profileFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
