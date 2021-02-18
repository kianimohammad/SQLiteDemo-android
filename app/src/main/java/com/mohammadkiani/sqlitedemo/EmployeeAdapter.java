package com.mohammadkiani.sqlitedemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mohammadkiani.sqlitedemo.model.Employee;

import java.util.Arrays;
import java.util.List;

public class EmployeeAdapter extends ArrayAdapter {

    Context context;
    int layoutRes;
    List<Employee> employeeList;
    SQLiteDatabase sqLiteDatabase;


    public EmployeeAdapter(@NonNull Context context, int resource, List<Employee> employeeList, SQLiteDatabase sqLiteDatabase) {
        super(context, resource, employeeList);
        this.employeeList = employeeList;
        this.sqLiteDatabase = sqLiteDatabase;
        this.context = context;
        this.layoutRes = resource;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(layoutRes, null);
        TextView nameTextView = v.findViewById(R.id.row_name);
        TextView departmentTextView = v.findViewById(R.id.row_department);
        TextView salaryTextView = v.findViewById(R.id.row_salary);
        TextView dateTextView = v.findViewById(R.id.row_joining_date);

        Employee employee = employeeList.get(position);
        nameTextView.setText(employee.getName());
        departmentTextView.setText(employee.getDepartment());
        salaryTextView.setText(String.valueOf(employee.getSalary()));
        dateTextView.setText(employee.getJoiningDate());

        v.findViewById(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEmployee();
            }

            private void updateEmployee() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View view = layoutInflater.inflate(R.layout.dialog_update_employee, null);
                builder.setView(view);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                EditText nameEditText = view.findViewById(R.id.et_name);
                EditText salaryEditText = view.findViewById(R.id.et_salary);
                Spinner departmentSpinner = view.findViewById(R.id.spinner_dept);

                String[] departmentArray = context.getResources().getStringArray(R.array.departments);
                int position = Arrays.asList(departmentArray).indexOf(employee.getDepartment());

                nameEditText.setText(employee.getName());
                salaryEditText.setText(String.valueOf(employee.getSalary()));
                departmentSpinner.setSelection(position);

                view.findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = nameEditText.getText().toString().trim();
                        String salary = salaryEditText.getText().toString().trim();
                        String department = departmentSpinner.getSelectedItem().toString();

                        if (name.isEmpty()) {
                            nameEditText.setError("name field cannot be empty");
                            nameEditText.requestFocus();
                            return;
                        }

                        if (salary.isEmpty()) {
                            salaryEditText.setError("salary field cannot be empty");
                            salaryEditText.requestFocus();
                            return;
                        }

                        String sql = "UPDATE employee SET name=?, department=?, salary=? WHERE id=?";
                        sqLiteDatabase.execSQL(sql, new String[]{name,department, salary, String.valueOf(employee.getId())});
                        loadEmployees();
                        alertDialog.dismiss();

                    }
                });

            }
        });

        v.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEmployee(employee);
            }

            private void deleteEmployee(Employee employee) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sql = "DELETE FROM employee WHERE id = ?";
                        sqLiteDatabase.execSQL(sql, new Integer[]{employee.getId()});
                        loadEmployees();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "The employee (" + employee.getName() + ") is not deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        return v;
    }

    private void loadEmployees() {
        String sql = "SELECT * FROM employee";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        employeeList.clear();
        if (cursor.moveToFirst()) {
            do {
                // create an employee instance
                employeeList.add(new Employee(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4)
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }
        notifyDataSetChanged();
    }
}
