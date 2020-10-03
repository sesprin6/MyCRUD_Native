package psi.tugas.mycrud_native;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mycrud.library.Cfg_Posisi;
import com.mycrud.library.GlobalUtils;
import com.mycrud.library.RequestHandler;

import java.util.HashMap;

public class MainActivity_Position extends AppCompatActivity implements View.OnClickListener
{
    private EditText editText_Position;
    private EditText editText_Salary;

    private Button button_Add;
    private Button button_View;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_position);

        editText_Position = findViewById(R.id.editText_Position);
        editText_Salary = findViewById(R.id.editText_Salary);

        button_Add = findViewById(R.id.button_Add);
        button_View = findViewById(R.id.button_View);

        button_Add.setOnClickListener(this);
        button_View.setOnClickListener(this);
    }

    //region Methods
    @SuppressWarnings("deprecation")
    private void addPosition()
    {
        final String position = editText_Position.getText().toString().trim();
        final String salary = editText_Salary.getText().toString().trim();

        if (TextUtils.isEmpty(position) || TextUtils.isEmpty(salary))
        {
            GlobalUtils.GToast.show(MainActivity_Position.this, "Form tidak boleh kosong");
            return;
        }

        class Task extends AsyncTask<Void, Void, String>
        {
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                GlobalUtils.GProgressDialog.show(MainActivity_Position.this, "Menambahkan", "Mohon tunggu...");
            }

            @Override
            protected String doInBackground(Void... voids)
            {
                HashMap<String, String> params = new HashMap<>();
                params.put(Cfg_Posisi.POSITION, position);
                params.put(Cfg_Posisi.SALARY, salary);

                return RequestHandler.sendPostRequest(Cfg_Posisi.URL_ADD, params);
            }

            @Override
            protected void onPostExecute(String s)
            {
                super.onPostExecute(s);
                GlobalUtils.GProgressDialog.dismiss();
                GlobalUtils.GToast.show(MainActivity_Position.this, s);
            }
        }

        new Task().execute();
    }
    //endregion

    //region Events

    @Override
    public void onClick(View view)
    {
        if (view == button_Add)
            addPosition();
    }

    //endregion
}