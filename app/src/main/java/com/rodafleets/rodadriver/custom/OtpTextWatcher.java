package com.rodafleets.rodadriver.custom;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class OtpTextWatcher implements TextWatcher {

    private EditText editText;
    private EditText editTextToFocus;

    public OtpTextWatcher(EditText editText, EditText editTextToFocus) {
        this.editText = editText;
        this.editTextToFocus = editTextToFocus;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (editText.getText().toString().length() == 1){     //size as per your requirement
            editTextToFocus.requestFocus();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void afterTextChanged(Editable s) {}

}
