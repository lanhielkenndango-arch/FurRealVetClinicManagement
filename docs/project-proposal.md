# Project Proposal and Problem Statement

## Project Title

FurReal Vet Clinic Management

## Course

Object-Oriented Programming

## Activity Title

Design and Development of a Multi-Table Java Swing Information System with CRUD, Search, and Relational Database Modeling

## Application Domain

The project is designed for a small veterinary clinic that needs a desktop-based information system for managing client records, pet records, clinic services, visit schedules, and transaction details.

## Problem Statement

Small veterinary clinics often record client details, pet information, service lists, and visit transactions manually or in separate files. This can make records difficult to search, update, and connect with related information. Manual record keeping may also lead to duplicate records, missing pet-owner relationships, unclear visit histories, and inconsistent service transaction totals.

FurReal Vet Clinic Management addresses this problem by providing a Java Swing desktop application connected to a MySQL database. The system organizes clinic records into related tables, supports CRUD operations, allows searching and filtering, and keeps visit transactions connected to clients, pets, and selected services.

## Target Users

- Clinic staff who register clients and encode pet records
- Clinic personnel who manage service records and prices
- Staff members who schedule visits and record selected services
- The clinic owner or administrator who needs organized records for daily operations

## Records Managed by the System

- Client account and contact information
- Pet profile information
- Clinic service catalog information
- Visit or appointment records
- Visit service or transaction detail records

## Initial Database Tables

- `clients`
- `pets`
- `clinic_services`
- `visits`
- `visit_services`

## System Objectives

- Create a structured Java Swing desktop application for veterinary clinic record management.
- Store data permanently using MySQL and JDBC.
- Demonstrate at least five related database tables.
- Implement Create, Read, Update, Delete, and Search features.
- Use JTable components to display records.
- Validate user inputs before saving records.
- Maintain data integrity through primary keys, foreign keys, and relationship rules.

## Core Processes

- Register and authenticate clients.
- View, search, update, and delete client records.
- Add, view, search, update, and delete pet records linked to clients.
- Add, view, search, update, and delete clinic service records.
- Schedule visits by selecting a client, pet, visit date, and service items.
- View, search, update, and delete visit records and transaction details.

## Input Summary

- Client name, phone number, email, and password
- Pet name, type, breed, and age
- Service name, category, price, and date
- Visit date, selected pet, selected client, selected services, and visit status
- Search keywords such as ID, owner name, pet name, service category, date, or status

## Output Summary

- Client and pet tables
- Service catalog table
- Visit and transaction record tables
- Confirmation and validation messages
- Computed visit total based on selected services
- Search and filter results

## Scope and Limitation

The system focuses on local desktop record management for a veterinary clinic. It does not include online booking, payment gateway integration, cloud synchronization, or multi-user role permissions. The project prioritizes the Object-Oriented Programming requirements: Java Swing GUI, JDBC database access, CRUD operations, search features, relational database modeling, and modular class design.
