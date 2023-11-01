package com.android.roomdatabase;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private List<Expense> expenseList;
    private ExpenseListener listener;
    private DatabaseHelper dbHelper;

    public interface ExpenseListener {
        void onExpenseUpdated(Expense expense);
        void onExpenseDeleted(Expense expense);
    }

    public ExpenseAdapter(List<Expense> expenses, ExpenseListener listener, DatabaseHelper dbHelper) {
        this.expenseList = expenses;
        this.listener = listener;
        this.dbHelper = dbHelper; // Initialize the database reference
    }

    // Method to update the dataset
    public void setExpenseList(List<Expense> expenses) {
        this.expenseList = expenses;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.bind(expense);

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle edit button click
                holder.showEditDialog(expense);
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle delete button click
                holder.showDeleteConfirmationDialog(expense);
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenseList != null ? expenseList.size() : 0;
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private TextView productNameTextView;
        private TextView amountTextView;
        private TextView uid;
        private TextView dateTextView;
        private CardView editBtn;
        private CardView deleteBtn;

        ExpenseViewHolder(View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productName);
            amountTextView = itemView.findViewById(R.id.productAmount);
            uid = itemView.findViewById(R.id.uid);
            dateTextView = itemView.findViewById(R.id.date);
            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }

        void bind(Expense expense) {
            productNameTextView.setText(expense.getProductName());
            amountTextView.setText(expense.getAmount());
            uid.setText(String.valueOf(expense.getUid()));
            dateTextView.setText(expense.getDate());
        }

        private void showEditDialog(Expense expense) {
            if (expense != null) {
                Context context = itemView.getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.update_data, null);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                EditText editProductName = dialogView.findViewById(R.id.UpdateProductName);
                EditText editAmount = dialogView.findViewById(R.id.updateProductAmount);
                TextView updateDate = dialogView.findViewById(R.id.UpdateDate);
                CardView updateButton = dialogView.findViewById(R.id.updateBtn);
                CardView cancelButton = dialogView.findViewById(R.id.UpdateCancleBtn);
                CardView calenderCardView = dialogView.findViewById(R.id.UpdateCalender);

                // Get the current date as the default date
                String selectedDate = getCurrentDate();
                // Set the selected date on the date TextView
                updateDate.setText(selectedDate);

                // Create a DatePickerDialog for selecting a new date
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String newDate = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
                                updateDate.setText(newDate); // Update the TextView with the new date
                            }
                        },
                        // Set the DatePickerDialog initial values to the current date
                        getCurrentYear(),
                        getCurrentMonth(),
                        getCurrentDay()
                );

                // Handle the calendar button click to show the DatePickerDialog
                calenderCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        datePickerDialog.show();
                    }
                });

                // Handle update button click
                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Get the updated data from the EditTexts and the selected date
                        String updatedName = editProductName.getText().toString();
                        String updatedAmount = editAmount.getText().toString();
                        String updatedDate = updateDate.getText().toString(); // Use the selected date

                        // Update the expense with the edited data and the selected date
                        expense.setProductName(updatedName);
                        expense.setAmount(updatedAmount);
                        expense.setDate(updatedDate);

                        // Notify the listener to update the expense in the database
                        if (listener != null) {
                            listener.onExpenseUpdated(expense);
                        }

                        dialog.dismiss();
                    }
                });

                // Handle cancel button click
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        }

        // Helper method to get the current year
        private int getCurrentYear() {
            Calendar calendar = Calendar.getInstance();
            return calendar.get(Calendar.YEAR);
        }

        // Helper method to get the current month
        private int getCurrentMonth() {
            Calendar calendar = Calendar.getInstance();
            return calendar.get(Calendar.MONTH);
        }

        // Helper method to get the current day
        private int getCurrentDay() {
            Calendar calendar = Calendar.getInstance();
            return calendar.get(Calendar.DAY_OF_MONTH);
        }

        // Helper method to get the current date in the desired format
        private String getCurrentDate() {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
            return dateFormat.format(calendar.getTime());
        }


        void showDeleteConfirmationDialog(final Expense expense) {
            Context context = itemView.getContext();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure you want to delete this expense?");
            builder.setPositiveButton("Delete", (dialog, which) -> {
                // Delete the expense from the database
                dbHelper.expenceDao().deleteExpense(expense);

                // Notify the listener to delete the expense
                if (listener != null) {
                    listener.onExpenseDeleted(expense);
                }

                // Remove the deleted item from the list
                expenseList.remove(expense);

                // Notify the adapter that the data has changed
                notifyDataSetChanged();
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }
    }
}
