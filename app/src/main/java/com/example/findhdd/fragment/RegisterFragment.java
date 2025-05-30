package com.example.findhdd.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.findhdd.api.ApiClient;
import com.example.findhdd.api.ApiMessage;
import com.example.findhdd.activity.AuthActivity;
import com.example.findhdd.R;
import com.example.findhdd.api.UserApi;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterFragment extends Fragment {

    private EditText usernameInput, emailInput, passwordInput, confirmPasswordInput;
    private Button registerButton;
    private TextView loginLink;
    private UserApi userApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        usernameInput = view.findViewById(R.id.registerUsernameInput);
        emailInput = view.findViewById(R.id.registerEmailInput);
        passwordInput = view.findViewById(R.id.registerPasswordInput);
        confirmPasswordInput = view.findViewById(R.id.registerConfirmPasswordInput);
        registerButton = view.findViewById(R.id.registerButton);
        loginLink = view.findViewById(R.id.loginLink);

        registerButton.setOnClickListener(v -> attemptRegister());
        loginLink.setOnClickListener(v -> switchToLogin());

        return view;
    }

    private void attemptRegister() {
        String username = usernameInput.getText().toString().toLowerCase();
        String email = emailInput.getText().toString().toLowerCase();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> registrationData = new HashMap<>();
        registrationData.put("username", username);
        registrationData.put("email", email);
        registrationData.put("password", password);

        Retrofit retrofit = ApiClient.getClient();
        userApi = retrofit.create(UserApi.class);

        userApi.register(registrationData).enqueue(new Callback<ApiMessage>() {
            @Override
            public void onResponse(Call<ApiMessage> call, Response<ApiMessage> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Регистрация успешна", Toast.LENGTH_SHORT).show();
                    switchToLogin();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        ApiMessage error = new Gson().fromJson(errorBody, ApiMessage.class);
                        String errorMessage = (error != null && error.getMessage() != null)
                                ? error.getMessage()
                                : "Ошибка регистрации: " + response.code();
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(getContext(), "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<ApiMessage> call, Throwable t) {
                Toast.makeText(getContext(), "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void switchToLogin() {
        if (getActivity() instanceof AuthActivity) {
            ((AuthActivity) getActivity()).loadLoginFragment();
        }
    }
}