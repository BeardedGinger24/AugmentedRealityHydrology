package edu.calstatela.jplone.watertrekapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTask;
import edu.calstatela.jplone.watertrekapp.R;

import edu.calstatela.jplone.watertrekapp.WatertrekCredentials;

public class CredentialsActivity extends Activity{
    private static final String TAG = "waka-credentials";

    TextInputEditText mUsernameEditText;
    TextInputEditText mPasswordEditText;
    Button mSubmitButton;
    TextInputLayout mUsernameLayout;
    TextInputLayout mPasswordLayout;

    String currUser;
    String currPass;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials);

        Log.d(TAG,"");
        mUsernameEditText = (TextInputEditText) findViewById(R.id.et_username);
        mPasswordEditText = (TextInputEditText) findViewById(R.id.et_password);

        mUsernameLayout = (TextInputLayout) findViewById(R.id.username_layout);
        mPasswordLayout = (TextInputLayout) findViewById(R.id.password_layout);

        mSubmitButton = (Button) findViewById(R.id.b_submit_credentials);
        mSubmitButton.setOnClickListener(buttonListener);

        mUsernameEditText.addTextChangedListener(new MyTextWatcher(mUsernameEditText));
        mPasswordEditText.addTextChangedListener(new MyTextWatcher(mPasswordEditText));

        // If default username and password have been provided in the intent, fill in fields
        String currentUsername = getIntent().getStringExtra("username");
        currUser = currentUsername;
        if(currentUsername == null)
            currentUsername = "";
        mUsernameEditText.setText(currentUsername);

        String currentPassword = getIntent().getStringExtra("password");
        currPass = currentPassword;
        if(currentPassword == null)
            currentPassword = "";
        mPasswordEditText.setText(currentPassword);
    }

    Button.OnClickListener buttonListener = new Button.OnClickListener(){
        @Override
        public void onClick(View view) {
            String usernameString = mUsernameEditText.getText().toString();
            String passwordString = mPasswordEditText.getText().toString();

            if(submitForm()) {
                Intent intent = new Intent();
                intent.putExtra("username", usernameString);
                intent.putExtra("password", passwordString);
                setResult(RESULT_OK, intent);

                NetworkTask.updateWatertrekCredentials(usernameString,passwordString);
                WatertrekCredentials credentials = new WatertrekCredentials(getApplicationContext());
                credentials.setUsername(usernameString);
                credentials.setPassword(passwordString);
                finish();
            }


        }
    };

    private boolean submitForm() {
        if (!validateUsername()) { return false; }
        if (!validatePassword()) { return false; }


        return true;
    }

    private boolean validateUsername() {
        String username = mUsernameEditText.getText().toString().trim();

        if(username.isEmpty()){
            mUsernameEditText.setError("Please Enter a Username");
            requestFocus(mUsernameEditText);
            return false;
        } else {
            mUsernameLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        String password = mPasswordEditText.getText().toString().trim();

        if(password.isEmpty()){
            mPasswordEditText.setError("Please Enter a Password");
            requestFocus(mPasswordEditText);
            return false;
        } else {
            mPasswordLayout.setErrorEnabled(false);
        }

        return true;
    }

    public static void launch(Activity currentActivity, String defaultUsername, String defaultPassword, int requestCode){
        Intent intent = new Intent(currentActivity, CredentialsActivity.class);
        intent.putExtra("username", defaultUsername);
        intent.putExtra("password", defaultPassword);
        currentActivity.startActivityForResult(intent, requestCode);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
        }
    }

}
