package mobile.android.upf.ui.delivery.delivery_notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DeliveryNotificationsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DeliveryNotificationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}