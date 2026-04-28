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

    // generates the next interview question
    // lastAnswer = null means: first question OR user skipped (generate random)
    // lastAnswer != null means: generate a follow-up based on previous answer
    public String generateQuestion(InterviewSetup setup,
                                   InterviewQuestionHistory lastAnswer,
                                   boolean isFollowUp) {

        String systemPrompt = buildSystemPrompt(setup);
        String userPrompt   = buildUserPrompt(setup, lastAnswer, isFollowUp);

        log.info("Generating {} question for setup id: {}",
                isFollowUp ? "follow-up" : "random", setup.getId());

        return openAIClient.chat(systemPrompt, userPrompt);
    }

    // system prompt sets the interviewer persona and context
    private String buildSystemPrompt(InterviewSetup setup) {
        if (setup.getInterviewType() == InterviewType.HR) {
            return """
                You are a professional HR interviewer conducting a job interview.
                Ask one clear, concise HR interview question at a time.
                Focus on behavioral, situational, and soft-skill questions.
                Do not add any explanation, numbering, or preamble — just the question itself.
                """;
        } else {
            String techStack = String.join(", ", setup.getTechStack());
            return String.format("""
                You are an expert technical interviewer specializing in: %s.
                The candidate has %d years of experience and is at %s level.
                Ask one clear, concise technical interview question at a time.
                Do not add any explanation, numbering, or preamble — just the question itself.
                Match the difficulty to the candidate's level.
                """,
                techStack,
                setup.getYearsOfExperience() != null ? setup.getYearsOfExperience() : 0,
                setup.getInterviewLevel().name()
            );
        }
    }

    // user prompt tells GPT what kind of question to generate
    private String buildUserPrompt(InterviewSetup setup,
                                    InterviewQuestionHistory lastAnswer,
                                    boolean isFollowUp) {
        if (!isFollowUp || lastAnswer == null) {
            // first question or after a skip — generate fresh random question
            return "Ask a new interview question.";
        } else {
            // generate follow-up based on previous Q&A
            return String.format("""
                Previous question: %s
                Candidate's answer: %s
                
                Based on this answer, ask a relevant follow-up question to dig deeper
                or clarify something the candidate mentioned.
                """,
                lastAnswer.getQuestionText(),
                lastAnswer.getUserAnswer()
            );
        }
    }
}