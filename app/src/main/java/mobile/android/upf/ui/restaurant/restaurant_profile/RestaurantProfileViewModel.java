package mobile.android.upf.ui.restaurant.restaurant_profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RestaurantProfileViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RestaurantProfileViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}