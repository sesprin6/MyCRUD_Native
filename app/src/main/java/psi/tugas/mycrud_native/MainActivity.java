package psi.tugas.mycrud_native;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mycrud.library.Cfg_Pegawai;
import com.mycrud.library.Cfg_Posisi;
import com.mycrud.library.GlobalUtils;
import com.mycrud.library.Obj_Position;
import com.mycrud.library.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener
{
    private EditText editText_Name;
    private EditText editText_Salary;

    private Spinner spinner_Position;

    private Button button_Add;
    private Button button_View;
    private Button button_List_Pos;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GlobalUtils.GDevice.scale = getResources().getDisplayMetrics().density;

        editText_Name = findViewById(R.id.editText_Name);
        editText_Salary = findViewById(R.id.editText_Salary);

        spinner_Position = findViewById(R.id.spinner_Position);

        button_Add = findViewById(R.id.button_Add);
        button_View = findViewById(R.id.button_View);
        button_List_Pos = findViewById(R.id.button_List_Pos);

        button_Add.setOnClickListener(this);
        button_View.setOnClickListener(this);
        button_List_Pos.setOnClickListener(this);

        spinner_Position.setOnItemSelectedListener(this);
    }

    //region Methods
    @SuppressWarnings("deprecation")
    private void addEmployee()
    {
        if (spinner_Position.getSelectedItemPosition() == 0)
        {
            GlobalUtils.GToast.show(MainActivity.this, "Silahkan memilih posisi jabatan");
            return;
        }

        final String name = editText_Name.getText().toString().trim();
        final String salary = editText_Salary.getText().toString().trim();

        final String position = spinner_Position.getSelectedItem().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(position) || TextUtils.isEmpty(salary))
        {
            GlobalUtils.GToast.show(MainActivity.this, "Form tidak boleh kosong!");
            return;
        }

        class Task extends AsyncTask<Void, Void, String>
        {
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                GlobalUtils.GProgressDialog.show(MainActivity.this, "Menambahkan", "Mohon tunggu...");
            }

            @Override
            protected String doInBackground(Void... voids)
            {
                HashMap<String, String> params = new HashMap<>();
                params.put(Cfg_Pegawai.NAME, name);
                params.put(Cfg_Pegawai.POSITION, position);
                params.put(Cfg_Pegawai.SALARY, salary);

                return RequestHandler.sendPostRequest(Cfg_Pegawai.URL_ADD, params);
            }

            @Override
            protected void onPostExecute(String s)
            {
                super.onPostExecute(s);
                GlobalUtils.GProgressDialog.dismiss();
                GlobalUtils.GToast.show(MainActivity.this, s);
            }
        }

        new Task().execute();
    }

    private void initializeList(String json_data) throws JSONException
    {
        List<Obj_Position> list = new ArrayList<>();
        list.add(new Obj_Position("NULL", "Pilih posisi"));

        JSONArray result = new JSONObject(json_data).getJSONArray(Cfg_Pegawai.TAG_JSON);

        for (int i = 0; i < result.length(); i++)
            list.add(new Obj_Position(result.getJSONObject(i).getString(Cfg_Pegawai.ID), result.getJSONObject(i).getString(Cfg_Pegawai.POSITION)));

        ArrayAdapter<Obj_Position> adapter = new ArrayAdapter<Obj_Position>(MainActivity.this, android.R.layout.simple_list_item_1, list)
        {
            @Override
            public boolean isEnabled(int position)
            {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
            {
                View view = super.getDropDownView(position, convertView, parent);
                TextView text = (TextView) view;

                if (position == 0)
                {
                    text.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                    text.setTextColor(Color.GRAY);
                    text.setTypeface(null, Typeface.BOLD);
                    text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                else
                {
                    text.setTextColor(Color.BLACK);
                    text.setPadding(GlobalUtils.GDevice.getPixel(16), 0, GlobalUtils.GDevice.getPixel(16), 0);
                }

                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_Position.setAdapter(adapter);
    }

    @SuppressWarnings("deprecation")
    private void loadPositions()
    {
        class Task extends AsyncTask<Void, Void, String>
        {
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                GlobalUtils.GProgressDialog.show(MainActivity.this, "Memuat data posisi", "Mohon tunggu...");
            }

            @Override
            protected String doInBackground(Void... voids)
            {
                return RequestHandler.sendGetRequest(Cfg_Posisi.URL_GET_ALL);
            }

            @Override
            protected void onPostExecute(String s)
            {
                super.onPostExecute(s);
                GlobalUtils.GProgressDialog.dismiss();
                try {initializeList(s);} catch (Exception ignored) {}
            }
        }

        new Task().execute();
    }
    //endregion

    //region Events
    @Override
    protected void onResume()
    {
        super.onResume();
        loadPositions();
    }

    @Override
    public void onClick(View view)
    {
        if (view == button_Add)
            addEmployee();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        if (spinner_Position.getSelectedItemPosition() == 0)
            return;

        final Obj_Position position = (Obj_Position) spinner_Position.getSelectedItem();

        class Task extends AsyncTask<Void, Void, String>
        {
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                GlobalUtils.GProgressDialog.show(MainActivity.this, "Memperbarui data gaji", "Mohon tunggu...");
            }

            @Override
            protected String doInBackground(Void... voids)
            {
                return RequestHandler.sendGetRequest(Cfg_Posisi.URL_GET + position.getId());
            }

            @Override
            protected void onPostExecute(String s)
            {
                super.onPostExecute(s);
                GlobalUtils.GProgressDialog.dismiss();

                try
                {
                    JSONArray result = new JSONObject(s).getJSONArray(Cfg_Posisi.TAG_JSON);
                    editText_Salary.setText(result.getJSONObject(0).getString(Cfg_Posisi.SALARY));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }

        new Task().execute();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }
    //endregion
}