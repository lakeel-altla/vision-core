package com.lakeel.altla.android.binding.sample;

import com.lakeel.altla.android.binding.Binder;
import com.lakeel.altla.android.binding.BooleanProperty;
import com.lakeel.altla.android.binding.IntProperty;
import com.lakeel.altla.android.binding.ObjectProperty;
import com.lakeel.altla.android.binding.RelayCommand;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;

public final class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private final ViewModel viewModel = new ViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Binder binder = new Binder(this);

        binder.radioGroup(R.id.radio_group_button).checked(viewModel.radioGroupChecked).bind();
        binder.view(R.id.button_on_click).click(viewModel.commandClick).bind();

        binder.textView(R.id.text_view_set_text).text(viewModel.textViewText).bind();
        binder.view(R.id.button_set_text).click(viewModel.commandTextViewText).bind();
        binder.view(R.id.button_clear_text).click(viewModel.commandClearTextViewText).bind();

        binder.editText(R.id.edit_text).text(viewModel.editTextText).bind();
        binder.textView(R.id.text_view_edit_text_result).text(viewModel.editTextText).bind();
        binder.view(R.id.button_edit_text_clear_text).click(viewModel.commandClearEditTextText).bind();

        binder.view(R.id.text_view_on_long_click).longClick(viewModel.commandLongClick).bind();

        binder.compoundButton(R.id.toggle_button).checked(viewModel.toggleButtonChecked).bind();
        binder.textView(R.id.text_view_toggle_button_result).text(viewModel.toggleButtonChecked)
              .converter(value -> value == null ? null : value.toString(), null)
              .bind();
        binder.view(R.id.edit_text_toggle_enabled).enabled(viewModel.toggleButtonChecked).bind();
    }

    private final class ViewModel {

        IntProperty radioGroupChecked = new IntProperty() {

            int value = R.id.radio_button_button_disabled;

            @Override
            public int get() {
                return value;
            }

            @Override
            public void set(int value) {
                if (this.value != value) {
                    this.value = value;
                    raiseOnValueChanged();
                    commandClick.raiseOnCanExecuteChanged();
                }
            }
        };

        RelayCommand commandClick = new RelayCommand(
                () -> Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show(),
                () -> radioGroupChecked.get() == R.id.radio_button_button_enabled);

        ObjectProperty<String> textViewText = new ObjectProperty<String>() {

            String value;

            @Nullable
            @Override
            public String get() {
                return value;
            }

            @Override
            public void set(@Nullable String value) {
                if (!Objects.equals(this.value, value)) {
                    this.value = value;
                    raiseOnValueChanged();
                }
            }
        };

        RelayCommand commandTextViewText = new RelayCommand(() -> textViewText.set("Text was set."));

        RelayCommand commandClearTextViewText = new RelayCommand(() -> textViewText.set(null));

        ObjectProperty<String> editTextText = new ObjectProperty<String>() {

            String value;

            @Nullable
            @Override
            public String get() {
                return value;
            }

            @Override
            public void set(@Nullable String value) {
                if (!Objects.equals(this.value, value)) {
                    Log.v(TAG, "set: " + value);
                    this.value = value;
                    raiseOnValueChanged();
                }
            }
        };

        RelayCommand commandClearEditTextText = new RelayCommand(() -> editTextText.set(null));

        RelayCommand commandLongClick = new RelayCommand(
                () -> Toast.makeText(MainActivity.this, "longClick", Toast.LENGTH_SHORT).show());

        BooleanProperty toggleButtonChecked = new BooleanProperty() {

            boolean value;

            @Override
            public boolean get() {
                return value;
            }

            @Override
            public void set(boolean value) {
                if (this.value != value) {
                    this.value = value;
                    raiseOnValueChanged();
                }
            }
        };
    }
}
