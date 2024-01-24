package com.google.codelabs.buildyourfirstmap

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.codelabs.buildyourfirstmap.classes.EventLevel
import com.google.codelabs.buildyourfirstmap.classes.EventManager
import com.google.codelabs.buildyourfirstmap.classes.GameItem
import com.google.codelabs.buildyourfirstmap.classes.GameItemArmor
import com.google.codelabs.buildyourfirstmap.classes.GameItemWeapon
import com.google.codelabs.buildyourfirstmap.classes.PlayerCharacter
import com.google.codelabs.buildyourfirstmap.classes.User
import com.google.codelabs.buildyourfirstmap.database.MongoDBManager
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var googleMap: GoogleMap? = null
    private var currentLatLng: LatLng? = null
    private var currentUser: User? = null
    private var currentCharacter: PlayerCharacter? = null
    private var inRaid = false
    private var em: EventManager? = null
    private var currentZoneLevel: EventLevel = EventLevel.NEUTRAL
    private val mongoDBManager = MongoDBManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        currentUser = intent.getSerializableExtra("user") as User
        currentCharacter = intent.getSerializableExtra("character") as PlayerCharacter
        em = EventManager(currentCharacter!!)

        supportActionBar?.hide()

        // Check for location permissions and request if not granted
        if (areLocationPermissionsGranted()) {
            initMap()
            requestLocationUpdates()
        } else {
            requestLocationPermissions()
        }

        val goToRaid: Button = findViewById(R.id.button_start)
        val statsButton: ImageButton = findViewById(R.id.stats_button)
        val inventoryButton: ImageButton = findViewById(R.id.inventory_button)
        goToRaid.setOnClickListener {
            if(currentCharacter?.isKnocked == false && em?.inBattle == false){
                inRaid = !inRaid
                val colorResId = if (inRaid) R.color.rog else R.color.orange
                goToRaid.setBackgroundColor(ContextCompat.getColor(this, colorResId))
            } else if (em?.inBattle == true) {
                Toast.makeText(this, "You can't run away from battle", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "woops", Toast.LENGTH_LONG).show()
            }
        }
        statsButton.setOnClickListener {
            val intent = Intent(this@MapActivity, StatsActivity::class.java)
            intent.putExtra("playerCharacter", currentCharacter)
            startActivity(intent)
        }
        inventoryButton.setOnClickListener {
            val intent = Intent(this@MapActivity, InventoryActivity::class.java)
            intent.putExtra("playerCharacter", currentCharacter)
            startActivityForResult(intent, INVENTORY_REQUEST_CODE)
        }
    }
    private fun updateZoneLocation() {
        for (zone in infectionZones) {
            if (isInCollision(currentLatLng!!, zone)) {
                currentZoneLevel = zone.tag as EventLevel
                em?.updateLocationLevel(currentZoneLevel)
                return
            }
        }
        currentZoneLevel = EventLevel.NEUTRAL
        em?.updateLocationLevel(currentZoneLevel)
    }

    private fun initMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync { map ->
            googleMap = map
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestLocationPermissions()
            }
            googleMap?.isMyLocationEnabled = true
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            requestLocationUpdates()
        }
    }

    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(75000)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == INVENTORY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val healAmount = data?.getIntExtra("healAmount", 0) ?: 0
            val removedHealItem = data?.getSerializableExtra("removedItem") as? GameItem
            val equippedWeapon = data?.getSerializableExtra("equippedWeapon") as? GameItem
            val equippedArmor = data?.getSerializableExtra("equippedArmor") as? GameItem
            currentCharacter?.changeHealth(healAmount)

            if (removedHealItem != null) {
                val matchingItem = currentCharacter?.inventory?.find { it.name == removedHealItem.name }
                matchingItem?.let { currentCharacter?.inventory?.remove(it) }
                Toast.makeText(this, "You used ${removedHealItem.name}", Toast.LENGTH_LONG).show()
            }

            if (equippedWeapon != null) {
                val matchingItem = currentCharacter?.inventory?.find { it.name == equippedWeapon.name }
                matchingItem?.let { currentCharacter?.inventory?.remove(it) }
                currentCharacter?.weapon?.let { currentCharacter?.inventory?.add(it) }
                currentCharacter?.weapon = matchingItem as GameItemWeapon?
                currentCharacter?.updateDamage()
                Toast.makeText(this, "You equipped ${equippedWeapon.name}", Toast.LENGTH_LONG).show()
            }

            if (equippedArmor != null) {
                val matchingItem = currentCharacter?.inventory?.find { it.name == equippedArmor.name }
                matchingItem?.let { currentCharacter?.inventory?.remove(it) }
                currentCharacter?.armor?.let { currentCharacter?.inventory?.add(it) }
                currentCharacter?.armor = matchingItem as GameItemArmor?
                Toast.makeText(this, "You equipped ${equippedArmor.name}", Toast.LENGTH_LONG).show()
            }

        }

    }



    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                onLocationChanged(location)
            }
        }
    }

    private fun onLocationChanged(location: Location) {
        currentLatLng = LatLng(location.latitude, location.longitude)
        val eventText: TextView = findViewById(R.id.generator_text)

        if (inRaid && currentCharacter?.isKnocked == false) {
            updateZoneLocation()
            eventText.text = em?.generateRandomEvent()
        }
        else {
            if (currentCharacter?.isKnocked == true) {
                currentCharacter?.changeHealth(1)
                eventText.text = getString(R.string.healing)
                if (currentCharacter?.currentHealth == currentCharacter?.getMaxHealth()) {
                    currentCharacter!!.isKnocked = false
                }
            }
            else {
                eventText.text = getString(R.string.NotRaid)
            }
        }
        val updateCharacterAsyncTask = UpdateCharacterAsyncTask(mongoDBManager)
        updateCharacterAsyncTask.execute(currentCharacter!!)


        if (googleMap != null) {
            onMapReady(googleMap!!)
        }
    }

    private fun areLocationPermissionsGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                initMap()
            } else {
                // TODO: Handle the case where permissions are not granted
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        const val INVENTORY_REQUEST_CODE = 1
    }

    private val infectionZones = mutableListOf<Circle>()

    override fun onMapReady(p0: GoogleMap) {
        if (currentLatLng != null) {
            for (i in 1..5) {
                val randomLatLng = generateRandomLatLng(currentLatLng!!)
                val radius = 100.0
                val level = generateRandomEventLevel()

                val circleOptions = CircleOptions()
                    .center(randomLatLng)
                    .radius(radius)
                    .strokeWidth(2f)
                    .strokeColor(getColorForLevel(level))

                val fillColor = getColorForLevel(level) and 0x00FFFFFF or (0x40 shl 24)
                circleOptions.fillColor(fillColor)

                val circle = googleMap?.addCircle(circleOptions)
                circle?.tag = level

                circle?.let { infectionZones.add(it) }
            }
        }
    }

    private fun generateRandomEventLevel(): EventLevel {
        val random = Random()
        val levels = listOf(EventLevel.SAFE, EventLevel.DANGER, EventLevel.HARDCORE)
        return levels[random.nextInt(levels.size)]
    }

    private fun getColorForLevel(level: EventLevel): Int {
        return when (level) {
            EventLevel.SAFE -> Color.GREEN
            EventLevel.DANGER -> Color.YELLOW
            EventLevel.HARDCORE -> Color.RED
            else -> Color.BLACK // Default color
        }
    }



    private fun generateRandomLatLng(currentLatLng: LatLng): LatLng {
        val random = Random()

        // Определяем коэффициенты ограничения
        val latDeviation = 0.1
        val lngDeviation = 0.1

        // Генерируем случайные координаты в заданном диапазоне от текущей GPS-метки
        val lat = currentLatLng.latitude + (random.nextDouble() - 0.5) * latDeviation
        val lng = currentLatLng.longitude + (random.nextDouble() - 0.5) * lngDeviation

        return LatLng(lat, lng)
    }


    // Метод для проверки коллизии текущего местоположения с кругом
    private fun isInCollision(currentLatLng: LatLng, circle: Circle): Boolean {
        val distance = calculateDistance(currentLatLng, circle.center)
        return distance < circle.radius
    }


    // Метод для вычисления расстояния между двумя точками на карте
    private fun calculateDistance(point1: LatLng, point2: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            point1.latitude, point1.longitude,
            point2.latitude, point2.longitude,
            results
        )
        return results[0]
    }

    class UpdateCharacterAsyncTask(private val mongoDBManager: MongoDBManager) : AsyncTask<PlayerCharacter, Void, Unit>() {

        override fun doInBackground(vararg characters: PlayerCharacter) {
            // Выполните вашу асинхронную операцию здесь (например, добавление или обновление в базе данных)
            mongoDBManager.addOrUpdatePlayerCharacter(characters[0])
        }

        override fun onPostExecute(result: Unit?) {
            // Этот метод вызывается в основном потоке после завершения doInBackground
            // Здесь вы можете обновить UI, если это необходимо
        }
    }

}
