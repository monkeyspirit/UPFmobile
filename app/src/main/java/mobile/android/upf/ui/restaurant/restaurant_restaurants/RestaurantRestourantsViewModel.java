package mobile.android.upf.ui.restaurant.restaurant_restaurants;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RestaurantRestourantsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RestaurantRestourantsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}