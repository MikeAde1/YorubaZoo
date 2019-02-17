package example.yorubazoo;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.FileLoader;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.GifImageView;

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.img)ImageView imageView;
    @BindView(R.id.description)TextView description;
    @BindView(R.id.animal)TextView name;
    @BindView(R.id.translation)TextView translation;
    @BindView(R.id.action_voice)ImageButton voice;
    @BindView(R.id.pg)ProgressBar progressBar;
    FirebaseStorage storage;
    StorageReference httpsReference;
    String photo;
    File animal_file1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        storage = FirebaseStorage.getInstance();
        getMessage(getIntent());
    }

    public void getMessage(Intent intent){
        String animal = intent.getStringExtra("animal");
        String animal_name = animal.toLowerCase();
        String trans = intent.getStringExtra("name");
        String desc = intent.getStringExtra("description");
        photo = intent.getStringExtra("url");
        writeExternalStorage(getApplicationContext(), animal_name);

        /*if (photo != null){
            Glide.with(getApplicationContext() )
                    .load(Uri.parse(photo))
                    .asGif()
                    .diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Uri, GifDrawable>() {
                @Override
                public boolean onException(Exception e, Uri model, Target<GifDrawable> target, boolean isFirstResource) {
                    Toast.makeText(getApplicationContext(),"Please check your internet connection",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    return false;
                }
                @Override
                public boolean onResourceReady(GifDrawable resource, Uri model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(imageView);
        }*/


        //Toast.makeText(getApplicationContext(), photo,Toast.LENGTH_LONG).show();
        name.setText(animal);
        translation.setText(trans);
        description.setText(desc);
    }

    private void writeExternalStorage(Context context, String title){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){

            final File root = Environment.getExternalStorageDirectory();
            File childfile = new File(root.getAbsolutePath()+"/YorubaZoo");
            if (!childfile.exists()){
                childfile.mkdirs();
            }
            title = title+".gif";
            File file2 = new File(childfile,title);

            /*File file = new File(context.getExternalFilesDir(
                    Environment.DIRECTORY_PICTURES), "YorubaZoo");

            if (!file.exists()){
                file.mkdirs();
            }
            title = title+".gif";
            File file1 = new File(file, title);*/

            readFromDb(getApplicationContext(),file2, title );

        }else {
            Toast.makeText(getApplicationContext(),"No SD card found",Toast.LENGTH_LONG).show();
        }
    }

    private void useGlide( Uri uri){
        Glide.with(getApplicationContext() )
                .load(uri)
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE).listener(new RequestListener<Uri, GifDrawable>() {
            @Override
            public boolean onException(Exception e, Uri model, Target<GifDrawable> target, boolean isFirstResource) {
                //Toast.makeText(getApplicationContext(), e.toString(),Toast.LENGTH_LONG).show();
                if (e != null)
                Log.i("ExternalStorage", "-> uri=" + e.toString());
                progressBar.setVisibility(View.GONE);
                return false;
            }
            @Override
            public boolean onResourceReady(GifDrawable resource, Uri model, Target<GifDrawable> target, boolean isFromMemoryCache,
                                           boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(imageView);

    }

    private void readFromDb(final Context context, final File animalfile, final String animal_name){
        //checks if the file is in memory
        /*MediaScannerConnection.scanFile(getApplicationContext(), new String[] { animalfile.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {*/
        animal_file1 = animalfile;
        progressBar.setVisibility(View.VISIBLE);
        //final File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "YorubaZoo");
        File root = Environment.getExternalStorageDirectory();
        final File file = new File(root.getAbsolutePath()+"/YorubaZoo");
                        File file1 = new File(file, animal_name);
                        if (file1.exists()){
                            useGlide(Uri.fromFile(file1));
                        /*try {
                                pl.droidsonroids.gif.GifDrawable gifDrawable = new pl.droidsonroids.gif.GifDrawable(file1);
                                imageView.setBackground(gifDrawable);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }*/
                        }
                        else {
                            if(photo != null){
                                if (checkInternetConnection()){
                                    httpsReference = storage.getReferenceFromUrl(photo);
                                    httpsReference.getFile(animalfile)
                                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                /*pl.droidsonroids.gif.GifDrawable gifDrawable = null;
                                                    try {
                                                        gifDrawable = new pl.droidsonroids.gif.GifDrawable(animalfile);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    imageView.setBackground(gifDrawable);
                                                */
                                                    useGlide(Uri.fromFile(animalfile));
                                                    //Toast.makeText(getApplicationContext(),"ll",Toast.LENGTH_LONG).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            File failedfile =  new File(file, animal_name);
                                            if (failedfile.exists()){
                                                failedfile.delete();
                                            }
                                            Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Empty data here", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
  /*                      Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                        //Toast.makeText(getApplicationContext(), "path",Toast.LENGTH_LONG).show();
                        if (uri == null){ //if the file does not exist
*/

                            //if (Environment.getRootDirectory().getFreeSpace() > 10000) {  //if there's still space
                                //useGlide(Uri.fromFile(animalfile));
                            }
                            /*else {
                                Toast.makeText(getApplicationContext(),"No available storage space. Please delete some files",Toast.LENGTH_LONG).show();
                            }
                        }*/
  /*                      else {
                            File file  = new File(path);
                            useGlide(Uri.fromFile(file));
                        }
                        Toast.makeText(getApplicationContext(),"available",Toast.LENGTH_LONG).show();
                    }
  */              //});

    @Override
    protected void onPause() {
        super.onPause();
        }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // If there's a download in progress, save the reference so you can query it later
        if (httpsReference != null) {
            outState.putString("reference", httpsReference.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final String stringRef = savedInstanceState.getString("reference");
        if (stringRef == null) {
            return;
        }
        httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(stringRef);
                httpsReference.getFile(animal_file1);
        List<FileDownloadTask> tasks = httpsReference.getActiveDownloadTasks();
        if (tasks.size() > 0) {
            // Get the task monitoring the download
            FileDownloadTask task = tasks.get(0);

            // Add new listeners to the task using an Activity scope
            task.addOnSuccessListener(this, new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot state) {
                    // Success!
                    // ...
                    //probably a success toast
                    useGlide(Uri.fromFile(animal_file1));

                }
            });
        }
    }

    private boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        else {
            return false;
        }
    }
    }

