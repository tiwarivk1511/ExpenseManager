package com.android.roomdatabase;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.roomdatabase.databinding.ActivityMainBinding;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ExpenseAdapter.ExpenseListener {

    ActivityMainBinding binding;
    String productName;
    String amount;
    CardView btnAdd;
    List<Expense> expenseList;
    ExpenseAdapter expenseAdapter; // Create an instance of ExpenseAdapter

    private CardView dateCardView;
    private DatePickerDialog datePickerDialog;
    private String selectedDate; // To store the selected date
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dateCardView = binding.dateBtn; // Correct the ID of the dateCardView

        dateCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbHelper = DatabaseHelper.getDB(this); // Initialize your database
        expenseList = dbHelper.expenceDao().getAllExpenses();

        // Pass the listener and database reference to the adapter
        expenseAdapter = new ExpenseAdapter(expenseList, this, dbHelper);
        recyclerView.setAdapter(expenseAdapter);

        btnAdd = binding.addBtn; // Initialize your btnAdd

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productName = binding.productName.getText().toString().trim();
                amount = binding.productAmount.getText().toString().trim();

                if (productName.isEmpty() || amount.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please provide the required data", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        dbHelper.expenceDao().addTransaction(new Expense(productName, amount, selectedDate));
                        expenseList = dbHelper.expenceDao().getAllExpenses();
                        expenseAdapter.setExpenseList(expenseList);
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Handle the exception (e.g., show an error message)
                        Toast.makeText(MainActivity.this, "An error occurred while adding the transaction", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectedDate = dayOfMonth+ "/" + (month + 1) +"/"+ year;
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    @Override
    public void onExpenseUpdated(Expense expense) {
        // Implementing the logic to update the data in the database
        if (dbHelper != null) {
            dbHelper.expenceDao().updateExpense(expense);
            // Refresh the data in the RecyclerView after the update
            expenseList = dbHelper.expenceDao().getAllExpenses();
            expenseAdapter.setExpenseList(expenseList);
        }
    }

    @Override
    public void onExpenseDeleted(Expense expense) {
        // Implementing the delete logic here, if needed
        if (dbHelper != null) {
            dbHelper.expenceDao().deleteExpense(expense);
            // Refresh the data in the RecyclerView after the delete
            expenseList = dbHelper.expenceDao().getAllExpenses();
            expenseAdapter.setExpenseList(expenseList);
        }
    }
}
