# PrepPilot AI

PrepPilot AI is an AI-powered personal learning assistant that helps students turn their study materials into structured and interactive learning resources.

Users can upload PDF study materials, organize them by subject, and use AI to generate summaries, quizzes, and flashcards. The application also tracks quiz attempts and provides a learning dashboard.

## Features

- User registration and login
- JWT-based authentication
- Role-based access control
- Admin dashboard and user management
- Subject management
- PDF document upload
- PDF text extraction using Apache PDFBox
- AI-powered document summaries
- AI-generated quizzes
- AI-generated flashcards
- Quiz attempts and score calculation
- Question-wise quiz results
- Quiz history
- Learning dashboard with statistics
- Search across documents and flashcards
- User-specific data access
- Safe admin user deletion with related data cleanup

## Tech Stack

### Backend

- Java 21
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate
- Spring Security
- JWT
- Bean Validation
- Lombok
- Maven
- MySQL

### AI & Document Processing

- Google Gemini API
- Apache PDFBox

### Frontend

- HTML5
- CSS3
- Vanilla TypeScript
- Vite

## Application Modules

### Authentication

Users can register and log in securely using email and password.

Passwords are stored using BCrypt hashing and authentication is handled using JWT tokens.

### Subject Management

Users can create, view, update, and delete their own subjects.

### Document Management

Users can upload PDF study materials under a subject.

The backend extracts text from uploaded PDFs using Apache PDFBox and stores the extracted content for AI processing.

### AI Summary

PrepPilot AI sends extracted document content to the Gemini API and generates a concise study summary.

Generated summaries are cached so that an existing summary can be reused instead of generating it repeatedly.

### AI Quiz Generator

The application generates five multiple-choice questions from document content.

Each question contains:

- Question
- Option A
- Option B
- Option C
- Option D
- Correct Answer

### Quiz Attempts

Users can attempt generated quizzes.

The system calculates:

- Score
- Total questions
- Percentage
- Question-wise correctness
- Correct answers
- Performance message

Quiz attempts are stored and displayed in Quiz History.

### AI Flashcards

The application generates ten question-answer flashcards from uploaded study material.

### Dashboard

The dashboard provides an overview of the user's learning activity, including:

- Total subjects
- Total documents
- Generated quizzes
- Generated flashcards
- Quiz attempts
- Average quiz score

### Search

Users can search their documents and flashcards using a search query.

Search results are restricted to the authenticated user's own learning data.

### Admin Panel

Administrators have access to a dedicated admin panel.

Admin capabilities include:

- View dashboard statistics
- View all users
- View individual users
- Change USER and ADMIN roles
- Delete users safely
- Prevent deletion of the currently logged-in admin
- Prevent deletion of the last admin account

Admin APIs are protected using Spring Security and role-based authorization.

## Security

PrepPilot AI uses Spring Security with JWT authentication.

Security features include:

- BCrypt password hashing
- JWT authentication
- Role-based authorization
- Protected REST APIs
- ADMIN-only endpoints
- User ownership validation
- Protected user-specific resources
- Environment-variable based Gemini API key configuration

The Gemini API key is not stored directly in the source code.

Example:

```properties
gemini.api-key=${GEMINI_API_KEY}