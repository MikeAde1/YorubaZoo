package example.yorubazoo;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.yorubazoo.adapter.SearchAdapter;
import example.yorubazoo.models.AnimalData;

public class MainActivity extends AppCompatActivity implements MaterialSearchView.OnQueryTextListener {

    @BindView(R.id.search_view)
    MaterialSearchView materialSearchView;
    @BindView(R.id.toolbar)
    android.support.v7.widget.Toolbar toolbar;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.image)
    ImageView imageView;
    @BindView(R.id.button)
    Button button;

    DatabaseReference databaseRef;
    private List<AnimalData> animalDataList;
    private SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseRef = firebaseDatabase.getReference("users");

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        SetUpAdapter();

        button.setVisibility(View.GONE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DetailActivity.class));
            }
        });
        materialSearchView = findViewById(R.id.search_view);
        materialSearchView.setOnQueryTextListener(this);
        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                materialSearchView.setHint("Search");
                materialSearchView.setHintTextColor(Color.parseColor("#FF685757"));
                imageView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSearchViewClosed() {
                recyclerView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void SetUpAdapter() {
        animalDataList = new ArrayList<>();
        searchAdapter = new SearchAdapter(MainActivity.this, animalDataList, new SearchAdapter.Callback() {
            @Override
            public void itemClick(int position) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("animal",animalDataList.get(position).getAnimal());
                intent.putExtra("name",animalDataList.get(position).getName());
                //Toast.makeText(getApplicationContext(), animalDataList.get(position).getName(),Toast.LENGTH_LONG).show();
                intent.putExtra("description", animalDataList.get(position).getDescription());
                intent.putExtra("url", animalDataList.get(position).getimg());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(searchAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);
        //MenuItemCompat.setOnActionExpandListener(sea)
        MenuItem menuItem = menu.findItem(R.id.action_search);//search button
        materialSearchView.setMenuItem(menuItem);
       /* if (!materialSearchView.isSearchOpen()){
            spinner.setVisibility(View.GONE);
        }*/
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {
        if (!newText.isEmpty()){
            animalDataList.clear();
/*
            for(AnimalData animalData:animalDataList){
                if (animalData.getimg().contains(newText)){
                    animalDataList.add(animalData);
                }
            }
            if (AnimalData animal : animalDataList.contains(data)){

            }
*/
            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    animalDataList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        String context = String.valueOf(snapshot.child("Animal").getValue());
                        //might use ignore case in this scenario
                        if (context.toLowerCase().contains(newText.toLowerCase())) {
                            recyclerView.setVisibility(View.VISIBLE);

                            AnimalData animalData = snapshot.getValue(AnimalData.class);
                            animalDataList.add(animalData);
                        }
                    }
                    Log.d("datass", String.valueOf(dataSnapshot));
                    searchAdapter.setAnimalDataList(animalDataList);
                    searchAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            animalDataList.clear();
            recyclerView.setVisibility(View.GONE);
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (materialSearchView.isSearchOpen()){
            materialSearchView.closeSearch();
        }
        else{
            super.onBackPressed();
        }
    }

}