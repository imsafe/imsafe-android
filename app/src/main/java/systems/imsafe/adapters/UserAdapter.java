package systems.imsafe.adapters;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import systems.imsafe.R;
import systems.imsafe.models.User;
import systems.imsafe.restapi.ImSafeService;
import systems.imsafe.restapi.ServiceGenerator;

import static android.content.Context.MODE_PRIVATE;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private ImSafeService service;
    private ArrayList<User> users;
    private ArrayList<User> followingList;

    public UserAdapter(ArrayList<User> users, ArrayList<User> followingList) {
        this.users = users;
        this.followingList = followingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.user_item, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvUsernameSearch.setText(users.get(position).getUsername());
        SharedPreferences prefs = holder.btnFollow.getContext().getSharedPreferences("File", MODE_PRIVATE);
        String username = prefs.getString("username", "");

        if (users.get(position).getUsername().equals(username)) {
            holder.btnFollow.setVisibility(View.INVISIBLE);
        }

        for (User user : followingList) {
            if (user.getUsername().equals(users.get(position).getUsername())) {
                holder.btnFollow.setText("Unfollow");
            }
        }

        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestBody userId = RequestBody.create(users.get(position).getId().toString(), MediaType.parse("multipart/form-data"));
                boolean isFollowing = false;

                for (User following : followingList) {
                    if (following.getUsername().equals(users.get(position).getUsername())) {
                        isFollowing = true;
                        break;
                    }
                }

                if (isFollowing) {
                    unfollow(userId, v, holder);
                } else {
                    follow(userId, v, holder);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    private void follow(RequestBody userId, View v, ViewHolder holder) {
        Call<ResponseBody> call = service.follow(userId);
        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(v.getContext(), "Followed successfully", Toast.LENGTH_SHORT).show();
                    holder.btnFollow.setText("Unfollow");
                    getFollowingList();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {

            }
        });
    }

    private void unfollow(RequestBody userId, View v, ViewHolder holder) {
        Call<ResponseBody> call = service.unfollow(userId);
        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(v.getContext(), "Unfollowed successfully", Toast.LENGTH_SHORT).show();
                    holder.btnFollow.setText("Follow");
                    getFollowingList();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {

            }
        });
    }

    private void getFollowingList() {
        Call<ArrayList<User>> call = service.getFollowingList();
        call.enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(@NotNull Call<ArrayList<User>> call, @NotNull Response<ArrayList<User>> response) {
                if (response.isSuccessful()) {
                    followingList = response.body();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ArrayList<User>> call, @NotNull Throwable t) {

            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvUsernameSearch;
        Button btnFollow;

        @SuppressLint("SetTextI18n")
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            service = ServiceGenerator.createService(ImSafeService.class);
            imageView = itemView.findViewById(R.id.imageViewUser);
            tvUsernameSearch = itemView.findViewById(R.id.tv_username_search);
            btnFollow = itemView.findViewById(R.id.btn_follow);
        }
    }
}
