import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firestore = FirebaseFirestore.getInstance()

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        taskAdapter = TaskAdapter()
        recyclerView.adapter = taskAdapter

        val taskInput: EditText = findViewById(R.id.taskInput)
        val addButton: Button = findViewById(R.id.addButton)

        addButton.setOnClickListener {
            val task = taskInput.text.toString()
            if (task.isNotEmpty()) {
                val taskData = hashMapOf("text" to task, "completed" to false)
                firestore.collection("tasks").add(taskData)
                    .addOnSuccessListener { 
                        taskInput.text.clear()
                        Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        firestore.collection("tasks").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("Firestore", "Listen failed.", e)
                return@addSnapshotListener
            }
            val tasks = snapshot?.documents?.map { doc ->
                Task(doc.id, doc.getString("text") ?: "", doc.getBoolean("completed") ?: false)
            } ?: emptyList()
            taskAdapter.submitList(tasks)
        }
    }
}
