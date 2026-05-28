package swust.edu.lab2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import swust.edu.lab2.R;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder> {

    private Context context;
    private List<Integer> avatarList;
    private int selectedPosition = 0;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position, int avatarResId);
    }

    public AvatarAdapter(Context context, List<Integer> avatarList, OnItemClickListener listener) {
        this.context = context;
        this.avatarList = avatarList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AvatarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_avatar, parent, false);
        return new AvatarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvatarViewHolder holder, int position) {
        int avatarResId = avatarList.get(position);
        holder.ivAvatar.setImageResource(avatarResId);
        holder.ivAvatar.setTag(position);
        
        if (position == selectedPosition) {
            holder.ivAvatar.setBorderColor(context.getResources().getColor(R.color.primary));
            holder.ivAvatar.setBorderWidth(4);
        } else {
            holder.ivAvatar.setBorderColor(context.getResources().getColor(R.color.white));
            holder.ivAvatar.setBorderWidth(2);
        }

        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                selectedPosition = adapterPosition;
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onItemClick(adapterPosition, avatarList.get(adapterPosition));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return avatarList.size();
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public static class AvatarViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivAvatar;

        public AvatarViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
        }
    }
}