package edu.calstatela.jplone.watertrekapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.calstatela.jplone.watertrekapp.WatertrekCredentials;

public class CredentialsActivity extends Activity{
    private static final String TAG = "waka-credentials";

    EditText usernameEditText;
    EditText passwordEditText;
    Button submitButton;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup Layout
        LinearLayout verticalLayout = new LinearLayout(this);
        verticalLayout.setOrientation(LinearLayout.VERTICAL);

        TextView sectionTitleTextView = new TextView(this);
        sectionTitleTextView.setText("Watertrek Credentials");

        LinearLayout usernameLayout = new LinearLayout(this);
        usernameLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView usernameTextView = new TextView(this);
        usernameTextView.setText("username: ");
        usernameEditText = new EditText(this);
        usernameEditText.setMinWidth(200);
        usernameLayout.addView(usernameTextView);
        usernameLayout.addView(usernameEditText);

        LinearLayout passwordLayout = new LinearLayout(this);
        passwordLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView passwordTextView = new TextView(this);
        passwordTextView.setText("password: ");
        passwordEditText = new EditText(this);
        passwordEditText.setMinWidth(200);
        passwordLayout.addView(passwordTextView);
        passwordLayout.addView(passwordEditText);

        submitButton = new Button(this);
        submitButton.setText("Submit");
        submitButton.setOnClickListener(buttonListener);

        verticalLayout.addView(sectionTitleTextView);
        verticalLayout.addView(usernameLayout);
        verticalLayout.addView(passwordLayout);
        verticalLayout.addView(submitButton);

        this.setContentView(verticalLayout);

        // If default username and password have been provided in the intent, fill in fields
        String currentUsername = getIntent().getStringExtra("username");
        if(currentUsername == null)
            currentUsername = "";
        usernameEditText.setText(currentUsername);

        String currentPassword = getIntent().getStringExtra("password");
        if(currentPassword == null)
            currentPassword = "";
        passwordEditText.setText(currentPassword);
    }

    Button.OnClickListener buttonListener = new Button.OnClickListener(){
        @Override
        public void onClick(View view) {
            String usernameString = usernameEditText.getText().toString();
            String passwordString = passwordEditText.getText().toString();

            Intent intent = new Intent();
            intent.putExtra("username", usernameString);
            intent.putExtra("password", passwordString);
            setResult(RESULT_OK, intent);

            finish();
        }
    };

    public static void launch(Activity currentActivity, String defaultUsername, String defaultPassword, int requestCode){
        Intent intent = new Intent(currentActivity, CredentialsActivity.class);
        intent.putExtra("username", defaultUsername);
        intent.putExtra("password", defaultPassword);
        currentActivity.startActivityForResult(intent, requestCode);
    }

}
