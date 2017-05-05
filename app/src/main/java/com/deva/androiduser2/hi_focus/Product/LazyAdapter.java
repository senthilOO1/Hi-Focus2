package com.deva.androiduser2.hi_focus.Product;

/**
 * Created by miit on 24/6/15.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.deva.androiduser2.hi_focus.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import com.technotalkative.loadwebimage.imageutils.ImageLoader;

public class LazyAdapter extends BaseAdapter {

    private Activity activity;
    List<Map<String, String>> data = new ArrayList<Map<String, String>>();
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;

    public LazyAdapter(Activity a, List<Map<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.add_list_detail, null);

        Map<String, String> ArrayValue = data.get(position);
        String name1 = ArrayValue.get("name");
        String id1 = ArrayValue.get("id");
        String image1 = ArrayValue.get("image");
        TextView id=(TextView)vi.findViewById(R.id.id);;
        ImageView image=(ImageView)vi.findViewById(R.id.image);

        TextView name=(TextView)vi.findViewById(R.id.name);
        id.setText(id1);
        name.setText(name1);
        imageLoader.DisplayImage(image1, image);
        return vi;
    }

}