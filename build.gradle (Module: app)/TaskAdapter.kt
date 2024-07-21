import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

data class Task(val id: String, val text: String, val completed: Boolean)

class TaskAdapter : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(R.id.textView)
        private val checkBox: CheckBox = view.findViewById(R.id.checkBox)
        private val deleteButton: Button = view.findViewById(R.id.deleteButton)

        fun bind(task: Task) {
            textView.text = task.text
            checkBox.isChecked = task.completed
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                FirebaseFirestore.getInstance().collection("tasks").document(task.id).update("completed", isChecked)
            }
            deleteButton.setOnClickListener {
                FirebaseFirestore.getInstance().collection("tasks").document(task.id).delete()
            }
        }
    }
}
