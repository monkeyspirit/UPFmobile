package mobile.android.upf.ui.restaurant.restaurant_homepage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RestaurantOrdersViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RestaurantOrdersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}