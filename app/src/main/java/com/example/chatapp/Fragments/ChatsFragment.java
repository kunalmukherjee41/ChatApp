package com.example.chatapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.Adapter.UserAdapter;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    List<String> userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();

                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            Chat chat = snapshot1.getValue(Chat.class);
                            if(chat.getSender().equals(firebaseUser.getUid())){
                                userList.add(chat.getReceiver());
                            }
                            if(chat.getReceiver().equals(firebaseUser.getUid())){
                                userList.add(chat.getSender());
                            }
                        }
                        readChats();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return view;
    }

    private void readChats(){

        mUsers = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mUsers.clear();

                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            User user = snapshot1.getValue(User.class);

                            //Display One User form firebase
                            for(String id : userList){
                                assert user != null;
                                if(user.getId().equals(id)){
//                                    if(mUsers.size() != 0){
//                                        for(User user1 : mUsers){
//                                            if(!(user.getId().equals(user1.getId()))){
//                                                mUsers.add(user);
//                                            }
//                                        }
//                                    } else {
//                                        mUsers.add(user);
//                                    }
                                    if(mUsers.size()!=0){
                                        int flag=0;
                                        for(User u : mUsers) {
                                            if (user.getId().equals(u.getId())) {
                                                flag = 1;
                                                break;
                                            }
                                        }
                                        if(flag==0)
                                            mUsers.add(user);
                                    }else{

                                        mUsers.add(user);
                                    }
                                }
                            }
                        }

                        userAdapter = new UserAdapter(getContext(),mUsers, true);
                        recyclerView.setAdapter(userAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
