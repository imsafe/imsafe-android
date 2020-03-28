package systems.imsafe.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import systems.imsafe.R;
import systems.imsafe.models.Image;

public class ImageAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private Activity context;
    private ArrayList<Image> images;

    public ImageAdapter(Activity context, ArrayList<Image> images) {
        this.context = context;
        this.images = images;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Image getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        itemView = (itemView == null) ? inflater.inflate(R.layout.list_item, null) : itemView;
        TextView tv_name = itemView.findViewById(R.id.tv_name);
        TextView tv_description = itemView.findViewById(R.id.tv_description);
        Image selectedImage = images.get(position);
        tv_name.setText(selectedImage.getName());
        tv_description.setText(selectedImage.getDescription());
        return itemView;
    }
}
