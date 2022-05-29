package mx.tecnm.ittepic.ladm_u4_practica1_mensajesrealtime_carlosurielfregosoespericueta

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class History(var celphone:String? = null, var message :String? = null, val date:String?= null, val hour:String?=null)