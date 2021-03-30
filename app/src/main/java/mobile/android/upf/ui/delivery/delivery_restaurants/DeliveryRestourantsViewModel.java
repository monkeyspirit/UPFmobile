package mobile.android.upf.ui.delivery.delivery_restaurants;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DeliveryRestourantsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DeliveryRestourantsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}