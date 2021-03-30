package mobile.android.upf.ui.client.client_homepage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ClientOrdersViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ClientOrdersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}