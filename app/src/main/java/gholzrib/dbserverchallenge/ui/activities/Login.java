package gholzrib.dbserverchallenge.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import gholzrib.dbserverchallenge.R;
import gholzrib.dbserverchallenge.core.models.User;
import gholzrib.dbserverchallenge.core.utils.CheckConnection;
import gholzrib.dbserverchallenge.core.utils.FieldVerification;
import gholzrib.dbserverchallenge.core.utils.PreferencesManager;

public class Login extends AppCompatActivity implements View.OnClickListener {

    EditText mEdtEmail;
    EditText mEdtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEdtEmail = (EditText) findViewById(R.id.act_login_edt_email);
        mEdtPassword = (EditText) findViewById(R.id.act_login_edt_password);

        findViewById(R.id.act_login_btn_log_in).setOnClickListener(this);
        findViewById(R.id.act_login_btn_forgot_password).setOnClickListener(this);
        findViewById(R.id.act_login_btn_new_account).setOnClickListener(this);

        if (PreferencesManager.containsEmail(this) && PreferencesManager.containsPassword(this)) {
            mEdtEmail.setText(PreferencesManager.getEmail(this));
            mEdtPassword.setText(PreferencesManager.getPassword(this));
            attemptToLogIn();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.act_login_btn_log_in:
                if (CheckConnection.hasInternetConnection(this, true)) {
                    attemptToLogIn();
                }
                break;
            case R.id.act_login_btn_forgot_password:
            case R.id.act_login_btn_new_account:
                Toast.makeText(this, R.string.warning_feature_not_available, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void attemptToLogIn() {
        mEdtEmail.setError(null);
        mEdtPassword.setError(null);

        String email = mEdtEmail.getText().toString();
        String password = mEdtPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!FieldVerification.isEmailValid(email)) {
            cancel = true;
            mEdtEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEdtEmail;
        }

        if (!FieldVerification.isPasswordValid(password)) {
            cancel = true;
            mEdtPassword.setError(getString(R.string.error_invalid_password));
            focusView = mEdtEmail;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            /**
             * Once the API is implemented, the login request will be called and treated.
             * For now, we assume a successful login
             **/
            onLoginSuccessful();
        }

    }

    private void onLoginSuccessful () {
        PreferencesManager.setEmail(this, mEdtEmail.getText().toString());
        PreferencesManager.setPassword(this, mEdtPassword.getText().toString());

        if (!PreferencesManager.containsUser(this)) {
            //Fake User
            User user = new User();
            user.setId(0);
            user.setName("Test User");
            PreferencesManager.setUser(this, user);
        }

        startActivity(new Intent(this, MainActivity.class));
    }
}
