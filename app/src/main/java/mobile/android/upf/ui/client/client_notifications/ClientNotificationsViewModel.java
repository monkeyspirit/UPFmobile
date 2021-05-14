package mobile.android.upf.ui.client.client_notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ClientNotificationsViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public ClientNotificationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
