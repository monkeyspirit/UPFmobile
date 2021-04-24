package mobile.android.upf.ui.restaurant.restaurant_notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RestaurantNotificationsViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public RestaurantNotificationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
