# Beach Resort Management System

## Overview
The **Beach Resort Management System** is a comprehensive Java-based desktop application designed to streamline the operations of a beach resort. It facilitates management for three distinct user roles: **Admins**, **Staff**, and **Guests**, covering everything from room and boat reservations to user administration and financial reporting.

**Author:** John Aldrie Baquiran

## Features

### 👤 Guest Module
* **User Account:** Sign up, Sign in, and Forgot Password (with email verification).
* **Reservations:** Browse and book resort rooms and boats.
* **Payments:** Integrated payment processing simulation for **GCash**, **Maya**, and **PayPal**.
* **Dashboard:** View personal booking history and manage profile details.

### 🧑‍💼 Staff Module
* **Front Desk Operations:** Handle guest check-ins and check-outs.
* **Reservation Management:** View and modify existing reservations for rooms and boats.
* **Availability Monitoring:** Real-time view of room and boat status.

### 🛡️ Admin Module
* **Dashboard:** High-level view of resort statistics and charts.
* **User Management:** Add, update, and delete system users (Staff, Admins).
* **Inventory Management:** Full control to add, update, or remove Rooms and Boats.
* **Activity Logs:** Monitor system usage and significant actions.
* **Reports:** Generate reports using `JFreeChart` and export data.

## 🛠️ Technology Stack

* **Programming Language:** Java (JDK 23)
* **IDE:** NetBeans
* **Database:** MySQL
* **GUI Framework:** Java Swing with **FlatLaf** for modern UI styling.

### Key Libraries & Dependencies
* **UI/Design:** `FlatLaf` (Light/Dark themes), `RSTableMetro`, `AbsoluteLayout`, `MigLayout`.
* **Data/Reporting:** `JFreeChart` (Graphs), `iTextPDF` (PDF Generation), `Apache POI` (Excel handling).
* **Utilities:** `JavaMail` (Email notifications), `JCalendar` & `Swing Date Picker`, `TimingFramework`.
* **Hardware/Media:** `Webcam Capture`, `OpenCV` (Computer Vision/Image processing).
* **Database:** `MySQL Connector/J 8.0.11`.
