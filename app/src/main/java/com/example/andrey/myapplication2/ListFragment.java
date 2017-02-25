package com.example.andrey.myapplication2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.andrey.myapplication2.Core.LocalStorage;
import com.example.andrey.myapplication2.SimpleClass.EventChp;
import com.example.andrey.myapplication2.SimpleClass.EventListCountall;
import com.example.andrey.myapplication2.SimpleClass.Myresponse;
import com.example.andrey.myapplication2.rep.EventRepository;

import java.util.ArrayList;

public class ListFragment extends Fragment {
    ArrayList<EventClass> event_list = new ArrayList<>();
    EventListAdapter eventListAdapter;
    LocalStorage myLocalStorage;
    EventRepository eventRepository;
    View _view;
    public ListFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myLocalStorage=new LocalStorage(this.getActivity());
        eventRepository=new EventRepository(myLocalStorage.GetBearerToken());
        _view=view;
        new EventRepositoryGetAllEventTask().execute("list","","0","allevents","1","0");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_list, container, false);

    }

    class EventRepositoryGetAllEventTask extends AsyncTask<String, Void, EventListCountall> {
        @Override
        protected EventListCountall doInBackground(String... params) {
            return eventRepository.GetAllEvent(params[0], params[1], Integer.parseInt(params[2]), params[3], Integer.parseInt(params[4]), Integer.parseInt(params[5]));
        }

        @Override
        protected void onPostExecute(EventListCountall res) {

            //event_list.add(new EventClass("Авто", "сегодня 12:30", "7 Парковая, 15", "Менты приехали, эвкауируют машины", "Пётр Иванов"));
            //event_list.add(new EventClass("Потеря предмета", "16.02.17 15:30", "9 Парковая, 21", "Найдены ключи около подъезда дома", "Иван Петров"));

            for(EventChp e:res.EventList){
                event_list.add(new EventClass(e.Category,e.Date,e.Adress,e.Description,e.Author));
            }
            eventListAdapter = new EventListAdapter(getActivity(), getContext(), event_list);
            ListView listView = (ListView) _view.findViewById(R.id.list);
            listView.setAdapter(eventListAdapter);
        }
    }


    class EventRepositoryDeleteEventTask extends AsyncTask<String, Void, Myresponse> {
        @Override
        protected Myresponse doInBackground(String... params) {
            return eventRepository.DeleteEvent(Integer.parseInt(params[0]));
        }

        @Override
        protected void onPostExecute(Myresponse res) {

        }
    }

    class EventRepositoryPublishEventTask extends AsyncTask<String, Void, Myresponse> {
        @Override
        protected Myresponse doInBackground(String... params) {
            return eventRepository.PublishEvent(Integer.parseInt(params[0]));
        }

        @Override
        protected void onPostExecute(Myresponse res) {

        }
    }

    class EventRepositoryGetEventForModeratorask extends AsyncTask<String, Void, ArrayList<EventChp>> {
        @Override
        protected ArrayList<EventChp> doInBackground(String... params) {
            return (new EventRepository(params[0])).GetEventForModerator();
        }

        @Override
        protected void onPostExecute(ArrayList<EventChp> res) {
            //asdfas
        }
    }


}
