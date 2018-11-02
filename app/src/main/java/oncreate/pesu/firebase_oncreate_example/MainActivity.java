package oncreate.pesu.firebase_oncreate_example;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //Declaring up all the UI elements
    Button updateDB;
    Button searchDB;
    EditText entername;
    EditText enterUID;
    EditText searchname;
    TextView displaySearchResults;
    TextView totalcount;

    //Declaring local database reference
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assigning the reference with the ROOT node of your Firebase database
        myRef = FirebaseDatabase.getInstance().getReference();

        //Binding UI elements
        updateDB = findViewById(R.id.updateDBbutton);
        searchDB = findViewById(R.id.searchname_button);
        entername = findViewById(R.id.addname_editxt);
        enterUID = findViewById(R.id.enteruid_editxt);
        searchname = findViewById(R.id.searchname_edittxt);
        displaySearchResults = findViewById(R.id.display_name);
        totalcount = findViewById(R.id.total_names_txt);

        //Setting up onClickListeners, this is another way to do it. You can use the same way you've done in class before.
        updateDB.setOnClickListener(this);
        searchDB.setOnClickListener(this);

        /**Setting a ValueEventListener for the ROOT node , this will detect any updates to your database and will update the
         * TextView in your app accordingly
        */
        ValueEventListener mVal = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String op = "No. of users in your database: "+ dataSnapshot.child("users").getChildrenCount();
                totalcount.setText(op);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        //Attaching the ValueEventListener we defined above to the local database reference.
        myRef.addValueEventListener(mVal);



    }

    //onClick method, you can replace this with your own one IF you're defining the click listener the way we've done in class
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.updateDBbutton:
                // Getting the string inputs and extracting them.
                    String name = entername.getText().toString();
                    String uid = enterUID.getText().toString();

                    //Checking to make sure the strings aren't empty.
                    if( !name.isEmpty() && !uid.isEmpty()){
                        //Adding a new user to the database.
                        myRef.child("users").child(name).setValue(uid);
                    }else{
                        //If there was no input, we Toast a message.
                        Toast.makeText(getApplicationContext(),"You are missing a parameter..",Toast.LENGTH_SHORT).show();
                    }

                break;
            case R.id.searchname_button:
                    //Extracting the name tp be found.
                    String s_name = searchname.getText().toString();
                    if(s_name!=""){
                        //Calling a method, this is basically like calling a function in python.
                        searchName(s_name);
                    }

        }

    }

    //Custom function that searches database for the name.
    public void searchName(final String name_find){

        /**This is a singleValueEvent listener. It runs only once. It is used here as you will only search the database once per button
         * click. This helps in speeding up your app and prevents unnecessary searching through your database every time the database is updated.
        */
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Making sure input string isn't empty.
                if(!name_find.isEmpty()){
                    //Checking if database has a child with that name
                    if(dataSnapshot.child("users").hasChild(name_find)){
                        //Getting name and UID from database and displaying using TextView
                        String temp = "User "+name_find+" has a UID of "+dataSnapshot.child("users").child(name_find).getValue(String.class);
                        displaySearchResults.setText(temp);
                    }else{
                        //If not found, we say this.
                        displaySearchResults.setText("User not present"); }

                }else{
                    //If no input given, this Toast will show up.
                    Toast.makeText(getApplicationContext(),"Please enter a name..",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
