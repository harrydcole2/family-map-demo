package com.example.familymapapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.familymapapp.tasks.DataSyncTask;
import com.example.familymapapp.tasks.LoginTask;
import com.example.familymapapp.tasks.RegisterTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import request.*;

public class LoginFragment extends Fragment {

    private EditText serverHost, serverPort;
    private EditText username, password, firstName, lastName, email;
    private RadioGroup gender;
    private Button loginButton, registerButton;
    private Listener listener;

    public interface Listener {
        void notifyDone();
    }
    public void registerListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        serverHost = view.findViewById(R.id.server_host);
        serverPort = view.findViewById(R.id.server_port);
        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        firstName = view.findViewById(R.id.first_name);
        lastName = view.findViewById(R.id.last_name);
        email = view.findViewById(R.id.user_email);
        gender = view.findViewById(R.id.gender_radio_group);

        loginButton = view.findViewById(R.id.login_button);
        registerButton = view.findViewById(R.id.register_button);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        serverHost.addTextChangedListener(textWatcher);
        serverPort.addTextChangedListener(textWatcher);
        username.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        firstName.addTextChangedListener(textWatcher);
        lastName.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);

        loginButton.setOnClickListener(loginButtonListener);
        registerButton.setOnClickListener(registerButtonListener);
    }

    private Handler dataSyncHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            Bundle bundle = message.getData();
            Toast.makeText(getContext(), "Welcome " + bundle.getString("name"), Toast.LENGTH_SHORT).show();
            listener.notifyDone();
        }
    };

    private View.OnClickListener registerButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RegisterRequest req = new RegisterRequest();
            req.setUsername(username.getText().toString());
            req.setPassword(password.getText().toString());
            req.setEmail(email.getText().toString());
            req.setFirstName(firstName.getText().toString());
            req.setLastName(lastName.getText().toString());

            int selectedGenderId = gender.getCheckedRadioButtonId();
            if(gender.findViewById(selectedGenderId) == gender.getChildAt(0)) {
                req.setGender("m");
            }
            else {
                req.setGender("f");
            }

            ExecutorService executorService = Executors.newSingleThreadExecutor();

            Handler registerHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message message) {
                    Bundle bundle = message.getData();
                    if(bundle.getBoolean("isSuccess")) {
                        DataSyncTask dataSyncTask = new DataSyncTask(dataSyncHandler, bundle.getString("Authorization"),
                                serverHost.getText().toString(), serverPort.getText().toString());

                        executorService.submit(dataSyncTask);
                    }
                    else {
                        Toast.makeText(getContext(), "register failed", Toast.LENGTH_SHORT).show();
                    }
                }
            };

            RegisterTask registerTask = new RegisterTask(registerHandler, req,
                    serverHost.getText().toString(), serverPort.getText().toString());

            executorService.submit(registerTask);
        }
    };

    private View.OnClickListener loginButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LoginRequest req = new LoginRequest();
            req.setUsername(username.getText().toString());
            req.setPassword(password.getText().toString());

            ExecutorService executorService = Executors.newSingleThreadExecutor();

            Handler loginTaskHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message message) {
                    Bundle bundle = message.getData();
                    if(bundle.getBoolean("isSuccess")) {
                        DataSyncTask dataSyncTask = new DataSyncTask(dataSyncHandler, bundle.getString("Authorization"),
                                serverHost.getText().toString(), serverPort.getText().toString());

                        executorService.submit(dataSyncTask);
                    }
                    else {
                        Toast.makeText(getContext(), "login failed", Toast.LENGTH_SHORT).show();
                    }
                }
            };

            LoginTask loginTask = new LoginTask(loginTaskHandler, req,
                    serverHost.getText().toString(), serverPort.getText().toString());

            executorService.submit(loginTask);
        }
    };

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String port = serverPort.getText().toString().trim();
            String host = serverHost.getText().toString().trim();
            String user = username.getText().toString().trim();
            String pwd = password.getText().toString().trim();
            String fn = firstName.getText().toString().trim();
            String ln = lastName.getText().toString().trim();
            String emessage = email.getText().toString().trim();

            boolean isFilled = !port.isEmpty() && !host.isEmpty() && !user.isEmpty() && !pwd.isEmpty();

            loginButton.setEnabled(isFilled);
            registerButton.setEnabled(isFilled && !fn.isEmpty() && !ln.isEmpty() && !emessage.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };
}