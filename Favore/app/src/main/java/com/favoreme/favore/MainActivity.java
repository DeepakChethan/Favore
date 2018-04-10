package com.favoreme.favore;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.favoreme.favore.Fragments.HomeFragment;
import com.favoreme.favore.Fragments.UserPostFragment;
import com.favoreme.favore.Location.Tracker;
import com.favoreme.favore.Login.LoginActivity;
import com.favoreme.favore.Models.Loci;
import com.favoreme.favore.Models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser usr;
    private Button btn;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    String post_text;
    private ListView lst;
    private ArrayList<Post> posts;
    private Date dt;
    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
    private DatabaseReference fdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dt = new Date();
        auth = FirebaseAuth.getInstance();
        usr = auth.getCurrentUser();
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        fdb= FirebaseDatabase.getInstance().getReference();
        if (usr == null){
            //if not signed in, launch the Sign In Activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        Fragment home = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame,home).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){

            //TODO update this mate
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.text:
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setTitle("New Post");
                        alert.setMessage("What's on your mind!");
                        final EditText input = new EditText(MainActivity.this);
                        alert.setView(input);
                        alert.setPositiveButton("Post!", new DialogInterface.OnClickListener() {
                            // TODO Integrate firebase
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                post_text = input.getText().toString();
                                Tracker t = new Tracker(getApplicationContext());
                                Location l = t.getLocation();
                                Toast.makeText(getApplicationContext(),t.getLocation()+" ",Toast.LENGTH_SHORT).show();
                                if (l != null) {

                                    Loci loci = new Loci(l.getLongitude(), l.getLatitude());
                                    Post post = new Post(fdb.push().getKey(), usr.getDisplayName(), post_text, loci, dt.getTime(), 0, usr.getPhotoUrl().toString());
                                    fdb.child("Posts").child(post.getPost_id()).setValue(post);
                                }

                            }
                        });
                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                        alert.show();


                        break;

                    case R.id.nav:
                        // TODO implement this shit
                        break;
                    case R.id.home:
                        getSupportActionBar().setTitle("Home");
                        Fragment home = new HomeFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame,home).commit();
                        break;
                    case R.id.profile:
                        getSupportActionBar().setTitle("My Posts");
                        Fragment frag = new UserPostFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, frag).commit();
                        break;
                }
                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.app_bar_search:
                Toast.makeText(getApplicationContext(),"This will be implemented in future",Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}