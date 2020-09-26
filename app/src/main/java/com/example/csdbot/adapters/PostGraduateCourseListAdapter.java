package com.example.csdbot.adapters;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.csdbot.viewholders.CourseViewHolder;
import com.example.csdbot.components.PostGraduateCourse;
import com.example.csdbot.R;

import java.util.List;

public class PostGraduateCourseListAdapter extends ArrayAdapter<PostGraduateCourse> {
    private int layout;
    private List<PostGraduateCourse> courseList;

    public PostGraduateCourseListAdapter(@NonNull Context context, int resource, @NonNull List<PostGraduateCourse> objects) {
        super(context, resource, objects);
        layout = resource;
        courseList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CourseViewHolder mainViewholder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layout, parent, false);
            CourseViewHolder viewHolder = new CourseViewHolder();

            /* Get the viewHolders views
             *      name: Course's name
             *      area: Course's area
             *      teacher: Course's teacher
             *      description: Course's description
             *      ects: Course's ECTS
             */
            viewHolder.name = (TextView) convertView.findViewById(R.id.courseTitle);
            viewHolder.area = (TextView) convertView.findViewById(R.id.courseArea);
            viewHolder.teacher = (TextView) convertView.findViewById(R.id.courseTeacher);
            viewHolder.description = (TextView) convertView.findViewById(R.id.courseDesc);
            viewHolder.ects = (TextView) convertView.findViewById(R.id.courseECTS);

            // Set the course's data
            String name = courseList.get(position).getCode_en() + " "
                    + courseList.get(position).getName_en();
            viewHolder.name.setText(name);
            String area = "";
            for( int i = courseList.get(position).getArea_codes_en().size() - 1 ; i >= 0 ; i-- ){
                if ( i == 0 ){
                    area += courseList.get(position).getArea_codes_en().get(i);
                } else {
                    area = area + courseList.get(position).getArea_codes_en().get(i) + ", " ;
                }
            }
            String area2 = "Area: " + area;
            viewHolder.area.setText(area2);
            if ( courseList.get(position).getTeacher() != null){
                String teacher = "Teacher: " + courseList.get(position).getTeacher();
                viewHolder.teacher.setText(teacher);
            }  else {
                String teacher = "Teacher: -";
                viewHolder.teacher.setText(teacher);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (courseList.get(position).getDescription_en().length() >= 100) {
                    viewHolder.description.setText(Html.fromHtml("Description: " + courseList.get(position).getDescription_en().substring(0, 100) +"...", Html.FROM_HTML_MODE_COMPACT));
                } else {
                    viewHolder.description.setText(Html.fromHtml("Description: " + courseList.get(position).getDescription_en(), Html.FROM_HTML_MODE_COMPACT));
                }
            } else {
                if (courseList.get(position).getDescription_en().length() >= 100) {
                    viewHolder.description.setText(Html.fromHtml("Description: " + courseList.get(position).getDescription_en().substring(0, 100) +"..."));
                } else {
                    viewHolder.description.setText(Html.fromHtml("Description: " + courseList.get(position).getDescription_en()));
                }
            }
            String ECTS = "ECTS: " + courseList.get(position).getECTS();
            viewHolder.ects.setText(ECTS);
            convertView.setTag(viewHolder);
        } else {
            mainViewholder = (CourseViewHolder) convertView.getTag();

            /* Get the viewHolders views
             *      name: Course's name
             *      area: Course's area
             *      teacher: Course's teacher
             *      description: Course's description
             *      ects: Course's ECTS
             */
            mainViewholder.name = (TextView) convertView.findViewById(R.id.courseTitle);
            mainViewholder.area = (TextView) convertView.findViewById(R.id.courseArea);
            mainViewholder.teacher = (TextView) convertView.findViewById(R.id.courseTeacher);
            mainViewholder.description = (TextView) convertView.findViewById(R.id.courseDesc);
            mainViewholder.ects = (TextView) convertView.findViewById(R.id.courseECTS);

            // Set the course's data
            String name = courseList.get(position).getCode_en() +
                    " " + courseList.get(position).getName_en();
            mainViewholder.name.setText(name);
            String area = "";
            for( int i = courseList.get(position).getArea_codes_en().size() - 1 ; i >= 0 ; i-- ){
                if ( i == 0 ){
                    area += courseList.get(position).getArea_codes_en().get(i);
                } else {
                    area = area + courseList.get(position).getArea_codes_en().get(i) + ", " ;
                }
            }
            String area2 = "Area: " + area;
            mainViewholder.area.setText(area2);
            if ( courseList.get(position).getTeacher() != null){
                String teacher = "Teacher: " + courseList.get(position).getTeacher();
                mainViewholder.teacher.setText(teacher);
            }  else {
                String teacher = "Teacher: -";
                mainViewholder.teacher.setText(teacher);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (courseList.get(position).getDescription_en().length() >= 100) {
                    mainViewholder.description.setText(Html.fromHtml("Description: " + courseList.get(position).getDescription_en().substring(0, 100) +"...", Html.FROM_HTML_MODE_COMPACT));
                } else {
                    mainViewholder.description.setText(Html.fromHtml("Description: " + courseList.get(position).getDescription_en(), Html.FROM_HTML_MODE_COMPACT));
                }
            } else {
                if (courseList.get(position).getDescription_en().length() >= 100) {
                    mainViewholder.description.setText(Html.fromHtml("Description: " + courseList.get(position).getDescription_en().substring(0, 100) +"..."));
                } else {
                    mainViewholder.description.setText(Html.fromHtml("Description: " + courseList.get(position).getDescription_en()));
                }
            }
            String ECTS = "ECTS: " + courseList.get(position).getECTS();
            mainViewholder.ects.setText(ECTS);
        }
        return convertView;
    }
}
