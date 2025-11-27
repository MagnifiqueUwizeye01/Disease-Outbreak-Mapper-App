package com.healthtracker.chw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.healthtracker.chw.R;

public class LoginScreenFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View btnLogin = view.findViewById(R.id.btn_login);
        View tvRegister = view.findViewById(R.id.tv_register);

        final NavController navController = Navigation.findNavController(view);

        View.OnClickListener navigateToHomeListener = v ->
                navController.navigate(R.id.action_login_to_home);

        if (btnLogin != null) {
            btnLogin.setOnClickListener(navigateToHomeListener);
        }

        if (tvRegister != null) {
            tvRegister.setOnClickListener(navigateToHomeListener);
        }
    }
}
