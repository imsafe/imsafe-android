package systems.imsafe.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import systems.imsafe.R;
import systems.imsafe.adapters.UserAdapter;
import systems.imsafe.models.User;
import systems.imsafe.restapi.ImSafeService;
import systems.imsafe.restapi.ServiceGenerator;

public class UserSearchActivity extends AppCompatActivity {
    private ImSafeService service;
    private ArrayList<User> users;
    private ArrayList<User> followingList;
    private RecyclerView userRecyclerView;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        service = ServiceGenerator.createService(ImSafeService.class);
        getFollowingList();
        users = new ArrayList<User>();

        userRecyclerView = findViewById(R.id.user_recycler_view);
        userAdapter = new UserAdapter(users, followingList);
        userRecyclerView.setAdapter(userAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        userRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    public void getFollowingList() {
        Call<ArrayList<User>> call2 = service.getFollowingList();
        call2.enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(@NotNull Call<ArrayList<User>> call, @NotNull Response<ArrayList<User>> response) {
                followingList = response.body();
            }

            @Override
            public void onFailure(@NotNull Call<ArrayList<User>> call, @NotNull Throwable t) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        item.expandActionView();
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search a user...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                RequestBody searchString = RequestBody.create(newText, MediaType.parse("multipart/form-data"));
                Call<ArrayList<User>> call = service.search(searchString);
                call.enqueue(new Callback<ArrayList<User>>() {
                    @Override
                    public void onResponse(@NotNull Call<ArrayList<User>> call, @NotNull Response<ArrayList<User>> response) {
                        users = response.body();
                        getFollowingList();
                        userAdapter = new UserAdapter(users, followingList);
                        userRecyclerView.setAdapter(userAdapter);
                    }

                    @Override
                    public void onFailure(@NotNull Call<ArrayList<User>> call, @NotNull Throwable t) {
                    }
                });
                return false;
            }
        });

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                finish();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
