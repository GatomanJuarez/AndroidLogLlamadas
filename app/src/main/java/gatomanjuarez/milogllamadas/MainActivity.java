package gatomanjuarez.milogllamadas;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int CODIGO_SOLICITUD = 1;
    private Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Gestionar permiso.
    public void mostrarLlamads(View v){
        if(checarStatusPermisos()){
            consultarCPLlamadas();
        }
        else{
            solicitarPermisos();
        }
    }

    public void solicitarPermisos(){
        //Read Call Log
        //Write Call Log
        boolean solicitarPermisoRCL = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CALL_LOG);
        boolean solicitarPermisoWCL = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_CALL_LOG);
        if(solicitarPermisoRCL && solicitarPermisoWCL){
            Toast.makeText(MainActivity.this, "Los permisos fueron otorgados.", Toast.LENGTH_SHORT).show();
        }
        else{
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG}, CODIGO_SOLICITUD);
        }
    }

    public boolean checarStatusPermisos(){
        boolean permisoReadCallLog = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED;
        boolean permisoWriteCallLog = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED;
        if(permisoReadCallLog && permisoWriteCallLog){
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CODIGO_SOLICITUD:
                if(checarStatusPermisos()){
                    Toast.makeText(MainActivity.this, "Ya esta activos los permisos.", Toast.LENGTH_SHORT).show();
                    consultarCPLlamadas();
                }
                else{
                    Toast.makeText(MainActivity.this, "No estan activo alguno de los permisos.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    /////

    //Consultar un contenet provider.
    public void consultarCPLlamadas() {
        TextView tvLlamadas = (TextView) findViewById(R.id.tvLlamadas);
        tvLlamadas.setText("");
        Uri direccionLlamadas = CallLog.Calls.CONTENT_URI;

        //Numero de llamada, fecha, tipo, duracion.
        String [] campos = {
                CallLog.Calls.NUMBER,
                CallLog.Calls.DATE,
                CallLog.Calls.TYPE,
                CallLog.Calls.DURATION
        };
        ContentResolver contentResolver = getContentResolver();
        Cursor registros = contentResolver.query(direccionLlamadas, campos, null, null, CallLog.Calls.DATE + "DESC");

        while(registros.moveToNext()){
            //Obtenemos los datos a partir del indice de la columna.
            String numero = registros.getString(registros.getColumnIndex(campos[0]));
            Long fecha =  registros.getLong(registros.getColumnIndex(campos[1]));
            int tipo = registros.getInt(registros.getColumnIndex(campos[2]));
            String duracion = registros.getString(registros.getColumnIndex(campos[3]));

            String tipoDeLlamada = "";
            //Validacion de la llamada.
            switch (tipo){
                case CallLog.Calls.INCOMING_TYPE:
                    tipoDeLlamada = getResources().getString(R.string.llamada_entrada);
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    tipoDeLlamada = getResources().getString(R.string.llamada_perdida);
                    break;

                case CallLog.Calls.OUTGOING_TYPE:
                    tipoDeLlamada = getResources().getString(R.string.llamada_salida);
                    break;

                default:
                    tipoDeLlamada = getResources().getString(R.string.llamada_desconocida);
            }

            String detalleLlamadas = getResources().getString(R.string.numero_etiqueta) + numero + "\n" + getResources().getString(R.string.fecha_etiqueta) + DateFormat.format("dd/mm/yy k:mm", fecha)
                    + "\n" + getResources().getString(R.string.tipo_etiqueta) + tipoDeLlamada + "\n"+  getResources().getString(R.string.duracion_etiqueta) + duracion + "s.";

            tvLlamadas.append(detalleLlamadas);
        }
    }
}
