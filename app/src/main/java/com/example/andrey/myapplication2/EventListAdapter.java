package com.example.andrey.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Адаптер для списка происшествий
 */

public class EventListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<EventClass> list;
    private FragmentActivity activity;

    EventListAdapter(FragmentActivity _activity, Context _context, ArrayList<EventClass> arrayList) {
        activity = _activity;
        context = _context;
        list = arrayList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.event_layout, parent, false);
        }

        EventClass p = getEvent(position);

        ((TextView) view.findViewById(R.id.ev_cat)).setText(p.category);
        ((TextView) view.findViewById(R.id.ev_date)).setText(p.date);
        ((TextView) view.findViewById(R.id.ev_adr)).setText(p.address);
        ((TextView) view.findViewById(R.id.ev_text)).setText(p.description);
        ((TextView) view.findViewById(R.id.ev_author)).setText(p.author);

        /**
         * Заглушка для изображений
         * */
        // Не трогать
        int targetW = 240;
        int targetH = 240;

        // Тестовый полный путь к фотке на моём телефоне
        String mCurrentPhotoPath = "/storage/emulated/0/DCIM/Camera/IMG_20170203_220923.jpg";

        // Масштабируем миниатюры (не трогать)
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        // Получаем Bitmap для конкретной фотки
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        // Если фотки есть, то делаем контейнер видимымм
        view.findViewById(R.id.ev_images_layout).setVisibility(View.VISIBLE);

        // Устанавливаем imageView и делаем его видимым
        ImageView img1 = (ImageView) view.findViewById(R.id.image_ev_1);
        ImageView img2 = (ImageView) view.findViewById(R.id.image_ev_2);
        ImageView img3 = (ImageView) view.findViewById(R.id.image_ev_3);
        img1.setImageBitmap(bitmap);
        img1.setVisibility(View.VISIBLE);
        img2.setImageBitmap(bitmap);
        img2.setVisibility(View.VISIBLE);
        img3.setImageBitmap(bitmap);
        img3.setVisibility(View.VISIBLE);

        // По клику показываем на полный экран
        img1.setOnClickListener(new OnImageClickListener(mCurrentPhotoPath));
        img2.setOnClickListener(new OnImageClickListener(mCurrentPhotoPath));
        img3.setOnClickListener(new OnImageClickListener(mCurrentPhotoPath));

        return view;
    }

    class OnImageClickListener implements View.OnClickListener {
        String path;

        public OnImageClickListener(String _path) {
            path = _path;
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(activity, FullscreenViewActivity.class);
            i.putExtra("path", path);
            activity.startActivity(i);
        }

    }

    EventClass getEvent(int position) {
        return ((EventClass) getItem(position));
    }
}
