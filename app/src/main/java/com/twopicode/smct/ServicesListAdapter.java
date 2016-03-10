package com.twopicode.smct;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.twopicode.smct.result.DateResult;
import com.twopicode.smct.result.ServiceResult;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**********************************
 * Created by Mikel on 14-Nov-15.
 *********************************/
public class ServicesListAdapter extends BaseAdapter {

    private static final int ITEM_ID_ITEM = 0;
    private static final int ITEM_ID_HEADER = 1;

    private ArrayList<Object> mListItems = null;
    private ArrayList<DateResult> mOriginalData = new ArrayList<>();
    private Context mContext;
    private String mFilterText = "";
    private ItemFilter mFilter = new ItemFilter();
    private int mLocationFilter = 0;
    private ArrayList<String> mDirectorFilter = null;

    public ServicesListAdapter(Context context, ArrayList<Object> listItems) {
        mContext = context;
        mListItems = listItems;
        setAllItems(getDateResultsFromList());
    }

    public void setFilters(int locationFilter, ArrayList<String> directorFilter) {
        mLocationFilter = locationFilter;
        mDirectorFilter = directorFilter;
    }

    public void setFilterText(String filterText) {
        mFilterText = filterText;
    }

    public void setAllItems(ArrayList<DateResult> dateResults) {
        mOriginalData.clear();
        if (dateResults != null)
            mOriginalData.addAll(dateResults);
        notifyDataSetChangedAndFilter();
    }

    private ArrayList<DateResult> getDateResultsFromList() {

        ArrayList<DateResult> dateResults = new ArrayList<>();

        for (Object object : mListItems) {
            if (object instanceof DateResult) {
                dateResults.add((DateResult) object);
            }
        }

        return dateResults;
    }

    private Context getContext() {
        return mContext;
    }

    public boolean isDateResultItem(int position) {
        return getItem(position) instanceof DateResult;
    }

    @Override
    public int getCount() {
        return mListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mListItems.get(position);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof DateResult)
            return ITEM_ID_ITEM;
        return ITEM_ID_HEADER;
    }

    public void notifyDataSetChangedAndFilter() {
        getFilter().filter(mFilterText);
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        removeHeaders();
        Collections.sort(mListItems, new DateResult.DateComparator());
        addHeaders();
        super.notifyDataSetChanged();
    }

    /*
        Add date headers to list
     */
    private void addHeaders() {

        if (mListItems == null || mListItems.size() == 0) { return; }
        ArrayList<DateTime> categories = ServiceResult.getUniqueDatesForResults(getDateResultsFromList()); //use live data
        mListItems.add(0, categories.get(0));
        int currentCategoryIndex = 0;

        for (int i = 1; i < mListItems.size(); i++) {
            if (mListItems.get(i) instanceof DateResult) {
                if (mListItems.get(i - 1) instanceof DateResult) {
                    DateResult previous = (DateResult) mListItems.get(i - 1);
                    DateResult current = (DateResult) mListItems.get(i);
                    if (!previous.servdate.equals(current.servdate)) {
                        currentCategoryIndex++;
                        mListItems.add(i, categories.get(currentCategoryIndex));
                    }
                }
            }
        }
    }

    /*
        Remove date headers from list
    */
    private void removeHeaders() {
        ArrayList<Object> itemsToDelete = new ArrayList<>();

        for (Object item : mListItems)
            if (!(item instanceof DateResult))
                itemsToDelete.add(item);

        mListItems.removeAll(itemsToDelete);
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder viewHolder = null;
        int type = getItemViewType(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            switch (type) {
                case ITEM_ID_ITEM:
                    viewHolder = new ItemViewHolder();
                    row = inflater.inflate(R.layout.services_list_item, parent, false);
                    ((ItemViewHolder) viewHolder).logo = (ImageView) row.findViewById(R.id.services_list_item_logo);
                    ((ItemViewHolder) viewHolder).name = (TextView) row.findViewById(R.id.services_list_item_name);
                    ((ItemViewHolder) viewHolder).time = (TextView) row.findViewById(R.id.services_list_item_time);
//                    ((ItemViewHolder) viewHolder).venue = (TextView) row.findViewById(R.id.services_list_item_venue);
                    ((ItemViewHolder) viewHolder).type = (TextView) row.findViewById(R.id.services_list_item_type);
                    ((ItemViewHolder) viewHolder).location = (TextView) row.findViewById(R.id.services_list_item_location);
                    ((ItemViewHolder) viewHolder).director = (TextView) row.findViewById(R.id.services_list_item_director);
                    break;

                case ITEM_ID_HEADER:
                    viewHolder = new HeaderViewHolder();
                    row = inflater.inflate(R.layout.services_list_header, parent, false);
                    ((HeaderViewHolder) viewHolder).title = (TextView) row.findViewById(R.id.services_list_header_title);
                    break;
            }

            row.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) row.getTag();
        }

        switch (type) {
            case ITEM_ID_ITEM:

                DateResult dateResult = (DateResult) getItem(position);

                if (viewHolder instanceof ItemViewHolder) {
//                    ((ItemViewHolder) viewHolder).logo.setImageDrawable(dateResult.getLogo());
                    ((ItemViewHolder) viewHolder).name.setText(dateResult.getName());
                    ((ItemViewHolder) viewHolder).time.setText(dateResult.getTime());
//                    ((ItemViewHolder) viewHolder).venue.setText("");
                    ((ItemViewHolder) viewHolder).type.setText(dateResult.getType());
                    ((ItemViewHolder) viewHolder).location.setText(dateResult.getLocation());
                    ((ItemViewHolder) viewHolder).director.setText(dateResult.getDirector());
                }
                break;

            case ITEM_ID_HEADER:

                DateTime date = (DateTime) getItem(position);

                if (viewHolder instanceof HeaderViewHolder) {
                    ((HeaderViewHolder) viewHolder).title.setText(ServiceResult.getDateStringForCategory(date, getContext()));
                }
                break;
        }

        return row;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();
            Filter.FilterResults results = new FilterResults();
            final List<DateResult> list = mOriginalData;

            int count = list.size();
            final ArrayList<Object> filteredList = new ArrayList<>(count);

            DateResult dateResult;

            for (int i = 0; i < count; i++) {
                dateResult = list.get(i);
                if (dateResult.getName().toLowerCase().contains(filterString)) {
                    // TODO: filter based on location and director (member values) (requires better data)
                    filteredList.add(dateResult);
                }
            }

            //Funeral Director filter
            if (mDirectorFilter != null && mDirectorFilter.size() > 0) {

                ArrayList<Object> toRemove = new ArrayList<>();

                for (Object curDateResult : filteredList) {
                    if (curDateResult instanceof DateResult) {

                        boolean isInFilter = false;

                        for (String director : mDirectorFilter)
                            if (director.equals(((DateResult) curDateResult).getDirector()))
                                isInFilter = true;

                        if (!isInFilter)
                            toRemove.add(curDateResult);
                    }
                }
                filteredList.removeAll(toRemove);
            }

            results.values = filteredList;
            results.count = filteredList.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mListItems = (ArrayList<Object>) results.values;
            notifyDataSetChanged();
        }
    }

    protected class ViewHolder {}

    protected class HeaderViewHolder extends ViewHolder {
        public TextView title;
    }

    protected class ItemViewHolder extends ViewHolder {
        public ImageView logo;
        public TextView name;
        public TextView time;
        public TextView venue;
        public TextView type;
        public TextView location;
        public TextView director;
    }
}