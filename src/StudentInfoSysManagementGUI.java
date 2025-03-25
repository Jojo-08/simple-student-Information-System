import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;


public class StudentInfoSysManagementGUI extends JFrame {
    private StudentDatabase studentDB;
    private ProgramDatabase programDB;
    private CollegeDatabase collegeDB;
    private SearchBy searchBy;
    private JTable studentTable;
    private JTable programTable;
    private JTable collegeTable;
    private JTabbedPane tabbedPane;
    private DefaultTableModel programTableModel;
    private DefaultTableModel studentTableModel;
    private DefaultTableModel collegeTableModel;
    private JTextField idField, firstNameField, lastNameField,
                        programCodeField, programNameField,
                       studentSearchField, programSearchField, collegeSearchField;
    private JButton okButton, cancelButton, addStudentButton, addProgramButton, studentSearchButton, programSearchButton,
                    collegeSearchButton, addCollegeButton, studentResetButton, programResetButton, collegeResetButton, 
                    sortButton, deleteStudentButton, deleteProgramButton,deleteCollegeButton, confirmDeleteStudentButton, 
                    confirmDeleteProgramButton,confirmDeleteCollegeButton, editStudentButton, editProgramButton, editCollegeButton,
                    confirmEditStudentButton, confirmEditProgramButton, confirmEditCollegeButton;

    private boolean isEditMode = false;
    private boolean isDeleteMode = false;
    private int previousTabIndex = 0; // Keeps track of the last selected tab
   
    public StudentInfoSysManagementGUI() {
        // Set FlatLaf Look and Feel

        

        // Initialize databases
       
        studentDB = new StudentDatabase("students.csv");
        programDB = new ProgramDatabase("programs.csv", studentDB);
        collegeDB = new CollegeDatabase("colleges.csv", studentDB, programDB);
        searchBy = new SearchBy(studentDB, programDB, collegeDB);

        // Set up the main frame
        setTitle("Simple Student Management System");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Students", createStudentPanel());
        tabbedPane.addTab("Programs", createProgramPanel());
        tabbedPane.addTab("Colleges", createCollegePanel());
        
      
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int newTabIndex = tabbedPane.getSelectedIndex();
        
                if ((isEditMode || isDeleteMode) && newTabIndex != previousTabIndex) {
                    JOptionPane.showMessageDialog(null,
                        "You must cancel Edit or Delete mode before switching tabs.",
                        "Action Required",
                        JOptionPane.WARNING_MESSAGE);
        
                    // Prevent tab switch by reverting to the previous tab
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            tabbedPane.setSelectedIndex(previousTabIndex);
                        }
                    });
        
                    return;
                }
        
                // Update previousTabIndex only if mode is not active
                if (!isEditMode && !isDeleteMode) {
                    previousTabIndex = newTabIndex;
                }
            }
        });
        
        
                  
        add(tabbedPane);
    }

    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        studentTableModel = new DefaultTableModel(new String[]{"ID", "First Name", "Last Name", "Year Level", "Gender", "Program Code"}, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(studentTableModel);

        studentTable.setFont(new Font("Arial", Font.PLAIN, 14)); // Change font and size as needed
        studentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16)); // Header font

        studentTable.setRowHeight(30);

        // Disable column reordering
        studentTable.getTableHeader().setReorderingAllowed(false);

        studentTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // Allow multiple selections

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        studentSearchField = new JTextField(20);
        studentSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (studentSearchField.getText().trim().isEmpty()) {
                    updateStudentTable();
                }
            }
        });

        JComboBox<String> searchOptions = new JComboBox<>(new String[]
                                        {"First Name", "Last Name", 
                                        "Program Code", "Student ID", "College"});
        studentSearchButton = new JButton("Search By");
        studentResetButton = new JButton("Reset");

        studentSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = studentSearchField.getText().trim();
                System.out.println("Query before passing to searchStudents: [" + query + "]");
                searchStudents(query, (String) searchOptions.getSelectedItem());
        
            }
        });

        studentResetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStudentTable();
            }
        });
        

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(studentSearchField);
        searchPanel.add(searchOptions);
        searchPanel.add(studentSearchButton);
        searchPanel.add(studentResetButton);

        deleteStudentButton = new JButton("Delete Student");
        deleteStudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleDeleteMode(deleteStudentButton);
            }
        });
    
        // Create a separate confirm delete button for Students
        confirmDeleteStudentButton = new JButton("Confirm Delete");
        confirmDeleteStudentButton.setVisible(false);
        confirmDeleteStudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedStudents();
            }
        });

        // Button to add student
        addStudentButton = new JButton("Add Student");
        addStudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAddStudentDialog();
            }
        });

        // Button to edit student
        editStudentButton = new JButton("Edit Student");
        editStudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleEditMode(editStudentButton);
            }
        });

        // Button to confirm edit
        confirmEditStudentButton = new JButton("Confirm Edit");
        confirmEditStudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSelectedStudent();
            }
        });
        confirmEditStudentButton.setVisible(false); // Initially hidden


        // Button to sort students
        sortButton = new JButton("Sort Students");
        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortStudents();
            }
        });

        
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addStudentButton);
        buttonPanel.add(editStudentButton);
        buttonPanel.add(confirmEditStudentButton);
        buttonPanel.add(deleteStudentButton);
        buttonPanel.add(confirmDeleteStudentButton);
        buttonPanel.add(sortButton);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Table to display students
        JScrollPane scrollPane = new JScrollPane(studentTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        System.out.println("scrollPane: " + (scrollPane == null ? "NULL" : "Initialized"));
        System.out.println("Adding studentTable to panel...");
         
        System.out.println("Student Panel Buttons: ");
        for (Component comp : buttonPanel.getComponents()) {
            System.out.println("- " + comp.getClass().getName());
        }

        updateStudentTable(); // Load existing students into the table
        return panel;
    }

    private void updateStudentTable() {
        if (studentTableModel == null) return; // Prevent null pointer errors
        studentTableModel.setRowCount(0); // Clear existing rows
    
        List<Student> students = studentDB.readStudents();
        if (students == null) return; // Prevent null pointer errors
    
        for (Student student : students) {
            studentTableModel.addRow(new Object[]{student.getId(), student.getFirstName(), student.getLastName(), student.getYearLevel(), student.getGender(), student.getProgramCode()});
        }
    }

    private void updateTableWithResults(List<Student> results) {
        studentTableModel.setRowCount(0); // Clear table before adding new results
    
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students found!", "Search Results", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    
        for (Student student : results) {
            studentTableModel.addRow(new Object[]{
                student.getId(), student.getFirstName(), student.getLastName(),
                student.getYearLevel(), student.getGender(), student.getProgramCode()
            });
        }
    
        studentTableModel.fireTableDataChanged();
        studentTable.revalidate();
        studentTable.repaint();
    }
    
    private void searchStudents(String query, String criteria) {
        query = query.trim(); // Remove spaces

        System.out.println("Query inside searchStudents: [" + query + "]");

        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search value!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        List<Student> results = new ArrayList<>();
    
        switch (criteria) {
            case "First Name":
                results = searchBy.getStudentsByFirstName(query);
                break;
            case "Last Name":
                results = searchBy.getStudentsByLastName(query);
                break;
            case "Program Code":
                results = searchBy.getStudentsByProgramCode(query);
                break;
            case "Student ID":
                results = searchBy.getStudentsById(query);
                break;
            case "College":
                results = searchBy.getStudentsByCollegeCode(query);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid search option!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }
    
        updateTableWithResults(results);
    }
    
     private void sortStudents() {
        String[] options = {"Sort by Id","Sort by Last Name", "Sort by First Name","Sort by Gender", "Sort by Year Level", "Sort by Program Code","Sort by College"};
        int choice = JOptionPane.showOptionDialog(this, "Select sorting option:", "Sort Students",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        List<Student> students = studentDB.readStudents(); // Fetch the list of students
        List <Program> programs = programDB.readPrograms(); // Fetch the list of programs
        switch (choice) {
            case 0:
                Sort.sortStudents(students, Sort.byId());
                break;
            case 1:
                Sort.sortStudents(students, Sort.byLastName());
                break;
            case 2:
                Sort.sortStudents(students, Sort.byFirstName());
                break;
            case 3: 
                Sort.sortStudents(students, Sort.byGender());
                break;
            case 4:
                Sort.sortStudents(students, Sort.byYearLevel());
                break;
            case 5:
                Sort.sortStudents(students, Sort.StudentbyProgramCode());
                break;
            case 6:
                Sort.sortStudents(students, Sort.StudentbyCollegeCode(programDB));
                break;
            default:
                return; // No sorting if no option is selected
        }

        // Clear the table and repopulate it with sorted data
        studentTableModel.setRowCount(0); // Clear existing rows
        for (Student student : students) {
            studentTableModel.addRow(new Object[]{student.getId(), student.getFirstName(), student.getLastName(), student.getYearLevel(), student.getGender(), student.getProgramCode()});
        }
    }

    private void disableAllButtonsExcept(JButton cancelButton, JButton confirmButton) {
        disableComponents(tabbedPane, cancelButton, confirmButton); // Disable all buttons inside tabbedPane
    }
    
    private void enableAllButtons() {
        enableComponents(tabbedPane); // Enable all buttons inside tabbedPane
    }

    private void disableComponents(Container container, JButton cancelButton, JButton confirmButton) {
        for (Component comp : container.getComponents()) { 
            if (comp instanceof JButton && comp != cancelButton && comp != confirmButton) {
                comp.setEnabled(false);
            }
            else if(comp instanceof  Container)
            {
                disableComponents((Container) comp, cancelButton, confirmButton);
            }
        }
    }

    private void enableComponents(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                comp.setEnabled(true);
            }
            else if(comp instanceof Container)
            {
                enableComponents((Container) comp);
            }
        }
    }

    private void toggleDeleteMode(JButton deleteButton) {
        isDeleteMode = !isDeleteMode;
        int activeTab = tabbedPane.getSelectedIndex();
    
        JButton currentDeleteButton = null;
        JButton currentConfirmDeleteButton = null;
        JButton currentCancelButton = null;
    
        switch (activeTab) {
            case 0:
                currentDeleteButton = deleteStudentButton;
                currentConfirmDeleteButton = confirmDeleteStudentButton;
                currentCancelButton = deleteStudentButton;
                break;
            case 1:
                currentDeleteButton = deleteProgramButton;
                currentConfirmDeleteButton = confirmDeleteProgramButton;
                currentCancelButton = deleteProgramButton;
                break;
            case 2:
                currentDeleteButton = deleteCollegeButton;
                currentConfirmDeleteButton = confirmDeleteCollegeButton;
                currentCancelButton = deleteCollegeButton;
                break;
            default:
                return;
        }
    
        if (currentDeleteButton == null || currentConfirmDeleteButton == null) {
            System.out.println("⚠ Warning: Delete buttons are not initialized for tab " + activeTab);
            return;
        }
    
        if (isDeleteMode) {
            int response = JOptionPane.showConfirmDialog(this, 
                "You are now in delete mode. Please select the items you want to delete.", 
                "Delete Mode", 
                JOptionPane.OK_CANCEL_OPTION);
    
            if (response == JOptionPane.OK_OPTION) {
                currentDeleteButton.setText("Cancel Delete");
                currentConfirmDeleteButton.setVisible(true);
                disableAllButtonsExcept(currentCancelButton, currentConfirmDeleteButton);
            } else {
                isDeleteMode = false;
            }
        } else {
            enableAllButtons();
            switch (activeTab) {
                case 0:
                    currentDeleteButton.setText("Delete Student");
                    break;
                case 1:
                    currentDeleteButton.setText("Delete Program");
                    break;
                case 2:
                    currentDeleteButton.setText("Delete College");
                    break;
            }
            currentConfirmDeleteButton.setVisible(false);
        }
    
        if (currentDeleteButton.getParent() != null) {
            currentDeleteButton.getParent().revalidate();
            currentDeleteButton.getParent().repaint();
        }
    }
    
    private void toggleEditMode(JButton editButton) {
        isEditMode = !isEditMode; // Toggle edit mode
        int activeTab = tabbedPane.getSelectedIndex();
    
        JButton currentEditButton = null;
        JButton currentConfirmEditButton = null;
        JButton currentCancelButton = null;
    
        // Determine the buttons based on the active tab
        switch (activeTab) {
            case 0: // Student Tab
                
                currentEditButton = editStudentButton;
                currentConfirmEditButton = confirmEditStudentButton;
                currentCancelButton = editStudentButton;
                break;
            case 1: // Program Tab
                currentEditButton = editProgramButton;
                currentConfirmEditButton = confirmEditProgramButton;
                currentCancelButton = editProgramButton;
                break;
            case 2: // College Tab
                currentEditButton = editCollegeButton;
                currentConfirmEditButton = confirmEditCollegeButton;
                currentCancelButton = editCollegeButton;
                break;
            default:
                return; // No action for undefined tabs
        }
    
        // Ensure buttons are not null before modifying them
        if (currentEditButton == null || currentConfirmEditButton == null) {
            System.out.println("⚠ Warning: Edit buttons are not initialized for tab " + activeTab);
            return;
        }
    
        if (isEditMode) {
            int response = JOptionPane.showConfirmDialog(this, 
                "You are now in edit mode. Please select an item to edit.", 
                "Edit Mode", 
                JOptionPane.OK_CANCEL_OPTION);
    
            if (response == JOptionPane.OK_OPTION) {
                currentEditButton.setText("Cancel Edit");
                currentConfirmEditButton.setVisible(true);
                disableAllButtonsExcept(currentCancelButton, currentConfirmEditButton);
            } else {
                isEditMode = false; // Cancel edit mode if the user does not confirm
            }
        } else {
            enableAllButtons();
            switch (activeTab) {
                case 0:
                    currentEditButton.setText("Edit Student");
                    break;
                case 1:
                    currentEditButton.setText("Edit Program");
                    break;
                case 2:
                    currentEditButton.setText("Edit College");
                    break;
            }
            currentConfirmEditButton.setVisible(false);
        }
    
        // Ensure buttons are revalidated to update visibility
        if (currentEditButton.getParent() != null) {
            currentEditButton.getParent().revalidate();
            currentEditButton.getParent().repaint();
        }
    }
         
    private void openAddStudentDialog() {
        JDialog dialog = new JDialog(this, "Add Student", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setResizable(true);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Input fields
         idField = new JTextField( 20);
         firstNameField = new JTextField(20);
         lastNameField = new JTextField( 20);
         JComboBox <String> yearLevelBox = new JComboBox<>(new String[]{"1", "2", "3", "4"});
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        JComboBox <String> programCodeBox = new JComboBox<>();
        List<Program> programs = programDB.readPrograms();
        for (Program program : programs){
            programCodeBox.addItem(program.getProgramCode());
        }
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        // Add components to the dialog
        gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; dialog.add(idField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; dialog.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; dialog.add(firstNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; dialog.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; dialog.add(lastNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; dialog.add(new JLabel("Year Level:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; dialog.add(yearLevelBox, gbc);
        gbc.gridx = 0; gbc.gridy = 4; dialog.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; dialog.add(genderBox, gbc);
        gbc.gridx = 0; gbc.gridy = 5; dialog.add(new JLabel("Program Code:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; dialog.add(programCodeBox, gbc);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; dialog.add(okButton, gbc);
        gbc.gridy = 7; dialog.add(cancelButton, gbc);

        // OK button action
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String yearLevel = (String) yearLevelBox.getSelectedItem();
                String gender = (String) genderBox.getSelectedItem();
                String programCode = (String) programCodeBox.getSelectedItem();
        
                // Prevent duplicate validation pop-ups
                if (!studentDB.validateStudentId(id)) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Invalid ID format. Please use YYYY-NNNN and ensure the year is 2020 or later.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;  // Stop execution after showing the message once
                }

                if(studentDB.doesStudentExist(id,firstName, lastName))
                {
                    JOptionPane.showMessageDialog(dialog, "student already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    dialog.dispose();
                    return;
                }
        
        
        
                studentDB.createStudent(id, firstName, lastName, yearLevel, gender, programCode);
                updateStudentTable();
                dialog.dispose(); // Close the dialog only after validation
            }
        });
        
        // Cancel button action
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this); // Center the dialog
        dialog.setVisible(true); // Show the dialog
    }

    private void editSelectedStudent() {
            isEditMode = true;
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "No student selected for editing.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
    
            // Get the selected student's data
            String id = (String) studentTableModel.getValueAt(selectedRow, 0);
            String firstName = (String) studentTableModel.getValueAt(selectedRow, 1);
            String lastName = (String) studentTableModel.getValueAt(selectedRow, 2);
            String yearLevel = (String) studentTableModel.getValueAt(selectedRow, 3);
            String gender = (String) studentTableModel.getValueAt(selectedRow, 4);
            String programCode = (String) studentTableModel.getValueAt(selectedRow, 5);
    
            // Open the dialog in edit mode
            JDialog dialog = new JDialog(this, "Edit Student", true);
            dialog.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);
    
            // Input fields
            idField = new JTextField(id, 10);
            firstNameField = new JTextField(firstName, 10);
            lastNameField = new JTextField(lastName, 10);
            JComboBox <String> yearLevelBox = new JComboBox<>( new String[]{"1", "2", "3", "4"});
            yearLevelBox.setSelectedItem(yearLevel);
            JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
            genderBox.setSelectedItem(gender); 
            JComboBox <String> programCodeBox = new JComboBox<>();
            List<Program> programs = programDB.readPrograms();
            for(Program program : programs)
            {
                programCodeBox.addItem(program.getProgramCode());
            }

            programCodeBox.setSelectedItem(programCode);

            okButton = new JButton("Save");
            cancelButton = new JButton("Cancel");
    
            // Add components to the dialog
            gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("ID:"), gbc);
            gbc.gridx = 1; gbc.gridy = 0; dialog.add(idField, gbc);
            gbc.gridx = 0; gbc.gridy = 1; dialog.add(new JLabel("First Name:"), gbc);
            gbc.gridx = 1; gbc.gridy = 1; dialog.add(firstNameField, gbc);
            gbc.gridx = 0; gbc.gridy = 2; dialog.add(new JLabel("Last Name:"), gbc);
            gbc.gridx = 1; gbc.gridy = 2; dialog.add(lastNameField, gbc);
            gbc.gridx = 0; gbc.gridy = 3; dialog.add(new JLabel("Year Level:"), gbc);
            gbc.gridx = 1; gbc.gridy = 3; dialog.add(yearLevelBox, gbc);
            gbc.gridx = 0; gbc.gridy = 4; dialog.add(new JLabel("Gender:"), gbc);
            gbc.gridx = 1; gbc.gridy = 4; dialog.add(genderBox, gbc);
            gbc.gridx = 0; gbc.gridy = 5; dialog.add(new JLabel("Program Code:"), gbc);
            gbc.gridx = 1; gbc.gridy = 5; dialog.add(programCodeBox, gbc);
            gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; dialog.add(okButton, gbc);
            gbc.gridy = 7; dialog.add(cancelButton, gbc);
    
            // Save button action
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String newId = idField.getText();
                    String newFirstName = firstNameField.getText();
                    String newLastName = lastNameField.getText();
                    String newYearLevel = (String) yearLevelBox.getSelectedItem();
                    String newGender = (String) genderBox.getSelectedItem();
                    String newProgramCode = (String) programCodeBox.getSelectedItem();
                    StringBuilder message = new StringBuilder();

                    if (studentDB.validateStudentId(newId)) {
                        // Update the student in the database
                
                        System.out.println(id + " == " + newId + " : " + id.equals(newId));
                        if(!id.equals(newId))
                        {   
                                System.out.println("Does ID " + newId + " exist? " + studentDB.doesStudentExist(newId,"",""));
                                if(studentDB.doesStudentExist(newId, "", ""))
                                {
                                    System.out.println("Cannot change ID " + newId);
                                    message.append("Cannot change ID ").append(newId).append(" already exists. \n ");
            
                                }
                        }

                        System.out.println(firstName + " == " + newFirstName + " : " + !firstName.equalsIgnoreCase(newFirstName));
                        System.out.println(lastName + " == " + newLastName + " : " + !lastName.equalsIgnoreCase(newLastName));
                        if ( !firstName.equalsIgnoreCase(newFirstName) && !lastName.equalsIgnoreCase(newLastName))
                        {
                            System.out.println("Does Name " + newFirstName + " " + newLastName + " exist? " + studentDB.doesStudentExist("",newFirstName,newLastName));
                            if(studentDB.doesStudentExist("", newFirstName, newLastName))
                            {
                                System.out.println("Cannot change Name " + newFirstName + " " + newLastName);
                                message.append("Cannot change Name ").append(newFirstName).append(" ").append(newLastName).append(" already exists. \n ");
        
                            }
                        }

                        if (message.length() > 0) {
                            JOptionPane.showMessageDialog(dialog, message.toString(), "Validation Error", JOptionPane.WARNING_MESSAGE);
                            return; // Prevent update if there are validation errors
                        }       

                        studentDB.updateStudent(id,newId, newFirstName, newLastName, newYearLevel, newGender, newProgramCode);
                        updateStudentTable();
                        dialog.dispose(); // Close the dialog
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Invalid ID format. Please use YYYY-NNNN and ensure the year is greater than 2019(Exceeded MRR).", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
    
            // Cancel button action
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                }
            });
    
            dialog.pack();
            dialog.setLocationRelativeTo(this); // Center the dialog
            dialog.setVisible(true); // Show the dialog
        }

    private void deleteSelectedStudents() {
        int[] selectedRows = studentTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "No students selected for deletion.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the selected students?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int rowIndex = selectedRows[i];
                String studentId = (String) studentTableModel.getValueAt(rowIndex, 0);
                studentDB.deleteStudent(studentId); // Delete from database
                studentTableModel.removeRow(rowIndex); // Remove from table model
            }
        }
    }

    private JPanel createProgramPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        
        programTableModel = new DefaultTableModel(new String[]{"Program Code", "Program name", "College Code"}, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        programTable = new JTable(programTableModel);

        programTable.setFont(new Font("Arial", Font.PLAIN, 14)); // Change font and size as needed
        programTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16)); // Header font

        programTable.setRowHeight(30); 

        // Disable column reordering
        programTable.getTableHeader().setReorderingAllowed(false);
        programTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // Allow multiple selections
        panel.add(new JScrollPane(programTable), BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        programSearchField = new JTextField(20);
        programSearchField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent e){
                if(programSearchField.getText().trim().isEmpty())
                {
                    updateProgramTable();
                }
            }
        });

        JComboBox<String> searchOptions = new JComboBox<>(new String[]
                                            {"Program Code", "Program Name", "College Code"});
        programSearchButton = new JButton("Search By");
        programResetButton = new JButton("Reset");

        programSearchButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e){
                String query = programSearchField.getText().trim();
                System.out.println("Query before passing to searchPrograms:" + query + "]");
                searchPrograms(query, (String) searchOptions.getSelectedItem());

            }
        });

        programResetButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                updateProgramTable();
            }
        });

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(programSearchField);
        searchPanel.add(searchOptions);
        searchPanel.add(programSearchButton);
        searchPanel.add(programResetButton);

         // Create a separate delete button for Programs
            deleteProgramButton = new JButton("Delete Program");
            deleteProgramButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    toggleDeleteMode(deleteProgramButton);
                }
        });

        // Create a separate confirm delete button for Programs
            confirmDeleteProgramButton = new JButton("Confirm Delete");
            confirmDeleteProgramButton.setVisible(false);
            confirmDeleteProgramButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteSelectedPrograms();
                }
        });

        // Button to add student
        addProgramButton = new JButton("Add Program");
        addProgramButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAddProgramDialog();
            }
        });

        // Button to edit student
        editProgramButton = new JButton("Edit Program");
        editProgramButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleEditMode(editStudentButton);
            }
        });

        // Button to confirm edit
        confirmEditProgramButton = new JButton("Confirm Edit");
        confirmEditProgramButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSelectedProgram();
            }
        });
        confirmEditProgramButton.setVisible(false); // Initially hidden

        // Button to sort programs
        sortButton = new JButton("Sort Programs");
        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortPrograms();
            }
        });

        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addProgramButton);
        buttonPanel.add(editProgramButton);
        buttonPanel.add(confirmEditProgramButton);
        buttonPanel.add(deleteProgramButton);
        buttonPanel.add(confirmDeleteProgramButton);
        buttonPanel.add(sortButton);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
       
         


        updateProgramTable(); // Load existing students into the table
        return panel;
    }

    private void updateProgramTable() {

        programTableModel.setRowCount(0); // Clear existing rows
        List<Program> programs = programDB.readPrograms();
        for (Program program : programs) {
            programTableModel.addRow(new Object[]{program.getProgramCode(),program.getProgramName(), program.getCollegeCode()});
        }
    }
   
    private void updateProgramTableWithResults(List<Program> results) {
        programTableModel.setRowCount(0); // Clear table before adding new results
    
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No programs found!", "Search Results", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    
        for (Program program : results) {
            programTableModel.addRow(new Object[]{
                program.getProgramCode(),program.getProgramName(), program.getCollegeCode()
            });
        }
    
        programTableModel.fireTableDataChanged();
        programTable.revalidate();
        programTable.repaint();
    }
  
    private void searchPrograms(String query, String criteria) {
        // Search options
        query = query.trim();

         System.out.println("Query inside searchPrograms: [" + query + "]");

        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search value!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Program> results = new ArrayList<>();
    
        switch (criteria) {
            case "Program Code":
                results = searchBy.getProgramsByProgramCode(query);
                break;
            case "Program Name":
                results = searchBy.getProgramsByProgramName(query);
                break;
            case "College Code":
                results = searchBy.getProgramsByCollege(query);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid search option!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }
    
        updateProgramTableWithResults(results);
    }

    private void sortPrograms() {
        String[] options = {"Sort by Program Name", "Sort by Program Code", "Sort by College Code"};
        int choice = JOptionPane.showOptionDialog(this, "Select sorting option:", "Sort Programs",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        List<Program> programs = programDB.readPrograms(); // Fetch the list of students

        switch (choice) {
            case 0:
                Sort.sortPrograms(programs, Sort.byProgramName());
                break;
            case 1:
                Sort.sortPrograms(programs, Sort.ProgrambyProgramCode());
                break;
            case 2:
                Sort.sortPrograms(programs, Sort.ProgrambyCollegeCode());
                break;
            
            default:
                return; // No sorting if no option is selected
        }

        // Clear the table and repopulate it with sorted data
        programTableModel.setRowCount(0); // Clear existing rows
        for (Program program : programs) {
            programTableModel.addRow(new Object[]{ program.getProgramCode(),program.getProgramName(), program.getCollegeCode()});
        }
    }
     
    private void openAddProgramDialog() {
        JDialog dialog = new JDialog(this, "Add Program", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setResizable(true);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField programCodeField = new JTextField( 10);
        JTextField programNameField = new JTextField(10);

        JComboBox<String> collegeCodeBox = new JComboBox<>();
        List<College> colleges = collegeDB.readColleges();
        for(College college : colleges)
        {
            collegeCodeBox.addItem(college.getCollegeCode());
        }
        JButton addButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");                            
    

        gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("Program Code:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; dialog.add(programCodeField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; dialog.add(new JLabel("Program Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; dialog.add(programNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; dialog.add(new JLabel("College Code:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; dialog.add(collegeCodeBox, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; dialog.add(addButton, gbc);
        gbc.gridy = 4; dialog.add(cancelButton, gbc);
            
        
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String newProgramCode = programCodeField.getText();
                String programName = programNameField.getText();
                String collegeCode = (String) collegeCodeBox.getSelectedItem();

                // **Check if College Exists**
                boolean collegeExists = collegeDB.doesCollegeExist(collegeCode);
               
                if(programDB.doesProgramExist(newProgramCode, programName))
                {
                    JOptionPane.showMessageDialog(dialog, "Program already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    dialog.dispose();
                    return;
                }
               
        
                // **If College Exists, Create Program**
                programDB.createProgram(newProgramCode, programName, collegeCode);
                updateProgramTable();
                dialog.dispose();

            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void editSelectedProgram() {
        isEditMode = true;
        int selectedRow = programTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No program selected for editing.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the selected student's data
        String programCode = (String) programTableModel.getValueAt(selectedRow, 0);
        String programName = (String) programTableModel.getValueAt(selectedRow, 1);
        String collegeCode = (String) programTableModel.getValueAt(selectedRow, 2);
      

        // Open the dialog in edit mode
        JDialog dialog = new JDialog(this, "Edit Program", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Input fields
        programCodeField = new JTextField(programCode, 10);
        programNameField = new JTextField(programName, 10);

        JComboBox <String> collegeCodeBox = new JComboBox<>();
        List<College> colleges = collegeDB.readColleges();
        for(College college : colleges)
        {
            collegeCodeBox.addItem(college.getCollegeCode());
        }
        collegeCodeBox.setSelectedItem(collegeCode);
        okButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        // Add components to the dialog
        gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("Program Code:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; dialog.add(programCodeField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; dialog.add(new JLabel("Program Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; dialog.add(programNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; dialog.add(new JLabel("College Code:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; dialog.add(collegeCodeBox, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; dialog.add(okButton, gbc);
        gbc.gridy = 4; dialog.add(cancelButton, gbc);

        // Save button action
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newProgramCode = programCodeField.getText();
                String newProgramName = programNameField.getText();
                String newCollegeCode = (String) collegeCodeBox.getSelectedItem();
                
                StringBuilder message = new StringBuilder();

                //Check for duplicates if the program code is being changed
                System.out.println(programCode + " == " + newProgramCode + " : " + programName.equals(newProgramName));
                if(!programCode.equals(newProgramCode))
                {
                    System.out.println("Does code " + newProgramCode + " exist? " + programDB.doesProgramExist(newProgramCode,""));
                    if(programDB.doesProgramExist(newProgramCode,""))
                    {
                        System.out.println("Cannot change code" + newProgramCode);
                        message.append("Cannot change program code ").append(newProgramCode).append(" already exists. \n ");

                    }
                }

                if(!programName.equalsIgnoreCase(newProgramName))
                {
                    System.out.println("Does program" + newProgramName + " exist? " + programDB.doesProgramExist("",newProgramName));
                    if (programDB.doesProgramExist("",newProgramName))
                    {
                        System.out.println("Cannot change name" + newProgramName);
                        message.append("Cannot change program name! ").append(newProgramName).append(" already exists.\n");
                    }
                }

                if (message.length() > 0) {
                    JOptionPane.showMessageDialog(dialog, message.toString(), "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return; // Prevent update if there are validation errors
                }

                    programDB.updateProgram(programCode,newProgramCode, newProgramName, newCollegeCode);
                    updateProgramTable();
                    updateStudentTable();
                    dialog.dispose(); // Close the dialog
                
            }
        });

        // Cancel button action
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this); // Center the dialog
        dialog.setVisible(true); // Show the dialog
    }

    private void deleteSelectedPrograms() {
        int[] selectedRows = programTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "No programs selected for deletion.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this, "Warning: Deleting this program will also delete all associated students. Do you want to proceed?",
                             "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int rowIndex = selectedRows[i];
                String programCode = (String) programTableModel.getValueAt(rowIndex, 0);
                programDB.deleteProgram(programCode); // Delete from database
                programTableModel.removeRow(rowIndex); // Remove from table model
            }
        }
        else {
            System.out.println("Deletion canceled by the user.");
            return;
        }
        updateStudentTable();
    }

    private JPanel createCollegePanel(){
        JPanel panel = new JPanel(new BorderLayout());

        // Table to display students
        collegeTableModel = new DefaultTableModel(new String[]{"College Code", "College name"}, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        collegeTable = new JTable(collegeTableModel);

        collegeTable.setFont(new Font("Arial", Font.PLAIN, 14)); // Change font and size as needed
        collegeTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16)); // Header font

        collegeTable.setRowHeight(30); 

        // Disable column reordering
        collegeTable.getTableHeader().setReorderingAllowed(false);
        collegeTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // Allow multiple selections
        panel.add(new JScrollPane(collegeTable), BorderLayout.CENTER);
          
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        collegeSearchField = new JTextField(20);
        collegeSearchButton = new JButton("Search By");
        collegeResetButton = new JButton("Reset");

        collegeSearchField.addKeyListener( new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e)
            {
                if(collegeSearchField.getText().trim().isEmpty())
                {
                    updateCollegeTable();
                }
            }
        });

        JComboBox<String> searchOptions = new JComboBox<>(new String[]{
            "College Code", "College Name"
             });
    
        collegeSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String query = collegeSearchField.getText().trim();
                System.out.println("Query before passting to searchColleges: [" + query + "]");
                searchColleges(query, (String) searchOptions.getSelectedItem());
            }
        });

        collegeResetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                updateCollegeTable();
            }
        });

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(collegeSearchField);
        searchPanel.add(searchOptions);
        searchPanel.add(collegeSearchButton);
        searchPanel.add(collegeResetButton);

        // Create a separate delete button for Colleges
        deleteCollegeButton = new JButton("Delete College");
        deleteCollegeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleDeleteMode(deleteCollegeButton);
            }
        });

    // Create a separate confirm delete button for Colleges
        confirmDeleteCollegeButton = new JButton("Confirm Delete");
        confirmDeleteCollegeButton.setVisible(false);
        confirmDeleteCollegeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedColleges();
            }
        });

        // Button to add college
        addCollegeButton = new JButton("Add College");
        addCollegeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAddCollegeDialog();
            }
        });

        // Button to edit student
        editCollegeButton = new JButton("Edit College");
        editCollegeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleEditMode(editCollegeButton);
            }
        });

        // Button to confirm edit
        confirmEditCollegeButton = new JButton("Confirm Edit");
        confirmEditCollegeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSelectedCollege();
            }
        });
        confirmEditCollegeButton.setVisible(false); // Initially hidden

        sortButton = new JButton("Sort Colleges");
        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortColleges();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addCollegeButton);
        buttonPanel.add(editCollegeButton);
        buttonPanel.add(confirmEditCollegeButton);
        buttonPanel.add(deleteCollegeButton);
        buttonPanel.add(confirmDeleteCollegeButton);
        buttonPanel.add(sortButton);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        

        if (collegeTableModel != null) {
        updateCollegeTable();
    } // Load existing colleges into the table
        return panel;
}

    private void updateCollegeTable() {

        collegeTableModel.setRowCount(0); // Clear existing rows
        List<College> colleges = collegeDB.readColleges();

        if (colleges == null) return; // Prevent NullPointerException
        for (College college: colleges) {
            collegeTableModel.addRow(new Object[]{college.getCollegeCode(), college.getCollegeName()});
        }

       /*  System.out.println("Colleges read from database:");
        for (College college : colleges) {
            System.out.println(college.getCollegeCode() + " - " + college.getCollegeName()); // Debugging
            collegeTableModel.addRow(new Object[]{college.getCollegeCode(), college.getCollegeName()});
        } */

        collegeTableModel.fireTableDataChanged();
        collegeTable.revalidate();
        collegeTable.repaint();
    }

    private void updateCollegeTableWithResults(List<College> results) {
       collegeTableModel.setRowCount(0); // Clear table before adding new results
    
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No colleges found!", "Search Results", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    
        for (College college : results) {
            collegeTableModel.addRow(new Object[]{
                college.getCollegeCode(), college.getCollegeName()
            });
        }
    
        collegeTableModel.fireTableDataChanged();
        collegeTable.revalidate();
        collegeTable.repaint();
    }

    private void searchColleges(String query, String criteria) {
        // Search options
       query = query.trim();
       System.out.println("Query inside searchColleges: [" + query + "]");
       
       if(query.isEmpty())
       {
            JOptionPane.showMessageDialog(this,"Please enter a search value!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
       }
        
        List<College> results = new ArrayList<>();
    
        // Call the appropriate search method from SearchBy.java
        switch (criteria) {
            case "College Code":
                results = searchBy.getCollegeByCollegeCode(query);
                break;
            case "College Name":
                results = searchBy.getCollegeByCollegeName(query);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid search option!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }
    
        // Update table with results
        updateCollegeTableWithResults(results);
    }
   
    private void openAddCollegeDialog() {
        JDialog dialog = new JDialog(this, "Add College", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setResizable(true);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
            
        JTextField collegeCodeField = new JTextField(10);
        JTextField collegeNameField = new JTextField(10);
        JButton okButton = new JButton("Ok");
        JButton cancelButton = new JButton("Cancel");

        gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("College Code:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; dialog.add(collegeCodeField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; dialog.add(new JLabel("College Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; dialog.add(collegeNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; dialog.add(okButton, gbc);
        gbc.gridy = 3; dialog.add(cancelButton, gbc);

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String collegeCode = collegeCodeField.getText();
                String collegeName = collegeNameField.getText();
                

                if(collegeDB.doesCollegeExist(collegeCode, collegeName))
                {
                    JOptionPane.showMessageDialog(dialog, "College already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    dialog.dispose();
                    return;
                }
                collegeDB.createCollege(collegeCode, collegeName);
                updateCollegeTable();
                dialog.dispose();
            }
                        
            
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void editSelectedCollege() {
        int selectedRow = collegeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "No college selected for editing.", 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        String oldCollegeCode = (String) collegeTableModel.getValueAt(selectedRow, 0);
        String oldCollegeName = (String) collegeTableModel.getValueAt(selectedRow, 1);
    
        JDialog dialog = new JDialog(this, "Edit College", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
    
        JTextField collegeCodeField = new JTextField(oldCollegeCode, 10);
        JTextField collegeNameField = new JTextField(oldCollegeName, 10);
        JButton okButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
    
        gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("College Code:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; dialog.add(collegeCodeField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; dialog.add(new JLabel("College Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; dialog.add(collegeNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; dialog.add(okButton, gbc);
        gbc.gridy = 3; dialog.add(cancelButton, gbc);
    
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newCollegeCode = collegeCodeField.getText().trim();
                String newCollegeName = collegeNameField.getText().trim();
    
                // **Check if Updating to an Existing College Code**
                StringBuilder message = new StringBuilder();

                // Check for duplicates if the college code is being changed
                System.out.println(oldCollegeCode + " == " + newCollegeCode + " : " + oldCollegeCode.equals(newCollegeCode));
                if (!oldCollegeCode.equals(newCollegeCode)) {
                    System.out.println("Does code " + newCollegeCode + " exist? " + collegeDB.doesCollegeExist(newCollegeCode, ""));
                    if (collegeDB.doesCollegeExist(newCollegeCode, "")) {
                        System.out.println("Cannot change code " + newCollegeCode);
                        message.append("Cannot change college code! ").append(newCollegeCode).append(" already exists.\n");
                    }
                }
    
                // Check for duplicates if the college name is being changed
                System.out.println(oldCollegeName + " == " + newCollegeName + " : " + oldCollegeName.equals(newCollegeName));
                if (!oldCollegeName.equalsIgnoreCase(newCollegeName)) {
                    System.out.println("Does college " + newCollegeName + " exist? " + collegeDB.doesCollegeExist("", newCollegeName));
                    if (collegeDB.doesCollegeExist("", newCollegeName)) {
                        System.out.println("Cannot change name " + newCollegeName);
                        message.append("Cannot change college name! ").append(newCollegeName).append(" already exists.\n");
                    }
                }

                // If there are any messages, show a single pop-up
                if (message.length() > 0) {
                    JOptionPane.showMessageDialog(dialog, message.toString(), "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return; // Prevent update if there are validation errors
                }
    
                // **Update College**
                collegeDB.updateCollege(oldCollegeCode, newCollegeCode, newCollegeName);
                collegeTableModel.fireTableDataChanged();
                updateCollegeTable();
                updateProgramTable();
                dialog.dispose(); // ✅ Close if successful
            }
        });
    
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
    
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void sortColleges() {
        String[] options = {"Sort by College Name", "Sort by College Code"};
        int choice = JOptionPane.showOptionDialog(this, "Select sorting option:", "Sort Colleges",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        List<College> colleges = collegeDB.readColleges(); 

        switch (choice) {
            case 0:
                Sort.sortColleges(colleges, Sort.byCollegeName());
                break;
        
            case 1:
                Sort.sortColleges(colleges, Sort.CollegebyCollegeCode());
                break;
            
            default:
                return; // No sorting if no option is selected
        }

        // Clear the table and repopulate it with sorted data
        collegeTableModel.setRowCount(0); // Clear existing rows
        for (College college : colleges) {
            collegeTableModel.addRow(new Object[]{college.getCollegeCode(), college.getCollegeName()});
        }
    }

    private void deleteSelectedColleges() {
        int[] selectedRows = collegeTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "No Colleges selected for deletion.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this, "Warning: Deleting this college will also delete all associated programs and students. Do you want to proceed?"
                                                        , "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int rowIndex = selectedRows[i];
                String collegeCode = (String) collegeTableModel.getValueAt(rowIndex, 0);
                collegeDB.deleteCollege(collegeCode); // Delete from database
                collegeTableModel.removeRow(rowIndex); // Remove from table model
            }
        }
        else {
            // User chose not to proceed with deletion
            System.out.println("Deletion canceled by the user.");
        }
        // After deleting the college

            updateProgramTable(); // Refresh program table
            updateStudentTable(); // Refresh student table
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                StudentInfoSysManagementGUI gui = new StudentInfoSysManagementGUI();
                gui.setVisible(true);
            }
        });
    }
} 


   
