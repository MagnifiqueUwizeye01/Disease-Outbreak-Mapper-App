package com.healthtracker.chw.services;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for Firebase Authentication
 * Handles login, registration, password reset, and Google Sign-In
 */
public class AuthService {
    private static final String TAG = "AuthService";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private Context context;
    
    public AuthService(Context context) {
        this.context = context;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
    }
    
    /**
     * Register new CHW user with email and password
     * Validates CHW code before registration
     */
    public void register(String email, String password, String fullName, String phone, 
                        String chwCode, RegistrationCallback callback) {
        // Step 1: Validate CHW code
        validateCHWCode(chwCode, new CHWCodeValidationCallback() {
            @Override
            public void onValid() {
                // Step 2: Create Firebase Auth account
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                // Step 3: Save user data to Firestore
                                saveUserToFirestore(user.getUid(), email, fullName, phone, chwCode, new FirestoreCallback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d(TAG, "Registration successful for: " + email);
                                        if (callback != null) {
                                            callback.onSuccess(user.getUid(), email, fullName, chwCode, phone);
                                        }
                                    }
                                    
                                    @Override
                                    public void onError(String error) {
                                        Log.e(TAG, "Error saving user to Firestore: " + error);
                                        // User is created in Auth but not in Firestore - delete Auth account
                                        user.delete();
                                        if (callback != null) {
                                            callback.onError("Failed to save user data: " + error);
                                        }
                                    }
                                });
                            } else {
                                if (callback != null) {
                                    callback.onError("User creation failed");
                                }
                            }
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                            Log.e(TAG, "Registration failed: " + error);
                            if (callback != null) {
                                callback.onError(error);
                            }
                        }
                    });
            }
            
            @Override
            public void onInvalid(String error) {
                Log.e(TAG, "Invalid CHW code: " + error);
                if (callback != null) {
                    callback.onError(error);
                }
            }
        });
    }
    
    /**
     * Login with email and password
     */
    public void login(String email, String password, LoginCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // Get user data from Firestore
                        getUserFromFirestore(user.getUid(), new UserDataCallback() {
                            @Override
                            public void onSuccess(String email, String name, String chwCode, String phone) {
                                Log.d(TAG, "Login successful for: " + email);
                                if (callback != null) {
                                    callback.onSuccess(user.getUid(), email, name, chwCode, phone);
                                }
                            }
                            
                            @Override
                            public void onError(String error) {
                                Log.e(TAG, "Error getting user data: " + error);
                                if (callback != null) {
                                    callback.onError("Failed to load user data: " + error);
                                }
                            }
                        });
                    } else {
                        if (callback != null) {
                            callback.onError("User not found");
                        }
                    }
                } else {
                    String error = task.getException() != null ? task.getException().getMessage() : "Login failed";
                    Log.e(TAG, "Login failed: " + error);
                    if (callback != null) {
                        callback.onError(error);
                    }
                }
            });
    }
    
    /**
     * Send password reset email
     */
    public void resetPassword(String email, PasswordResetCallback callback) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Password reset email sent to: " + email);
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } else {
                    String error = task.getException() != null ? task.getException().getMessage() : "Failed to send reset email";
                    Log.e(TAG, "Password reset failed: " + error);
                    if (callback != null) {
                        callback.onError(error);
                    }
                }
            });
    }
    
    /**
     * Get Google Sign-In client
     * Note: You need to add your Web Client ID to strings.xml as "default_web_client_id"
     * Get it from Firebase Console: Project Settings > Your apps > Web app > OAuth client ID
     */
    public GoogleSignInClient getGoogleSignInClient() {
        // Try to get Web Client ID from strings.xml, or use a placeholder
        String webClientId;
        try {
            int resId = context.getResources().getIdentifier("default_web_client_id", "string", context.getPackageName());
            if (resId != 0) {
                webClientId = context.getString(resId);
            } else {
                // Fallback: You'll need to add this to strings.xml
                // Get it from Firebase Console: Project Settings > Your apps > Web app
                Log.w(TAG, "default_web_client_id not found in strings.xml. Google Sign-In may not work.");
                webClientId = null; // Will cause Google Sign-In to fail gracefully
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting Web Client ID", e);
            webClientId = null;
        }
        
        GoogleSignInOptions.Builder builder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail();
        
        if (webClientId != null && !webClientId.isEmpty()) {
            builder.requestIdToken(webClientId);
        }
        
        return GoogleSignIn.getClient(context, builder.build());
    }
    
    /**
     * Handle Google Sign-In result
     */
    public void handleGoogleSignIn(Task<GoogleSignInAccount> task, GoogleSignInCallback callback) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(authTask -> {
                        if (authTask.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                // Check if user exists in Firestore
                                checkUserExistsInFirestore(user.getUid(), new UserExistsCallback() {
                                    @Override
                                    public void onExists(String email, String name, String chwCode, String phone) {
                                        // User exists, login successful
                                        if (callback != null) {
                                            callback.onSuccess(user.getUid(), email, name, chwCode, phone);
                                        }
                                    }
                                    
                                    @Override
                                    public void onNotExists() {
                                        // New user - need to register with CHW code
                                        if (callback != null) {
                                            callback.onNeedsCHWCode(user.getUid(), user.getEmail(), user.getDisplayName());
                                        }
                                    }
                                    
                                    @Override
                                    public void onError(String error) {
                                        if (callback != null) {
                                            callback.onError(error);
                                        }
                                    }
                                });
                            }
                        } else {
                            String error = authTask.getException() != null ? authTask.getException().getMessage() : "Google sign-in failed";
                            if (callback != null) {
                                callback.onError(error);
                            }
                        }
                    });
            }
        } catch (ApiException e) {
            Log.e(TAG, "Google sign-in failed", e);
            if (callback != null) {
                callback.onError("Google sign-in failed: " + e.getMessage());
            }
        }
    }
    
    /**
     * Complete Google Sign-In registration with CHW code
     */
    public void completeGoogleRegistration(String uid, String email, String name, String phone, 
                                          String chwCode, RegistrationCallback callback) {
        // Validate CHW code first
        validateCHWCode(chwCode, new CHWCodeValidationCallback() {
            @Override
            public void onValid() {
                // Save user to Firestore
                saveUserToFirestore(uid, email, name, phone, chwCode, new FirestoreCallback() {
                    @Override
                    public void onSuccess() {
                        if (callback != null) {
                            callback.onSuccess(uid, email, name, chwCode, phone);
                        }
                    }
                    
                    @Override
                    public void onError(String error) {
                        if (callback != null) {
                            callback.onError(error);
                        }
                    }
                });
            }
            
            @Override
            public void onInvalid(String error) {
                if (callback != null) {
                    callback.onError(error);
                }
            }
        });
    }
    
    /**
     * Validate CHW code against Firestore
     */
    private void validateCHWCode(String chwCode, CHWCodeValidationCallback callback) {
        if (chwCode == null || chwCode.trim().isEmpty()) {
            callback.onInvalid("CHW Code is required");
            return;
        }
        
        firestore.collection("chw_codes")
            .whereEqualTo("code", chwCode.trim())
            .whereEqualTo("isActive", true)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot snapshot = task.getResult();
                    if (snapshot != null && !snapshot.isEmpty()) {
                        // Valid code found
                        callback.onValid();
                    } else {
                        callback.onInvalid("Invalid or inactive CHW Code");
                    }
                } else {
                    String error = task.getException() != null ? task.getException().getMessage() : "Failed to validate CHW code";
                    callback.onInvalid("Error validating CHW code: " + error);
                }
            });
    }
    
    /**
     * Save user data to Firestore
     */
    private void saveUserToFirestore(String uid, String email, String fullName, String phone, 
                                     String chwCode, FirestoreCallback callback) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", uid);
        userData.put("email", email);
        userData.put("fullName", fullName);
        userData.put("phone", phone);
        userData.put("chwCode", chwCode);
        userData.put("role", "CHW");
        userData.put("createdAt", com.google.firebase.Timestamp.now());
        userData.put("lastLogin", com.google.firebase.Timestamp.now());
        
        firestore.collection("chw_users")
            .document(uid)
            .set(userData)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "User data saved to Firestore");
                callback.onSuccess();
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error saving user data", e);
                callback.onError(e.getMessage());
            });
    }
    
    /**
     * Get user data from Firestore
     */
    private void getUserFromFirestore(String uid, UserDataCallback callback) {
        firestore.collection("chw_users")
            .document(uid)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String email = documentSnapshot.getString("email");
                    String name = documentSnapshot.getString("fullName");
                    String chwCode = documentSnapshot.getString("chwCode");
                    String phone = documentSnapshot.getString("phone");
                    
                    // Update last login
                    firestore.collection("chw_users")
                        .document(uid)
                        .update("lastLogin", com.google.firebase.Timestamp.now());
                    
                    callback.onSuccess(email, name, chwCode, phone);
                } else {
                    callback.onError("User data not found");
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error getting user data", e);
                callback.onError(e.getMessage());
            });
    }
    
    /**
     * Check if user exists in Firestore
     */
    private void checkUserExistsInFirestore(String uid, UserExistsCallback callback) {
        firestore.collection("chw_users")
            .document(uid)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String email = documentSnapshot.getString("email");
                    String name = documentSnapshot.getString("fullName");
                    String chwCode = documentSnapshot.getString("chwCode");
                    String phone = documentSnapshot.getString("phone");
                    
                    // Update last login
                    firestore.collection("chw_users")
                        .document(uid)
                        .update("lastLogin", com.google.firebase.Timestamp.now());
                    
                    callback.onExists(email, name, chwCode, phone);
                } else {
                    callback.onNotExists();
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error checking user existence", e);
                callback.onError(e.getMessage());
            });
    }
    
    /**
     * Get current Firebase user
     */
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }
    
    /**
     * Logout
     */
    public void logout() {
        firebaseAuth.signOut();
    }
    
    // Callback interfaces
    public interface RegistrationCallback {
        void onSuccess(String userId, String email, String name, String chwCode, String phone);
        void onError(String error);
    }
    
    public interface LoginCallback {
        void onSuccess(String userId, String email, String name, String chwCode, String phone);
        void onError(String error);
    }
    
    public interface PasswordResetCallback {
        void onSuccess();
        void onError(String error);
    }
    
    public interface GoogleSignInCallback {
        void onSuccess(String userId, String email, String name, String chwCode, String phone);
        void onNeedsCHWCode(String uid, String email, String name); // New Google user needs CHW code
        void onError(String error);
    }
    
    private interface CHWCodeValidationCallback {
        void onValid();
        void onInvalid(String error);
    }
    
    private interface FirestoreCallback {
        void onSuccess();
        void onError(String error);
    }
    
    private interface UserDataCallback {
        void onSuccess(String email, String name, String chwCode, String phone);
        void onError(String error);
    }
    
    private interface UserExistsCallback {
        void onExists(String email, String name, String chwCode, String phone);
        void onNotExists();
        void onError(String error);
    }
}

