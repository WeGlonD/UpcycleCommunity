package com.example.upcyclecommunity.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public interface Reacts {
    void ifDataChanged(DataSnapshot dataSnapshot);
    void ifCancelled(DatabaseError error);
}
