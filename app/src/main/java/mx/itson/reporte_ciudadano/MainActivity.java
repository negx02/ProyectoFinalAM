package mx.itson.reporte_ciudadano;

import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private ScrollView layoutReporte;
    private LinearLayout layoutContacto;
    private LinearLayout layoutExito;
    private BottomNavigationView bottomNavigation;

    private EditText etNombre, etDireccion, etCelular, etCorreo, etDescripcion;
    private Spinner spinnerColonia, spinnerTipoReporte;
    private Button btnSeleccionarImagen, btnEnviarReporte, btnVolverAFormulario;
    private ImageView ivEvidencia;
    private Uri imagenSeleccionadaUri;

    private Button btnMapa, btnCorreo, btnLlamar;

    private final ActivityResultLauncher<String> abrirGaleria = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        imagenSeleccionadaUri = uri;
                        ivEvidencia.setImageURI(uri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        layoutReporte = findViewById(R.id.layoutReporte);
        layoutContacto = findViewById(R.id.layoutContacto);
        layoutExito = findViewById(R.id.layoutExito);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        etNombre = findViewById(R.id.etNombre);
        etDireccion = findViewById(R.id.etDireccion);
        etCelular = findViewById(R.id.etCelular);
        etCorreo = findViewById(R.id.etCorreo);
        etDescripcion = findViewById(R.id.etDescripcion);
        spinnerColonia = findViewById(R.id.spinnerColonia);
        spinnerTipoReporte = findViewById(R.id.spinnerTipoReporte);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnEnviarReporte = findViewById(R.id.btnEnviarReporte);
        btnVolverAFormulario = findViewById(R.id.btnVolverAFormulario);
        ivEvidencia = findViewById(R.id.imagen);

        btnMapa = findViewById(R.id.btnMapa);
        btnCorreo = findViewById(R.id.btnCorreo);
        btnLlamar = findViewById(R.id.btnLlamar);

        ArrayAdapter<CharSequence> adapterColonias = ArrayAdapter.createFromResource(this,
                R.array.colonias_array, android.R.layout.simple_spinner_item);
        adapterColonias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColonia.setAdapter(adapterColonias);

        ArrayAdapter<CharSequence> adapterTipos = ArrayAdapter.createFromResource(this,
                R.array.tipos_reporte, android.R.layout.simple_spinner_item);
        adapterTipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoReporte.setAdapter(adapterTipos);

        btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirGaleria.launch("image/*");
            }
        });

        btnEnviarReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNombre.getText().toString();
                String direccion = etDireccion.getText().toString();
                String celular = etCelular.getText().toString();
                String correo = etCorreo.getText().toString();
                String descripcion = etDescripcion.getText().toString();
                String colonia = spinnerColonia.getSelectedItem().toString();
                String tipoReporte = spinnerTipoReporte.getSelectedItem().toString();

                if(nombre.isEmpty() || descripcion.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor llena los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject parametrosPost = new JSONObject();
                try {
                    parametrosPost.put("nombre_interesado", nombre);
                    parametrosPost.put("direccion", direccion);
                    parametrosPost.put("colonia", colonia);
                    parametrosPost.put("celular", celular);
                    parametrosPost.put("correo", correo);
                    parametrosPost.put("tipo", tipoReporte);
                    parametrosPost.put("descripcion", descripcion);
                    parametrosPost.put("imagen", JSONObject.NULL);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String urlServidor = "https://mcaconsultores.com.mx/apireporte/reporte.php";

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, urlServidor, parametrosPost,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                etNombre.setText("");
                                etDireccion.setText("");
                                etCelular.setText("");
                                etCorreo.setText("");
                                etDescripcion.setText("");
                                spinnerColonia.setSelection(0);
                                spinnerTipoReporte.setSelection(0);
                                ivEvidencia.setImageResource(R.drawable.imagen);
                                imagenSeleccionadaUri = null;

                                layoutReporte.setVisibility(View.GONE);
                                layoutContacto.setVisibility(View.GONE);
                                layoutExito.setVisibility(View.VISIBLE);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, "Error al enviar: " + error.toString(), Toast.LENGTH_LONG).show();
                            }
                        })
                {
                    @Override
                    public java.util.Map<String, String> getHeaders() {
                        java.util.Map<String, String> headers = new java.util.HashMap<>();
                        headers.put("Authorization", "Bearer a0f4dcad-5903-482f-8982-88ec8bc6156e");
                        return headers;
                    }
                };

                RequestQueue colaPeticiones = Volley.newRequestQueue(MainActivity.this);
                colaPeticiones.add(request);
            }
        });

        btnVolverAFormulario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutExito.setVisibility(View.GONE);
                layoutContacto.setVisibility(View.GONE);
                layoutReporte.setVisibility(View.VISIBLE);
            }
        });

        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:27.9193,-110.8976?q=Guaymas+Sonora");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                startActivity(mapIntent);
            }
        });

        btnCorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:atencion.ciudadana@guaymas.gob.mx"));
                startActivity(emailIntent);
            }
        });

        btnLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:6221694155"));
                startActivity(callIntent);
            }
        });

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                layoutExito.setVisibility(View.GONE);
                if (id == R.id.nav_reporte) {
                    layoutReporte.setVisibility(View.VISIBLE);
                    layoutContacto.setVisibility(View.GONE);
                    return true;
                } else if (id == R.id.nav_contacto) {
                    layoutReporte.setVisibility(View.GONE);
                    layoutContacto.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }
        });
    }
}