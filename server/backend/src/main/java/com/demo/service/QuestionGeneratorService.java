package com.demo.service;

import com.demo.gpt.OpenAIClient;
import com.demo.model.InterviewQuestionHistory;
import com.demo.model.InterviewSetup;
import com.demo.model.InterviewType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionGeneratorService {

    private final OpenAIClient openAIClient;

    public String generateQuestion(InterviewSetup setup,
                                   InterviewQuestionHistory lastAnswer,
                                   boolean isFollowUp) {

        String systemPrompt = buildSystemPrompt(setup);
        String userPrompt   = buildUserPrompt(setup, lastAnswer, isFollowUp);

        log.info("Generating {} question for setup id: {}",
                isFollowUp ? "follow-up" : "random", setup.getId());

        return openAIClient.chat(systemPrompt, userPrompt);
    }

    private String buildSystemPrompt(InterviewSetup setup) {
        if (setup.getInterviewType() == InterviewType.HR) {
            return """
                You are a senior HR interviewer at a reputed technology company conducting a formal job interview.

                Your responsibilities:
                - Ask one professional HR interview question at a time.
                - Questions must be concept-based and theoretical in nature.
                - The question should be answerable in 2 to 3 lines by the candidate.
                - Focus on topics like: teamwork, conflict resolution, time management, leadership,
                  communication skills, adaptability, problem-solving approach, and career goals.
                - Questions must be clear, formal, and to the point.
                - Do not ask questions that require long storytelling or vague open-ended answers.
                - Do not add any explanation, numbering, options, or preamble.
                - Return only the question itself — nothing else.

                Example of good questions:
                - What does effective communication mean to you in a professional environment?
                - How do you prioritize tasks when you have multiple deadlines at the same time?
                - What is the difference between a leader and a manager?
                """;
        } else {
            String techStack = String.join(", ", setup.getTechStack());
            String level     = setup.getInterviewLevel().name();
            int    exp       = setup.getYearsOfExperience() != null ? setup.getYearsOfExperience() : 0;

            return String.format("""
                You are a senior technical interviewer at a top-tier software company.
                You are interviewing a candidate with %d years of experience at %s level
                for technologies: %s.

                Your responsibilities:
                - Ask one professional technical interview question at a time.
                - Questions must be strictly concept-based and theoretical — no coding questions.
                - The question should be answerable in 2 to 3 lines by the candidate.
                - Focus on core concepts, definitions, differences between terms,
                  real-world use cases, advantages/disadvantages, and best practices.
                - Match the difficulty strictly to the candidate's experience level:
                    * BEGINNER   → basic definitions, core concepts, simple comparisons
                    * INTERMEDIATE → internal working, design patterns, trade-offs, architecture
                    * EXPERT     → deep internals, performance, scalability, system design concepts
                - Do not ask questions that require writing code or long explanations.
                - Do not add any explanation, numbering, options, or preamble.
                - Return only the question itself — nothing else.

                Example of good questions:
                - What is the difference between HashMap and LinkedHashMap in Java?
                - How does Spring Boot auto-configuration work internally?
                - What is the purpose of the @Transactional annotation in Spring?
                - What is the difference between REST and WebSocket communication?
                """,
                    exp, level, techStack
            );
        }
    }

    private String buildUserPrompt(InterviewSetup setup,
                                   InterviewQuestionHistory lastAnswer,
                                   boolean isFollowUp) {
        if (!isFollowUp || lastAnswer == null) {
            if (setup.getInterviewType() == InterviewType.HR) {
                return """
                    Generate a fresh professional HR interview question.
                    The question must be theoretical and answerable in 2 to 3 lines.
                    Do not repeat common questions like 'tell me about yourself'.
                    Return only the question.
                    """;
            } else {
                String techStack = String.join(", ", setup.getTechStack());
                return String.format("""
                    Generate a fresh professional technical interview question
                    related to one of these technologies: %s.
                    The question must be concept-based, theoretical,
                    and answerable in 2 to 3 lines.
                    Do not ask for code. Return only the question.
                    """, techStack);
            }
        } else {
            // follow-up based on previous answer
            return String.format("""
                The candidate was asked the following question:
                "%s"

                The candidate gave this answer:
                "%s"

                Based on this answer, ask one professional follow-up question
                that goes one level deeper into the concept the candidate mentioned.
                The follow-up must be theoretical and answerable in 2 to 3 lines.
                Do not ask for code. Return only the question.
                """,
                    lastAnswer.getQuestionText(),
                    lastAnswer.getUserAnswer()
            );
        }
    }
}