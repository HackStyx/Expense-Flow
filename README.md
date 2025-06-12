<!-- ExpenseFlow -->

<div align="center">
  <h1>ExpenseFlow</h1>
  <p>A comprehensive Java-based expense tracking and financial management desktop application</p>
  
  <!-- Badges -->
  <p>
    <img src="https://img.shields.io/badge/Java-21-orange" alt="Java 21">
    <img src="https://img.shields.io/badge/MySQL-8.0-blue" alt="MySQL 8.0">
    <img src="https://img.shields.io/badge/UI-Swing-green" alt="Swing">
    <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License">
    <img src="https://img.shields.io/badge/Status-Active-success" alt="Status">
  </p>
</div>

## 📋 Table of Contents
- [Overview](#-overview)
- [Features](#-features)
- [Screenshots](#-screenshots)
- [Technology Stack](#-technology-stack)
- [Project Structure](#-project-structure)
- [Database Schema](#-database-schema)
- [Installation](#-installation)
- [Usage](#-usage)
- [Architecture](#-architecture)
- [Future Enhancements](#-future-enhancements)
- [Contributing](#-contributing)
- [License](#-license)

## 🔍 Overview
ExpenseFlow is a powerful desktop application designed to help users track, categorize, and analyze their expenses through an intuitive graphical interface. The application provides robust functionality for managing both expenses and categories, with various filtering and sorting capabilities to give users comprehensive control over their personal finances.

## ✨ Features

### 💰 Expense Management
- Add, edit, and delete expense entries
- Track expense amount, title, payment mode, and recurring status
- Categorize expenses for better organization

### 📊 Category Management
- Create and manage expense categories
- Set monthly spending limits per category
- Assign priority levels to categories (High, Medium, Low)
- Toggle active status for categories

### 🔍 Filtering and Sorting
- Filter expenses by payment mode (Cash, Digital, Bank Transfer)
- Filter expenses by recurring status
- Filter categories by priority level
- Filter categories by active status
- Sort data by multiple criteria (ID, name/title, amount, etc.)

### 📈 Data Visualization and Reporting
- View summary statistics (total expenses, category spending)
- Generate financial reports
- Visual indicators for budget status

### 🎨 User Interface
- Modern and intuitive Swing-based UI
- Card-based summary dashboard
- Tabular data view with sorting functionality
- Form-based data entry with validation
- Custom styling and theming

## 📸 Screenshots
<div align="center">
  <img src="https://github.com/user-attachments/assets/7af7083c-d478-413f-a645-a3cbc7f15315" alt="ExpenseFlow Main Interface" width="800"/>
  <p><em>ExpenseFlow expense tracking interface showing expense list and summary dashboard</em></p>
</div>

## 🛠️ Technology Stack
- **Java Development Kit**: JDK 21
- **UI Framework**: Java Swing for graphical user interface
- **Database**: MySQL 8.0
- **Database Connector**: MySQL Connector/J 8.0.33 (JDBC Driver)
- **Build Tool**: Batch scripts for compilation and execution

## 📁 Project Structure
```
ExpenseFlow
  ├── lib/                             # Libraries and dependencies
  │   └── mysql-connector-j-8.0.33.jar # MySQL JDBC driver
  ├── resources/                       # Application resources
  │   └── icon.png                     # Wallet icon used in the application
  ├── SQL Queries_ Bat Files/          # SQL scripts and batch files
  ├── src/                             # Source code
  │   ├── dao/                         # Data Access Objects
  │   │   ├── DBConnection.java        # Database connection management
  │   │   ├── ExpenseDAO.java          # Expense database operations
  │   │   └── CategoryDAO.java         # Category database operations
  │   ├── logic/                       # Business logic
  │   │   ├── DataManager.java         # Generic data management
  │   │   ├── ExpenseManager.java      # Expense-specific operations
  │   │   └── CategoryManager.java     # Category-specific operations
  │   ├── model/                       # Data models
  │   │   ├── Expense.java             # Expense entity
  │   │   └── Category.java            # Category entity
  │   ├── ui/                          # User interface
  │   │   └── MainApp.java             # Main application entry point
  │   └── utils/                       # Utility classes
  │       └── ReportGenerator.java     # Report generation utilities
  ├── run_app.bat                      # Batch file to compile and run the application
  └── build/                           # Compiled class files (generated)
```

## 🗃️ Database Schema
The application uses a MySQL database named `expense_intelligence` with the following tables:

### Expenses Table
| Column | Type | Description |
|--------|------|-------------|
| id | INT | Primary key, auto-increment |
| title | VARCHAR | Expense description |
| amount | FLOAT | Expense amount |
| mode | CHAR | Payment mode: C=Cash, D=Digital, B=Bank Transfer |
| is_recurring | BOOLEAN | Whether expense recurs regularly |
| category_id | INT | Foreign key to categories.id |

### Categories Table
| Column | Type | Description |
|--------|------|-------------|
| id | INT | Primary key, auto-increment |
| name | VARCHAR | Category name |
| monthly_limit | FLOAT | Monthly budget limit |
| priority | CHAR | H=High, M=Medium, L=Low |
| is_active | BOOLEAN | Whether category is active |

## 🚀 Installation

### Prerequisites
- Java Development Kit (JDK) 21
- MySQL Server 8.0
- Git (for cloning the repository)

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/HackStyx/Expense-Flow.git
   cd Expense-Flow
   ```

2. Set up the MySQL database:
   ```sql
   CREATE DATABASE expense_intelligence;
   ```
   
3. Run the SQL scripts in the `SQL Queries_ Bat Files/` directory to create tables and initial data.

4. Update database connection settings in `src/dao/DBConnection.java` if needed (default: username="root", password="root").

5. Run the application:
   ```bash
   .\run_app.bat
   ```

## 💻 Usage

### Main Dashboard
The application opens to a main dashboard with a table view and summary cards.

### Managing Expenses
1. Click "Add Expense" to create a new expense
2. Fill in the required details: title, amount, payment mode, category
3. Toggle "Recurring" if it's a regular expense
4. Click "Save" to store the expense

### Managing Categories
1. Switch to Categories view
2. Click "Add Category" to create a new category
3. Enter the name, monthly limit, and select priority
4. Toggle "Active" status as needed
5. Click "Save" to store the category

### Filtering Data
Use the filter panel to show only relevant expenses or categories:
- Filter expenses by payment mode or recurring status
- Filter categories by priority or active status

### Sorting Data
Click on column headers to sort data or use the dedicated sort controls.

## 🏗️ Architecture
The application follows a layered architecture with clear separation of concerns:

### Model Layer
Contains data entities (Expense, Category) that represent the application's domain objects.

### Data Access Layer (DAO)
Handles all database operations, abstracting the SQL operations from the business logic.

### Logic Layer
Contains the business logic and data management, implementing operations like filtering and validation.

### UI Layer
Manages the user interface and interactions, handling events and displaying data.

## 🔮 Future Enhancements
- Data export to CSV/Excel
- Data visualization with charts and graphs
- Multi-currency support
- Receipt image attachment
- Budget forecasting
- User authentication and multi-user support

## 🤝 Contributing
Contributions are welcome! Here's how you can contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request at [https://github.com/HackStyx/Expense-Flow/pulls](https://github.com/HackStyx/Expense-Flow/pulls)

## 📜 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<div align="center">
  <p>Developed with ❤️ by HackStyx</p>
  <p>© 2023 ExpenseFlow</p>
</div> 
