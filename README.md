# 🤖 AI Interviewer — Full Stack Web Application

> An AI-powered interview platform that conducts real-time adaptive interviews using GPT-4o, WebSocket communication, Speech-to-Text answer capture, and generates detailed PDF evaluation reports.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Database Design](#database-design)
- [API Endpoints](#api-endpoints)
- [WebSocket Events](#websocket-events)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Interview Flow](#interview-flow)
- [Cost Estimation](#cost-estimation)
- [Screenshots](#screenshots)

---

## 🎯 Overview

AI Interviewer is a full-stack web application that automates the preliminary interview process. It supports both **HR** and **Technical** interviews, asks adaptive follow-up questions based on candidate responses, scores answers on 5 parameters in real-time, and generates a comprehensive downloadable PDF evaluation report.

---

## ✨ Features

| Feature | Description |
|---|---|
| 🔐 JWT Authentication | Secure register/login with BCrypt password encoding |
| 🎙️ Voice-Based Answers | Speech-to-Text captures spoken answers in real-time |
| 🔊 AI Question Delivery | Text-to-Speech reads questions aloud via ResponsiveVoice |
| 🤖 Adaptive Questions | GPT-4o generates follow-up or random questions based on answers |
| 📊 5-Parameter Scoring | Each answer scored on Relevance, Concept, Clarity, Grammar, Completeness |
| 🛡️ Proctoring | Tab switch and window blur detection with violation logging |
| 📄 PDF Report | Downloadable evaluation report with full question breakdown |
| 📈 Profile & History | Score trend charts, parameter radar, interview history |
| 🌐 Real-time WebSocket | Low-latency bidirectional communication during live interview |
| 🔇 Silence Detection | Auto-submits answer after 5 seconds of silence |

---

## 🛠️ Tech Stack

### Backend
```
Java 17
Spring Boot 3.x
Spring Security + JWT
Spring Data JPA
WebSocket (Raw WebSocket API)
OpenAI GPT-4o API
iText PDF 5.x
SQLite
Lombok
Maven
```

### Frontend
```
HTML5, CSS3, Vanilla JavaScript
Web Speech API (Speech-to-Text)
ResponsiveVoice (Text-to-Speech) — Free
WebSocket API (Browser built-in)
Chart.js (Profile page graphs)
```

### AI
```
OpenAI GPT-4o — Question generation, Answer analysis, Evaluation report
Prompt Engineering — Structured JSON responses, Scoring rubrics
```

---

## 📁 Project Structure

```
ai-interviewer/
│
├── src/main/java/com/demo/
│   ├── config/
│   │   ├── AppConfig.java              # RestTemplate bean
│   │   └── WebSocketConfig.java        # WebSocket endpoint registration
│   │
│   ├── controller/
│   │   ├── AuthController.java         # POST /api/auth/register, /login
│   │   ├── InterviewSetupController.java
│   │   ├── InterviewSessionController.java
│   │   ├── EvaluationController.java
│   │   ├── PdfExportController.java
│   │   └── TTSController.java
│   │
│   ├── dto/
│   │   ├── RegisterRequestDTO.java
│   │   ├── LoginRequestDTO.java
│   │   ├── AuthResponseDTO.java
│   │   ├── InterviewSetupRequestDTO.java
│   │   ├── InterviewSetupResponseDTO.java
│   │   ├── EvaluationReportResponseDTO.java
│   │   ├── WebSocketMessage.java
│   │   ├── QuestionPayload.java
│   │   ├── AnswerPayload.java
│   │   └── ProctorEventPayload.java
│   │
│   ├── gpt/
│   │   └── OpenAIClient.java           # HTTP client for GPT API
│   │
│   ├── model/
│   │   ├── User.java
│   │   ├── InterviewSetup.java
│   │   ├── InterviewSession.java
│   │   ├── InterviewQuestionHistory.java
│   │   ├── AnalysisResult.java
│   │   ├── EvaluationReport.java
│   │   ├── ProctorLog.java
│   │   ├── Role.java                   # USER, ADMIN
│   │   ├── InterviewType.java          # HR, TECHNICAL
│   │   ├── InterviewLevel.java         # BEGINNER, INTERMEDIATE, EXPERT
│   │   └── SessionStatus.java          # IN_PROGRESS, COMPLETED, ABANDONED
│   │
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── InterviewSetupRepository.java
│   │   ├── InterviewSessionRepository.java
│   │   ├── InterviewQuestionHistoryRepository.java
│   │   ├── AnalysisResultRepository.java
│   │   ├── EvaluationReportRepository.java
│   │   └── ProctorLogRepository.java
│   │
│   ├── security/
│   │   ├── JwtUtil.java                # Token generation & validation
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── CustomUserDetailsService.java
│   │   └── SecurityConfig.java
│   │
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── InterviewSetupService.java
│   │   ├── InterviewSessionService.java
│   │   ├── QuestionGeneratorService.java
│   │   ├── AnalysisService.java
│   │   ├── EvaluationService.java
│   │   └── PdfExportService.java
│   │
│   └── websocket/
│       └── InterviewWebSocketHandler.java
│
├── src/main/resources/
│   ├── application.properties
│   └── static/
│       ├── index.html                  # Landing page
│       ├── auth.html                   # Login / Register
│       ├── setup.html                  # Interview configuration
│       ├── interview.html              # Live interview page
│       ├── evaluation.html             # Evaluation report + history
│       └── profile.html               # User profile + charts
│
└── pom.xml
```

---

## 🗄️ Database Design

```
User (1) ──────────────── (Many) InterviewSetup
InterviewSetup (1) ─────── (Many) InterviewSession
InterviewSession (1) ────── (Many) InterviewQuestionHistory
InterviewSession (1) ────── (1)   EvaluationReport
InterviewSession (1) ────── (Many) ProctorLog
InterviewQuestionHistory (1) ─── (1) AnalysisResult
```

### Entity Summary

| Entity | Key Fields |
|---|---|
| User | id, name, email, password (BCrypt), role, createdAt |
| InterviewSetup | interviewType, techStack, interviewLevel, yearsOfExperience, totalQuestions |
| InterviewSession | sessionToken (UUID), startTime, endTime, overallScore, status, proctorViolationCount |
| InterviewQuestionHistory | questionText, userAnswer, questionNumber, isFollowUp, wasSkipped, askedAt, responseTimeSeconds |
| AnalysisResult | relevanceScore, conceptScore, clarityScore, grammarScore, completenessScore, overallScore, feedbackSummary |
| EvaluationReport | overallScore, performanceBand, strengths, areasToImprove, recommendation |
| ProctorLog | violationType, details, detectedAt |

---

## 🌐 API Endpoints

### Auth
```
POST   /api/auth/register          Register new user
POST   /api/auth/login             Login, returns JWT token
```

### Interview Setup
```
POST   /api/interview/setup        Create interview setup (HR or Technical)
GET    /api/interview/setup        Get all setups for logged-in user
GET    /api/interview/setup/{id}   Get specific setup
```

### Interview Session
```
POST   /api/interview/session/start?setupId={id}   Start session, returns sessionToken
```

### Evaluation
```
POST   /api/evaluation/generate/{sessionToken}     Generate evaluation report
GET    /api/evaluation/{sessionToken}              Fetch evaluation report
GET    /api/evaluation/history                     All past reports for user
GET    /api/evaluation/download/{sessionToken}     Download PDF report
```

### WebSocket
```
ws://localhost:8080/ws/interview/{sessionToken}    Live interview connection
```

---

## ⚡ WebSocket Events

### Server → Client
| Type | Description |
|---|---|
| `QUESTION` | Next question with questionNumber, questionText, isFollowUp |
| `ANALYSIS` | 5 scores + feedbackSummary after each answer |
| `INTERVIEW_END` | Interview complete, final score |
| `ERROR` | Error message |

### Client → Server
| Type | Description |
|---|---|
| `ANSWER` | Transcribed answer text, questionNumber, skipped=false |
| `SKIP` | Skip current question, skipped=true |
| `PROCTOR_EVENT` | Violation type (TAB_SWITCH, WINDOW_BLUR) + details |

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- OpenAI API key
- Chrome browser (for Speech Recognition)

### 1. Clone the repository
```bash
git clone https://github.com/yourusername/ai-interviewer.git
cd ai-interviewer
```

### 2. Configure application.properties
```properties
# Server
server.port=8080

# SQLite Database
spring.datasource.url=jdbc:sqlite:ai-interviewer.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret=your-secret-key-minimum-32-characters-long
jwt.expiration=3600000

# OpenAI
openai.api.key=your-openai-api-key-here
openai.api.url=https://api.openai.com/v1/chat/completions
openai.model=gpt-4o
```

### 3. Add SQLite dependency to pom.xml
```xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.43.0.0</version>
</dependency>
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-community-dialects</artifactId>
</dependency>
```

### 4. Run the application
```bash
mvn spring-boot:run
```

### 5. Open in browser
```
http://localhost:8080
```

---

## ⚙️ Configuration

### application.properties — Full Reference
```properties
# JWT Token Expiry
jwt.expiration=3600000               # 1 hour in milliseconds

# OpenAI Model
openai.model=gpt-4o                  # or gpt-4o-mini for lower cost

# Optional: Google TTS (if not using ResponsiveVoice)
google.tts.api.key=your-key
google.tts.voice=en-US-Neural2-D
```

### Silence Detection (interview.html)
```javascript
const SILENCE_MS = 5000;   // auto-submit after 5 seconds of silence
```

### STT Language (interview.html)
```javascript
recognition.lang = 'en-IN';   // Indian English — change to 'en-US' if needed
```

---

## 🔄 Interview Flow

```
1.  User registers / logs in
        ↓
2.  User selects interview type (HR / Technical)
    For Technical: enters tech stack, level, experience
        ↓
3.  POST /api/interview/setup        → creates setup
    POST /api/interview/session/start → creates session, returns sessionToken
        ↓
4.  Frontend requests camera + microphone access
        ↓
5.  WebSocket opens: ws://localhost:8080/ws/interview/{sessionToken}
        ↓
6.  Server generates Q1 → sends QUESTION event
    Frontend speaks question via ResponsiveVoice TTS
        ↓
7.  User speaks answer → SpeechRecognition captures transcript
    After 5s silence → auto-submits via WebSocket ANSWER event
        ↓
8.  Server calls GPT-4o → analyzes answer → sends ANALYSIS event
    5 scores shown on screen instantly
        ↓
9.  If user answered  → GPT generates follow-up question
    If user skipped   → GPT generates fresh random question
        ↓
10. Loop steps 6–9 until totalQuestions reached
        ↓
11. Server sends INTERVIEW_END event
    Frontend calls POST /api/evaluation/generate/{token}
        ↓
12. Redirects to evaluation.html
    User can view full report and download PDF
```

---

## 💰 Cost Estimation

| Component | Cost |
|---|---|
| GPT-4o question generation (10 calls) | ~$0.015 |
| GPT-4o answer analysis (10 calls) | ~$0.025 |
| GPT-4o evaluation report (1 call) | ~$0.018 |
| **Total per interview** | **~$0.058 (~₹5)** |
| 100 interviews/month | ~$6 (~₹500) |
| 1000 interviews/month | ~$58 (~₹5,000) |
| TTS (ResponsiveVoice) | Free |
| STT (Web Speech API) | Free |

> **Tip:** Switch `openai.model=gpt-4o-mini` for question generation to reduce cost to ~₹2 per interview.

---

## 📊 Scoring Parameters

Each answer is scored by GPT-4o on a scale of 0–10:

| Parameter | Weight | Description |
|---|---|---|
| Relevance | 25% | How relevant the answer is to the question |
| Concept | 30% | Depth of conceptual understanding |
| Clarity | 20% | How clearly the answer communicates the idea |
| Grammar | 10% | Spoken grammatical correctness |
| Completeness | 15% | How complete and thorough the answer is |

### Performance Bands
| Score | Band |
|---|---|
| 8.0 – 10.0 | Excellent |
| 6.0 – 7.9 | Good |
| 4.0 – 5.9 | Average |
| 2.0 – 3.9 | Below Average |
| 0.0 – 1.9 | Poor |

---

## 🌐 Frontend Pages

| Page | File | Description |
|---|---|---|
| Landing | index.html | Animated landing page with particle background |
| Auth | auth.html | Login and Register with JWT |
| Setup | setup.html | Interview type configuration |
| Interview | interview.html | Live interview with camera, STT, TTS, WebSocket |
| Evaluation | evaluation.html | Full report with PDF download and history |
| Profile | profile.html | Score charts, parameter radar, interview history |

---

## 🔒 Security

- Passwords hashed with **BCrypt**
- **JWT tokens** expire after 1 hour
- All API endpoints except `/api/auth/**` and `/ws/**` require valid JWT
- WebSocket sessions validated by `sessionToken` (UUID)
- **STATELESS** Spring Security — no server-side sessions

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is for educational and portfolio purposes.

---

## 👨‍💻 Author

Built with Spring Boot · GPT-4o · WebSocket · JavaScript

> **Note:** This project was built as a full-stack portfolio project demonstrating Spring Boot, JWT security, real-time WebSocket communication, AI API integration, and Speech APIs.
