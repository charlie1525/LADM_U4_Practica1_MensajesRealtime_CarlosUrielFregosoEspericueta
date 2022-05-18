package mx.tecnm.ittepic.ladm_u4_practica1_mensajesrealtime_carlosurielfregosoespericueta

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import mx.tecnm.ittepic.ladm_u4_practica1_mensajesrealtime_carlosurielfregosoespericueta.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {// fin de la clase
    lateinit var binding: ActivityMainBinding
    private val receivedPermission = 1
    private val sendPermission = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Mensajeria con RealTime"

        if (ActivityCompat.checkSelfPermission
                (this,android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)
                {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.RECEIVE_SMS),
                    receivedPermission
            )
        }// fin del cuerpo del If

        binding.btnEnviar.setOnClickListener {
            if (ActivityCompat.checkSelfPermission
                    (this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
                    {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.SEND_SMS),
                        sendPermission
                )
            }// fin del cuerpo del If
            else {
                enviaSMS(binding.txtTelefono.text.toString(), binding.txtMensaje.text.toString())
                binding.txtMensaje.text.clear()
                binding.txtTelefono.text.clear()
            }
        }


    }// fin del onCreate

    override fun onRequestPermissionsResult( requestCode: Int,permissions: Array<out String>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == sendPermission) mensaje("Permisos de envio concedidos")
        if(requestCode == receivedPermission) mensaje("Permisos de recepcion concedidos")
    }

    private fun enviaSMS(telefono: String, mensaje: String) {
        SmsManager.getDefault().sendTextMessage(telefono,null,mensaje,null,null)
        mensaje("Mensaje enviado con éxito")
    }

    private fun mensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun alerta(mensaje: String) {
        AlertDialog.Builder(this)
            .setMessage(mensaje)
            .setPositiveButton("ok") { d, i -> }
            .show()
    }


}