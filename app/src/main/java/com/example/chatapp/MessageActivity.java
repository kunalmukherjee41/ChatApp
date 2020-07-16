package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Adapter.MessageAdapter;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MessageActivity extends AppCompatActivity {

    ImageView profile_image;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    ImageButton btn_send;
    EditText txt_send;

    MessageAdapter messageAdapter;
    List<Chat> mchat;
    RecyclerView recyclerView;

    Intent intent;

    ValueEventListener seenListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MessageActivity.this,Main2Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);


        intent = getIntent();
        final String userid = intent.getStringExtra("userid");

        btn_send = findViewById(R.id.btn_send);
        txt_send = findViewById(R.id.txt_send);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        btn_send.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        String msg = txt_send.getText().toString();
                        if(!(msg.equals(""))){
                            sendMessage(firebaseUser.getUid(), userid, msg);
                        }
                        txt_send.setText("");
                    }
                }
        );

        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        assert user != null;
                        username.setText(user.getUsername());

//                        if("normal".equals(user.getImage())) {
//                            profile_image.setImageResource(R.mipmap.ic_launcher);
//                        }else {
//                            Glide.with(MessageActivity.this).load(user.getImage()).into(profile_image);
//                        }

//                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                            if(Objects.equals(dataSnapshot.getKey(), "image")) {
//                                if (!(Objects.equals(dataSnapshot.getValue(), "normal"))) {
////                                    profile_image.setImageResource(R.mipmap.ic_launcher);
////                                } else {
//                                    Glide.with(getApplicationContext()).load(dataSnapshot.getValue()).into(profile_image);
//                                }
//                            }
//                        }

                        readMessage(firebaseUser.getUid(), userid, user.getImage());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        seenMessage(userid);
    }

    private void seenMessage(final String userid){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListner =  reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Chat chat = dataSnapshot.getValue(Chat.class);
                            if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)){
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("isseen", true);

                                dataSnapshot.getRef().updateChildren(hashMap);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    private void sendMessage(String sender, String receiver, String message){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>() ;
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen",false);

        reference.child("Chats").push().setValue(hashMap);
    }

    private void readMessage(final String myid, final String userid, final String imageurl){
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mchat.clear();
                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            Chat chat = snapshot1.getValue(Chat.class);

                            if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) || chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                                mchat.add(chat);
                            }
                            messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageurl);
                            recyclerView.setAdapter(messageAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    private void status(String status1){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status1);
        reference.updateChildren(hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListner);
        status("Offline");
    }

}
