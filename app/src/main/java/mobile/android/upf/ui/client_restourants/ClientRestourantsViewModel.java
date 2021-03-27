package mobile.android.upf.ui.client_restourants;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ClientRestourantsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ClientRestourantsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}