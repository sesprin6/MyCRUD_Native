package psi.tugas.mycrud_native;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.mycrud.library.Cfg_Pegawai;
import com.mycrud.library.GlobalUtils;
import com.mycrud.library.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Employee_Show_All extends AppCompatActivity implements ListView.OnItemClickListener
{
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_show_all);

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        getJSON();
    }

    //region Methods
    private void showEmployee(String json_data)
    {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        try
        {
            JSONArray result = new JSONObject(json_data).getJSONArray(Cfg_Pegawai.TAG_JSON);

            for (int i = 0; i < result.length(); i++)
            {
                JSONObject obj = result.getJSONObject(i);

                HashMap<String, String> employee = new HashMap<>();
                employee.put(Cfg_Pegawai.ID, obj.getString(Cfg_Pegawai.ID));
                employee.put(Cfg_Pegawai.NAME, obj.getString(Cfg_Pegawai.NAME));

                list.add(employee);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        ListAdapter adapter = new SimpleAdapter(Employee_Show_All.this, list, R.layout.list_item, new String[] {Cfg_Pegawai.ID, Cfg_Pegawai.NAME}, new int[] {R.id.id, R.id.name});
        listView.setAdapter(adapter);
    }

    @SuppressWarnings("deprecation")
    private void getJSON()
    {
        class Task extends AsyncTask<Void, Void, String>
        {
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                GlobalUtils.GProgressDialog.show(Employee_Show_All.this, "Mengambil data", "Mohon tunggu...");
            }

            @Override
            protected String doInBackground(Void... voids)
            {
                return RequestHandler.sendGetRequest(Cfg_Pegawai.URL_GET_ALL);
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
    //endregion

    //region Events
    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        Intent intent = new Intent(this, Employee_Show.class);
        HashMap<String, String> map = (HashMap<String, String>) adapterView.getItemAtPosition(i);
        String id = map.get(Cfg_Pegawai.ID);
        intent.putExtra(Cfg_Pegawai.TAG_ID, id);
        startActivity(intent);
    }
    //endregion
}
