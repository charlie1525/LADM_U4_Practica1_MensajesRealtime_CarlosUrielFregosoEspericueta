package mx.tecnm.ittepic.ladm_u4_practica1_mensajesrealtime_carlosurielfregosoespericueta

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras
        if (extras != null) {
            val sms = extras.get("pdus") as Array<*>
            for (it in sms.indices) {
                val formato =extras.getString("format")
                val smsMensaje =SmsMessage.createFromPdu(sms[it] as ByteArray,formato)
            }// fin del ciclo for
        }// fin del if para ver si los extras son nulos
    }// fin del método onReceiver

}// fin de la clase