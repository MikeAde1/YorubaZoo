package example.yorubazoo;

import android.content.Context;
import android.content.SharedPreferences;

//import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import example.yorubazoo.models.AnimalData;

public class SessionManager {
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";

    List<AnimalData> animalDataList = new ArrayList<>();

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    void putList(AnimalData animalData) {
        this.animalDataList.add(animalData);
  //      Gson gson = new Gson();
    //    String string = gson.toJson(animalDataList);
      //  editor.putString("animal", string).apply();

        /*this.transCardHashMap.put(card_id, transCard);
        Gson gson = new Gson();
        String string = gson.toJson(transCardHashMap);
        editor.putString("card", string).apply();*/

    }
    void clearList(){
        this.animalDataList.clear();
    }

}
