package dev.aakash.comment;

import android.content.Intent;
import android.graphics.Typeface;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private int SIGN_IN_REQUEST_CODE=1;
    private ListView listView;
    private  DatabaseReference ref;
    private RelativeLayout activity_main;
    private ArrayList<String> list;
    private ArrayAdapter<String> list_apadter;
    private FirebaseListAdapter<ChatMessage> adapter;

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find views by
        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i)
            {
                if(i != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }

            }
        });
        Button fab = (Button) findViewById(R.id.fab);
        final EditText input = (EditText) findViewById(R.id.input);
        listView = (ListView) findViewById(R.id.list);
        ref=FirebaseDatabase.getInstance().getReference().child("Comments");

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .build(), SIGN_IN_REQUEST_CODE);
        } else {
            // User is already signed in, show list of messages
            showAllOldMessages();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter some texts!", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseDatabase.getInstance()
                            .getReference().child("Comments")
                            .push()
                            .setValue(new ChatMessage(input.getText().toString(),
                                    FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                    FirebaseAuth.getInstance().getCurrentUser().getUid())
                            );
                    input.setText("");
                }
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in successful!", Toast.LENGTH_LONG).show();
                showAllOldMessages();
            } else {
                Toast.makeText(this, "Sign in failed, please try again later", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

   // @Override
    //public boolean onCreateOptionsMenu(Menu menu) {
      //  getMenuInflater().inflate(R.menu.main, menu);
        //return true;
    //}

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            Toast.makeText(MainActivity.this, "You have logged out!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
        }
        return true;
    }
    */private String loggedInUserName = "";




    private void showAllOldMessages()
    {
        loggedInUserName = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(FirebaseAuth.getInstance().getCurrentUser().getUid()!=getLoggedInUserName())
            Log.d("Main", "user id: " + loggedInUserName);
        Query query = FirebaseDatabase.getInstance().getReference().child("Comments");
        FirebaseListOptions<ChatMessage> options = new FirebaseListOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .setLayout(R.layout.item_in_message)
                .build();

        ListView listOfMessage = (ListView)findViewById(R.id.list);

        adapter=new FirebaseListAdapter<ChatMessage>(options)
        {

            @Override
            protected void populateView(View v, ChatMessage model, int position)
            {
                final TextView messageText, messageUser, messageTime;
                messageText=(TextView) v.findViewById(R.id.message_text);
                messageText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                Log.d("Text", String.valueOf(messageText));
                messageUser = (TextView) v.findViewById(R.id.message_user);
                messageTime = (TextView) v.findViewById(R.id.message_time);
                messageUser.setTypeface(null,Typeface.BOLD);

                messageText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String toSpeak=messageText.getText().toString();
                        Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    }
                });
                messageUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String toSpeak=messageText.getText().toString();
                        Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    }
                });
                messageTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String toSpeak=messageText.getText().toString();
                        Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    }
                });
                messageText.setText(model.getMessageText());
                Log.d("TextSet", String.valueOf(messageText));
                messageUser.setText(model.getMessageUser());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));
            }
        };

     /* adapter = new FirebaseListAdapter<ChatMessage>(this,ChatMessage.class, R.layout.item_in_message, FirebaseDatabase.getInstance().getReference())
        {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {

                //Get references to the views of list_item.xml
                TextView messageText, messageUser, messageTime;
                messageText=(TextView) v.findViewById(R.id.message_text);
                messageUser = (TextView) v.findViewById(R.id.message_user);
                messageTime = (TextView) v.findViewById(R.id.message_time);

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));

            }
        };*/

        listOfMessage.setAdapter(adapter);
    }




    public String getLoggedInUserName() {
        return loggedInUserName;
    }
    public void onPause(){
        if(tts !=null){
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
