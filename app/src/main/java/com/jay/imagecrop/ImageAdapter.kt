import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jay.imagecrop.databinding.ItemImageBinding
import com.jay.imagecrop.model.Hit

class ImageAdapter(private var list: List<Hit>,private val listener: OnImageClickListener) :
    RecyclerView.Adapter<ImageAdapter.SavedViewHolder>() {

    private var filteredList: MutableList<Hit> = ArrayList()

    init {
        filteredList.addAll(list)
    }

    inner class SavedViewHolder(binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val image = binding.ivImage
        val title = binding.tvImageName

        fun bind(hit: Hit) {
            Glide.with(itemView.context).load(hit.largeImageURL).into(image)
            title.text = hit.tags
            image.setOnClickListener {
                listener.onImageClicked(Uri.parse(hit.largeImageURL))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemImageBinding.inflate(inflater, parent, false)
        return SavedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount(): Int = filteredList.size

 fun filter(query: String) {
        val previousSize = filteredList.size
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(list)
        } else {
            val lowerCaseQuery = query.lowercase()
            list.forEach { hit ->
                if (hit.tags.lowercase().contains(lowerCaseQuery)) {
                    filteredList.add(hit)
                }
            }
        }
        if (filteredList.size != previousSize) {
            notifyDataSetChanged()
        }
    }

    interface OnImageClickListener {
        fun onImageClicked(imageUri: Uri)
    }
}
