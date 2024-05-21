package com.anafthdev.story.foundation.util

import com.google.android.gms.maps.model.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object DistanceUtil {

    private const val EARTH_RADIUS_KM = 6_371.0 // Radius bumi dalam kilometer

    /**
     * Mencari jarak dari dua titik berdasarkan garis lintang dan bujur pada bumi
     *
     * Haversine formula:
     *
     * ```
     * - a = sin²(Δφ/2) + cos φ1 ⋅ cos φ2 ⋅ sin²(Δλ/2)
     * - c = 2 ⋅ atan2(√a, √(1−a))
     * - d = R ⋅ c
     *
     * φ is latitude, λ is longitude, R is earth radius (mean radius = 6,371km). note that angles need to be in radians to pass to trig functions!
     * ```
     *
     * @see <a href="http://www.movable-type.co.uk/scripts/latlong.html">Sumber</a>
     */
    private fun haversine(latitude1: Double, longitude1: Double, latitude2: Double, longitude2: Double): Double {
        var lat1 = latitude1
        var lat2 = latitude2
        val deltaLat = Math.toRadians(lat2 - lat1)
        val deltaLon = Math.toRadians(longitude2 - longitude1)

        lat1 = Math.toRadians(lat1)
        lat2 = Math.toRadians(lat2)

        val a = sin(deltaLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(deltaLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }

    /**
     * Berfungsi untuk mencari titik terjauh dari beberapa titik
     */
    fun findFurthestPoints(locations: List<LatLng>): Pair<LatLng?, LatLng?> {
        var maxDistance = 0.0
        var point1: LatLng? = null
        var point2: LatLng? = null

        for (i in locations.indices) {
            for (j in i + 1 until locations.size) {
                val distance = haversine(
                    locations[i].latitude,
                    locations[i].longitude,
                    locations[j].latitude,
                    locations[j].longitude
                )

                if (distance > maxDistance) {
                    maxDistance = distance
                    point1 = locations[i]
                    point2 = locations[j]
                }
            }
        }

        return point1 to point2
    }
}
