package mobile.android.upf.ui.admin.admin_restaurants;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AdminRestourantsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AdminRestourantsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}