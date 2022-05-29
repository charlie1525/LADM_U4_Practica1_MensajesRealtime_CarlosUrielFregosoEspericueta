package mx.tecnm.ittepic.ladm_u4_practica1_mensajesrealtime_carlosurielfregosoespericueta

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.telephony.SmsManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import mx.tecnm.ittepic.ladm_u4_practica1_mensajesrealtime_carlosurielfregosoespericueta.databinding.ActivityMainBinding
import java.io.*
import java.lang.Exception
import java.nio.file.Paths
import java.util.*

class MainActivity : AppCompatActivity() {// fin de la clase
    lateinit var binding: ActivityMainBinding
    private val receivedPermission = 1
    private val sendPermission = 1
    private val listaId = ArrayList<String>()
    private val calendar = GregorianCalendar.getInstance()
    private val date = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH)}/${calendar.get(Calendar.YEAR)}"
    private var hour = "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}:${calendar.get(Calendar.SECOND)}"
    private val arrMensaje = ArrayList<String>()
    private val arrReceptor = ArrayList<String>()
    private val arrHora = ArrayList<String>()
    private val datos = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Mensajeria con RealTime"

        //conexión a la base de RealTime
        val conection = FirebaseDatabase.getInstance().reference.child("historial")
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaId.clear()
                datos.clear()
                arrMensaje.clear()
                arrHora.clear()
                arrReceptor.clear()
                for (data in snapshot.children){
                    val id = data.key
                    listaId.add(id!!)
                    val telefono = data.getValue<History>()?.celphone
                    val mensaje = data.getValue<History>()?.message
                    val dia = data.getValue<History>()?.date
                    val hora = data.getValue<History>()?.hour
                    arrMensaje.add(mensaje!!)
                    arrReceptor.add(telefono!!)
                    arrHora.add("$dia,$hora")
                    datos.add("Receptor: $telefono\nMensaje: $mensaje\nDía: $dia, a las $hora")
                }// fin del for
                mostrarLista(datos)
            }// fin del OnDataChange

            override fun onCancelled(error: DatabaseError) {}
        }// fin del value event Listener
        conection.addValueEventListener(postListener)

        // inicio de la programacion de la actividad
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
                val mensajes = binding.txtMensaje.text
                val telefonos = binding.txtTelefono.text
                hour = "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}:${calendar.get(Calendar.SECOND)}"

                val bd = Firebase.database.reference
                val historialPersonal = History(telefonos.toString(), mensajes.toString(),date,hour)

                enviaSMS(telefonos.toString(), mensajes.toString())
                bd.child("historial").push().setValue(historialPersonal)
                    .addOnFailureListener {alerta("Error...\n${it.message}")}
                    .addOnSuccessListener {
                        mensaje("mensaje guardado")
                    }

                mensajes.clear()
                telefonos.clear()
            }
        } // fin del boton para enviar

        binding.btnExportar.setOnClickListener {
            guardarArchivo(listaId.size,arrReceptor,arrMensaje,arrHora)
            leerArchivo()
        }// fin del boton para exportar


    }// fin del onCreate

    private fun mostrarLista(datos: ArrayList<String>) {
        binding.lvContactos.adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,datos)
    }

    override fun onRequestPermissionsResult
                (requestCode: Int,permissions: Array<out String>,grantResults: IntArray)
    {
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
            .setPositiveButton("ok") { _, _ -> }
            .show()
    }

    private fun guardarArchivo(index:Int, receptor:ArrayList<String>, mensage:ArrayList<String>, hora:ArrayList<String>){
        try{
            val archivo = OutputStreamWriter(openFileOutput("MessageHistory.txt", MODE_PRIVATE))
            var cadena =""
            val header="Receptor,Mensage,Día,Hora\n"
            cadena = header
            for (it in 0 until index){ cadena += "${receptor[it]},${mensage[it]},${hora[it]}\n" }
            archivo.write(cadena)
            archivo.flush()
            archivo.close()

            mensaje("Archivo guaardado")
        } catch (a : Exception){ alerta("${a.message}") }


    }// fin del guardar archivos

    private fun leerArchivo() {
        try{
            val archivo = BufferedReader(InputStreamReader(openFileInput("MessageHistory.txt")))
            var cadena = ""
            archivo.forEachLine {
                cadena += " "+archivo.readText()
            }
            archivo.close()
            alerta(cadena)
           // alerta(fileOut.toString())
        } catch (a : java.lang.Exception){
            alerta("${a.message}\nError de lectura")
        }
    } // fin de la función
}