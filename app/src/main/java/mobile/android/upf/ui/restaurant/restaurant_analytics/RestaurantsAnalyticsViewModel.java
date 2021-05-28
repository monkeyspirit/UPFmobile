package mobile.android.upf.ui.restaurant.restaurant_analytics;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RestaurantsAnalyticsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RestaurantsAnalyticsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
