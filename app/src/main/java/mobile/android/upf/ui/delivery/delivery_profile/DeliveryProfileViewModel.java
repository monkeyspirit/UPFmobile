package mobile.android.upf.ui.delivery.delivery_profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DeliveryProfileViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DeliveryProfileViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}