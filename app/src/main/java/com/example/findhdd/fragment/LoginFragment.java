package com.example.findhdd.fragment;

import android.content.Intent;
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
import com.example.findhdd.activity.MainActivity;
import com.example.findhdd.R;
import com.example.findhdd.PreferencesManager;
import com.example.findhdd.api.UserApi;
import com.example.findhdd.dto.UserDTO;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginFragment extends Fragment {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        usernameInput = view.findViewById(R.id.usernameInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        loginButton = view.findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> attemptLogin());

        TextView registerLink = view.findViewById(R.id.registerLink);
        registerLink.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).loadRegisterFragment();
            }
        });

        return view;
    }

    private void attemptLogin() {
        String username = usernameInput.getText().toString().trim().toLowerCase();
        String password = passwordInput.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }
        UserApi userApi = ApiClient.getClient(username, password).create(UserApi.class);

        userApi.login().enqueue(new Callback<ApiMessage>() {
            @Override
            public void onResponse(Call<ApiMessage> call, Response<ApiMessage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    fetchUserInfo(userApi, username, password);
                } else {
                    handleLoginError(response);
                }
            }

            @Override
            public void onFailure(Call<ApiMessage> call, Throwable t) {
                Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserInfo(UserApi userApi, String username, String password) {
        userApi.getCurrentUser().enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserDTO user = response.body();
                    boolean isAdmin = user.getRole() != null && user.getRole().equals("ROLE_ADMIN");
                    PreferencesManager prefs = new PreferencesManager(requireContext());
                    prefs.saveCredentials(username, password, isAdmin);
                    navigateToMain();
                } else {
                    Toast.makeText(getContext(), "Не удалось получить информацию о пользователе", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Toast.makeText(getContext(), "Ошибка при получении информации о пользователе: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleLoginError(Response<ApiMessage> response) {
        try {
            String errorBody = response.errorBody().string();
            ApiMessage error = new Gson().fromJson(errorBody, ApiMessage.class);
            String errorMessage = (error != null && error.getMessage() != null)
                    ? error.getMessage()
                    : "Ошибка входа: " + response.code();
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Ошибка входа", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}