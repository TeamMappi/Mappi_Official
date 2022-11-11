package com.example.mappi;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

public class FirebaseInteraction {
    private final StorageReference storageReference;
    private final FirebaseUser firebaseUser;

    private final int ONE_MEGABYTE = 1024 * 1024;
    private final String resultText = null;

    public FirebaseInteraction(StorageReference storageReference, FirebaseUser firebaseUser) {
        this.storageReference = storageReference;
        this.firebaseUser = firebaseUser;
    }

    public void uploadToFirebase(String fileName, String text) {
        StorageReference uploadStorageReference = storageReference.child(firebaseUser.getUid() + "/" + fileName + ".txt");
        byte[] textBytes = text.getBytes();
        uploadStorageReference.putBytes(textBytes).addOnSuccessListener(taskSnapshot -> {
            // Success
        }).addOnFailureListener(e -> {
            // Failure
        });
    }
}
