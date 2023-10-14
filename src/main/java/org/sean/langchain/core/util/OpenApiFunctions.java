package org.sean.langchain.core.util;

import dev.ai4j.openai4j.chat.*;
import dev.ai4j.openai4j.shared.Usage;
import org.sean.langchain.core.data.message.*;
import org.sean.langchain.core.data.output.FinishReason;
import org.sean.langchain.core.data.output.TokenUsage;
import org.sean.langchain.core.tool.ToolExecutionRequest;
import org.sean.langchain.core.tool.ToolParameters;
import org.sean.langchain.core.tool.ToolSpecification;

import java.time.Duration;
import java.util.Collection;
import java.util.List;

import static dev.ai4j.openai4j.chat.Role.*;
import static java.time.Duration.ofSeconds;
import static java.util.stream.Collectors.toList;
import static org.sean.langchain.core.constants.OpenAiModelName.GPT_3_5_TURBO;
import static org.sean.langchain.core.constants.OpenAiModelName.GPT_4;
import static org.sean.langchain.core.data.message.AiMessage.aiMessage;
import static org.sean.langchain.core.data.output.FinishReason.*;

public class OpenApiFunctions {

    public static Duration defaultTimeoutFor(String modelName) {
        if (modelName.startsWith(GPT_3_5_TURBO)) {
            return ofSeconds(7);
        } else if (modelName.startsWith(GPT_4)) {
            return ofSeconds(20);
        }

        return ofSeconds(10);
    }

    public static Role roleFrom(ChatMessage message) {
        if (message instanceof AiMessage) {
            return ASSISTANT;
        } else if (message instanceof ToolExecutionResultMessage) {
            return FUNCTION;
        } else if (message instanceof SystemMessage) {
            return SYSTEM;
        } else {
            return USER;
        }
    }

    private static String nameFrom(ChatMessage message) {
        if (message instanceof UserMessage) {
            return ((UserMessage) message).name();
        }

        if (message instanceof ToolExecutionResultMessage) {
            return ((ToolExecutionResultMessage) message).toolName();
        }

        return null;
    }

    private static FunctionCall functionCallFrom(ChatMessage message) {
        if (message instanceof AiMessage) {
            AiMessage aiMessage = (AiMessage) message;
            if (aiMessage.toolExecutionRequest() != null) {
                return FunctionCall.builder()
                        .name(aiMessage.toolExecutionRequest().name())
                        .arguments(aiMessage.toolExecutionRequest().arguments())
                        .build();
            }
        }

        return null;
    }

    public static TokenUsage tokenUsageFrom(Usage openAiUsage) {
        if (openAiUsage == null) {
            return null;
        }
        return new TokenUsage(
                openAiUsage.promptTokens(),
                openAiUsage.completionTokens(),
                openAiUsage.totalTokens()
        );
    }


    public static List<Message> toOpenAiMessages(List<ChatMessage> messages) {

        return messages.stream()
                .map(OpenApiFunctions::toOpenAiMessage)
                .collect(toList());
    }

    public static Message toOpenAiMessage(ChatMessage message) {

        return Message.builder()
                .role(roleFrom(message))
                .name(nameFrom(message))
                .content(message.text())
                .functionCall(functionCallFrom(message))
                .build();
    }

    public static List<Function> toFunctions(Collection<ToolSpecification> toolSpecifications) {
        return toolSpecifications.stream()
                .map(OpenApiFunctions::toFunction)
                .collect(toList());
    }

    private static Function toFunction(ToolSpecification toolSpecification) {
        return Function.builder()
                .name(toolSpecification.name())
                .description(toolSpecification.description())
                .parameters(toOpenAiParameters(toolSpecification.parameters()))
                .build();
    }

    private static dev.ai4j.openai4j.chat.Parameters toOpenAiParameters(ToolParameters toolParameters) {
        if (toolParameters == null) {
            return dev.ai4j.openai4j.chat.Parameters.builder().build();
        }
        return dev.ai4j.openai4j.chat.Parameters.builder()
                .properties(toolParameters.properties())
                .required(toolParameters.required())
                .build();
    }

    public static AiMessage aiMessageFrom(ChatCompletionResponse response) {
        if (response.content() != null) {
            return aiMessage(response.content());
        } else {
            FunctionCall functionCall = response.choices().get(0).message().functionCall();

            ToolExecutionRequest toolExecutionRequest = ToolExecutionRequest.builder()
                    .name(functionCall.name())
                    .arguments(functionCall.arguments())
                    .build();

            return aiMessage(toolExecutionRequest);
        }
    }


    public static FinishReason finishReasonFrom(String openAiFinishReason) {
        switch (openAiFinishReason) {
            case "stop":
                return STOP;
            case "length":
                return LENGTH;
            case "function_call":
                return TOOL_EXECUTION;
            case "content_filter":
                return CONTENT_FILTER;
            default:
                return null;
        }
    }



}
