package example.yorubazoo;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import example.yorubazoo.models.AnimalData;

public class Backend extends Application {
    FirebaseDatabase firebaseDatabase;
    Context context;
    DatabaseReference dbRef;


    @Override
    public void onCreate() {
        super.onCreate();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        /*initFirebase();
        final SessionManager sessionManager  = new SessionManager(this);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sessionManager.clearList();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    AnimalData animalData = snapshot.getValue(AnimalData.class);
                    sessionManager.putList(animalData);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });*/
    }
    private void initFirebase() {
        this.context = this;
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.dbRef = firebaseDatabase.getReference("users");
    }
}
