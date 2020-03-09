package systems.imsafe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import systems.imsafe.R;
import systems.imsafe.models.Image;

public class ImageListActivity extends AppCompatActivity {

    ListView lvUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        lvUserList = findViewById(R.id.lv_imageList);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        List<Image> images = null;

        if (bundle != null) {
            images = (List<Image>) bundle.getSerializable("images");
        }
        assert images != null;
        ArrayList<String> imageList = new ArrayList<>();

        for (Image image : images) {
            imageList.add(image.getName());
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, imageList);
        lvUserList.setAdapter(arrayAdapter);
    }
}
