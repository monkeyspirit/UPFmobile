package mobile.android.upf.ui.delivery.delivery_homepage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DeliveryOrdersViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DeliveryOrdersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}