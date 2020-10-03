package psi.tugas.mycrud_native;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class Employee_Show extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener
{
    private EditText editText_Id;
    private EditText editText_Name;
    private EditText editText_Salary;

    private Spinner spinner_Position;

    private Button button_Update;
    private Button button_Delete;

    private String initial_position;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_show);

        editText_Id = findViewById(R.id.editText_Id);
        editText_Name = findViewById(R.id.editText_Name);
        editText_Salary = findViewById(R.id.editText_Salary);

        spinner_Position = findViewById(R.id.spinner_Position);

        button_Update = findViewById(R.id.button_Update);
        button_Delete = findViewById(R.id.button_Delete);

        spinner_Position.setOnItemSelectedListener(this);

        button_Update.setOnClickListener(this);
        button_Delete.setOnClickListener(this);

        editText_Id.setText(getIntent().getStringExtra(Cfg_Pegawai.TAG_ID));

        getEmployee();
    }

    //region Methods
    @SuppressWarnings("deprecation")
    private void getEmployee()
    {
        class Task extends AsyncTask<Void, Void, String>
        {
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                GlobalUtils.GProgressDialog.show(Employee_Show.this, "Memuat data", "Mohon tunggu...");
            }

            @Override
            protected String doInBackground(Void... voids)
            {
                return RequestHandler.sendGetRequest(Cfg_Pegawai.URL_GET + editText_Id.getText());
            }

            @Override
            protected void onPostExecute(String s)
            {
                super.onPostExecute(s);
                GlobalUtils.GProgressDialog.dismiss();
                showEmployee(s);
            }
        }

        new Task().execute();
    }

    private void showEmployee(String json_data)
    {
        try
        {
            JSONArray result = new JSONObject(json_data).getJSONArray(Cfg_Pegawai.TAG_JSON);
            JSONObject data = result.getJSONObject(0);

            editText_Name.setText(data.getString(Cfg_Pegawai.NAME));
            editText_Salary.setText(data.getString(Cfg_Pegawai.SALARY));

            initial_position = data.getString(Cfg_Pegawai.POSITION);

            loadPositions();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void initializeList(String json_data) throws JSONException
    {
        List<Obj_Position> list = new ArrayList<>();
        list.add(new Obj_Position("NULL", initial_position));

        JSONArray result = new JSONObject(json_data).getJSONArray(Cfg_Pegawai.TAG_JSON);

        for (int i = 0; i < result.length(); i++)
            list.add(new Obj_Position(result.getJSONObject(i).getString(Cfg_Pegawai.ID), result.getJSONObject(i).getString(Cfg_Pegawai.POSITION)));

        ArrayAdapter<Obj_Position> adapter = new ArrayAdapter<Obj_Position>(Employee_Show.this, android.R.layout.simple_list_item_1, list)
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
                    text.setTextColor(Color.GRAY);
                }
                else
                {
                    text.setTextColor(Color.BLACK);
                }
                text.setPadding(GlobalUtils.GDevice.getPixel(16), 0, GlobalUtils.GDevice.getPixel(16), 0);

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
                GlobalUtils.GProgressDialog.show(Employee_Show.this, "Memuat data posisi", "Mohon tunggu...");
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

    @SuppressWarnings("deprecation")
    private void updateEmployee()
    {
        final String id = editText_Id.getText().toString();
        final String name = editText_Name.getText().toString();
        final String position = spinner_Position.getSelectedItem().toString();
        final String salary = editText_Salary.getText().toString();

        class Task extends AsyncTask<Void, Void, String>
        {
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                GlobalUtils.GProgressDialog.show(Employee_Show.this, "Memperbarui data", "Mohon tunggu...");
            }

            @Override
            protected String doInBackground(Void... voids)
            {
                HashMap<String, String> map = new HashMap<>();
                map.put(Cfg_Pegawai.ID, id);
                map.put(Cfg_Pegawai.NAME, name);
                map.put(Cfg_Pegawai.POSITION, position);
                map.put(Cfg_Pegawai.SALARY, salary);

                return RequestHandler.sendPostRequest(Cfg_Pegawai.URL_UPDATE, map);
            }

            @Override
            protected void onPostExecute(String s)
            {
                super.onPostExecute(s);
                GlobalUtils.GProgressDialog.dismiss();
                GlobalUtils.GToast.show(Employee_Show.this, s);
                finish();
            }
        }

        new Task().execute();
    }

    @SuppressWarnings("deprecation")
    private void deleteEmployee()
    {
        class Task extends AsyncTask<Void, Void, String>
        {
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                GlobalUtils.GProgressDialog.show(Employee_Show.this, "Menghapus data", "Mohon tunggu...");
            }

            @Override
            protected String doInBackground(Void... voids)
            {
                return RequestHandler.sendGetRequest(Cfg_Pegawai.URL_DELETE + editText_Id.getText());
            }

            @Override
            protected void onPostExecute(String s)
            {
                super.onPostExecute(s);
                GlobalUtils.GProgressDialog.dismiss();
                GlobalUtils.GToast.show(Employee_Show.this, s);
                finish();
            }
        }

        new Task().execute();
    }

    private void confirmDeleteEmployee()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah Anda yakin ingin menghapus pegawai ini?");

        builder.setPositiveButton("Ya",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        deleteEmployee();
                    }
                });
        builder.setNegativeButton("Tidak",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //endregion

    //region Events
    @Override
    public void onClick(View view)
    {
        if (view == button_Update)
            updateEmployee();
        else if (view == button_Delete)
            confirmDeleteEmployee();
    }

    @SuppressWarnings("deprecation")
    @Override
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
                GlobalUtils.GProgressDialog.show(Employee_Show.this, "Memperbarui data gaji", "Mohon tunggu...");
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
        //The interface requires it. No further implementation needed.
    }
    //endregion
}
