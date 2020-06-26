package me.vojinpuric.SosAppServer

data class LastLocation(var lat:Double =0.0, var lon:Double=0.0){
    constructor():this(0.0,0.0)
}
data class SosUser(val id: String, var lastLocation: LastLocation)