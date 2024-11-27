
import com.alcaldiasantaananorte.nortegomotorista.model.datos.Driver
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class DriverProvider {

    val db = Firebase.firestore.collection("Drivers")

    fun create(driver: Driver): Task<Void>{
        return db.document(driver.id!!).set(driver)
    }

}