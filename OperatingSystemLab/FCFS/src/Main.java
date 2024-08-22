import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {
    private JTextField[] fcfsArrivalTimeField;
    private JTextField[] fcfsBurstTimeField;
    private JButton executeFCFSButton;
    private JTable fcfsTable;

    private JTextField[] priorityArrivalTimeField;
    private JTextField[] priorityBurstTimeField;
    private JTextField[] priorityPriorityField;
    private JButton executePriorityButton;
    private JTable priorityTable;

    public Main() {
        setTitle("CPU Scheduling Simulator");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create FCFS input panel
        JPanel fcfsInputPanel = createInputPanel("FCFS");

        // Create Priority input panel
        JPanel priorityInputPanel = createInputPanel("Priority");

        executeFCFSButton = createButton("Execute FCFS", e -> executeFCFS(fcfsArrivalTimeField.length));
        executePriorityButton = createButton("Execute Priority Scheduling", e -> executePriority(priorityArrivalTimeField.length));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(executeFCFSButton);
        buttonPanel.add(executePriorityButton);

        // Create tables and their models
        DefaultTableModel fcfsTableModel = createTableModel("Complete", "Turnaround", "Waiting");
        DefaultTableModel priorityTableModel = createTableModel("Priority", "Complete", "Turnaround", "Waiting");

        fcfsTable = new JTable(fcfsTableModel);
        priorityTable = new JTable(priorityTableModel);

        JScrollPane fcfsScrollPane = new JScrollPane(fcfsTable);
        JScrollPane priorityScrollPane = new JScrollPane(priorityTable);

        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        tablesPanel.add(fcfsScrollPane);
        tablesPanel.add(priorityScrollPane);

        setLayout(new BorderLayout(10, 10));
        add(fcfsInputPanel, BorderLayout.WEST);
        add(priorityInputPanel, BorderLayout.EAST);
        add(tablesPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();  // Adjust frame size to fit components
        setVisible(true);
    }

    private JPanel createInputPanel(String title) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        int n = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of " + title + " processes:"));

        if (title.equals("FCFS")) {
            fcfsArrivalTimeField = new JTextField[n];
            fcfsBurstTimeField = new JTextField[n];

            for (int i = 0; i < n; i++) {
                panel.add(createLabel(title + " Arrival Time for Process " + (i + 1) + ":"));
                fcfsArrivalTimeField[i] = new JTextField();
                panel.add(fcfsArrivalTimeField[i]);

                panel.add(createLabel(title + " Burst Time for Process " + (i + 1) + ":"));
                fcfsBurstTimeField[i] = new JTextField();
                panel.add(fcfsBurstTimeField[i]);
            }
        } else if (title.equals("Priority")) {
            priorityArrivalTimeField = new JTextField[n];
            priorityBurstTimeField = new JTextField[n];
            priorityPriorityField = new JTextField[n];

            for (int i = 0; i < n; i++) {
                panel.add(createLabel(title + " Arrival Time for Process " + (i + 1) + ":"));
                priorityArrivalTimeField[i] = new JTextField();
                panel.add(priorityArrivalTimeField[i]);

                panel.add(createLabel(title + " Burst Time for Process " + (i + 1) + ":"));
                priorityBurstTimeField[i] = new JTextField();
                panel.add(priorityBurstTimeField[i]);

                panel.add(createLabel(title + " Priority for Process " + (i + 1) + ":"));
                priorityPriorityField[i] = new JTextField();
                panel.add(priorityPriorityField[i]);
            }
        }

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        return button;
    }

    private DefaultTableModel createTableModel(String... columns) {
        DefaultTableModel model = new DefaultTableModel();
        for (String column : columns) {
            model.addColumn(column);
        }
        return model;
    }

    private void executeFCFS(int n)
    {
        int[] pid = new int[n];
        int[] ar = new int[n];
        int[] bt = new int[n];
        int[] ct = new int[n];
        int[] ta = new int[n];
        int[] wt = new int[n];
        float avgwt = 0, avgta = 0;
        int temp;

        for (int i = 0; i < n; i++) {
            ar[i] = Integer.parseInt(fcfsArrivalTimeField[i].getText());
            bt[i] = Integer.parseInt(fcfsBurstTimeField[i].getText());
            pid[i] = i + 1;
        }

        // Sort processes according to arrival times
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n - (i + 1); j++) {
                if (ar[j] > ar[j + 1]) {
                    temp = ar[j];
                    ar[j] = ar[j + 1];
                    ar[j + 1] = temp;
                    temp = bt[j];
                    bt[j] = bt[j + 1];
                    bt[j + 1] = temp;
                    temp = pid[j];
                    pid[j] = pid[j + 1];
                    pid[j + 1] = temp;
                }
            }
        }

        // Finding completion times
        for (int i = 0; i < n; i++) {
            if (i == 0) {
                ct[i] = ar[i] + bt[i];
            } else {
                if (ar[i] > ct[i - 1]) {
                    ct[i] = ar[i] + bt[i];
                } else {
                    ct[i] = ct[i - 1] + bt[i];
                }
            }
            ta[i] = ct[i] - ar[i];
            wt[i] = ta[i] - bt[i];
            avgwt += wt[i];
            avgta += ta[i];
        }
        DefaultTableModel fcfsTableModel = (DefaultTableModel) fcfsTable.getModel();
        fcfsTableModel.setRowCount(0); // Clear existing rows

        for (int i = 0; i < n; i++) {
            fcfsTableModel.addRow(new Object[]{
                    ct[i], ta[i], wt[i]
            });
        }
        // Display the result
        StringBuilder result = new StringBuilder("\n");
        result.append("+-----+---------+-------+----------+------------+---------+---------\n");
        result.append(String.format("| PID | Arrival | Burst | Complete | Turnaround | Waiting |\n"));

        for (int i = 0; i < n; i++) {
            result.append(String.format("| %3d | %9d | %7d | %9d | %15d | %20d |\n",
                    pid[i], ar[i], bt[i], ct[i], ta[i], wt[i]));
        }

        result.append("+-----+---------+-------+----------+------------+---------+---------\n");

        result.append("\nAverage Waiting Time: ").append(avgwt / n);
        result.append("\nAverage Turnaround Time: ").append(avgta / n);

        JOptionPane.showMessageDialog(this, result.toString());



    }

    private void executePriority(int n) {
        int[] pid = new int[n];
        int[] ar = new int[n];
        int[] bt = new int[n];
        int[] priority = new int[n];
        int[] ct = new int[n];
        int[] ta = new int[n];
        int[] wt = new int[n];
        float avgwt = 0, avgta = 0;
        int temp;

        for (int i = 0; i < n; i++) {
            ar[i] = Integer.parseInt(priorityArrivalTimeField[i].getText());
            bt[i] = Integer.parseInt(priorityBurstTimeField[i].getText());
            priority[i] = Integer.parseInt(priorityPriorityField[i].getText());
            pid[i] = i + 1;
        }

        // Sort processes according to priority and arrival time (tie-breaking)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n - (i + 1); j++) {
                // Check priority first
                if (priority[j] > priority[j + 1]) {
                    // Swap if priority is higher
                    swapProcesses(j, j + 1, ar, bt, priority, pid);
                } else if (priority[j] == priority[j + 1]) {
                    // If priorities are equal, consider arrival time as tie-breaker
                    if (ar[j] > ar[j + 1]) {
                        // Swap if arrival time is higher
                        swapProcesses(j, j + 1, ar, bt, priority, pid);
                    }
                }
            }
        }

        // Finding completion times based on priority scheduling
        ct[0] = ar[0] + bt[0];
        for (int i = 1; i < n; i++) {
            ct[i] = ct[i - 1] + bt[i];
        }

        // Finding turnaround times and waiting times
        for (int i = 0; i < n; i++) {
            ta[i] = ct[i] - ar[i];
            wt[i] = ta[i] - bt[i];
            avgwt += wt[i];
            avgta += ta[i];
        }

        DefaultTableModel priorityTableModel = (DefaultTableModel) priorityTable.getModel();
        priorityTableModel.setRowCount(0); // Clear existing rows

        for (int i = 0; i < n; i++) {
            priorityTableModel.addRow(new Object[]{
                    priority[i], ct[i], ta[i], wt[i]
            });
        }

        // Display the result
        StringBuilder result = new StringBuilder("\n");
        result.append("+-----+---------+-------+----------+------------+---------+---------\n");
        result.append(String.format("| PID | Arrival | Burst | Priority | Complete | Turnaround | Waiting |\n"));

        for (int i = 0; i < n; i++) {
            result.append(String.format("|  %3d | %9d | %7d | %9d | %9d | %15d | %20d |\n",
                    pid[i], ar[i], bt[i], priority[i], ct[i], ta[i], wt[i]));
        }

        result.append("+-----+---------+-------+----------+------------+---------+---------\n");

        result.append("\nAverage Waiting Time: ").append(avgwt / n);
        result.append("\nAverage Turnaround Time: ").append(avgta / n);

        JOptionPane.showMessageDialog(this, result.toString());
    }

    // Helper method to swap processes
    private void swapProcesses(int i, int j, int[] ar, int[] bt, int[] priority, int[] pid) {
        int temp;
        temp = ar[i];
        ar[i] = ar[j];
        ar[j] = temp;
        temp = bt[i];
        bt[i] = bt[j];
        bt[j] = temp;
        temp = priority[i];
        priority[i] = priority[j];
        priority[j] = temp;
        temp = pid[i];
        pid[i] = pid[j];
        pid[j] = temp;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}
