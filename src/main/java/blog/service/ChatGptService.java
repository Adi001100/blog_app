package blog.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.ImageResult;
import com.theokanning.openai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;

@Service
public class ChatGptService {

    @Value("${openai.model}")
    private String model;

    @Value("${open.api.key}")
    private String openaiApiKey;

    private OpenAiService openAiService;

    private CloudinaryService cloudinaryService;

    private final String SYSTEM_TASK_MESSAGE = "You are in an API server." +
            System.lineSeparator() +
            "Dont say anything else just respond." +
            System.lineSeparator() +
            "The user will provide you a title and you have to write a post body about that in maximum five sentences." +
            System.lineSeparator() +
            "Don't do anything else at the end just respond.";

    private static final Logger logger = LoggerFactory.getLogger(ChatGptService.class);

    @Autowired
    public ChatGptService(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }


    @PostConstruct
    private void initGptService() {
        openAiService = new OpenAiService(openaiApiKey, Duration.ofSeconds(30));
        logger.info("Connected to OpenAi!");
    }

    public String generate(String prompt) {
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .temperature(1.0)
                .messages(List.of(
                        new ChatMessage("system", SYSTEM_TASK_MESSAGE),
                        new ChatMessage("user", prompt)))
                .build();
        return String.valueOf(openAiService.createChatCompletion(request).getChoices().get(0).getMessage().getContent());
    }

    public String generateImg(String prompt) {
        CreateImageRequest request = CreateImageRequest.builder()
                .prompt(prompt)
                .size("1024x1024")
                .n(1)
                .build();
        ImageResult result = openAiService.createImage(request);
        return cloudinaryService.uploadImgFromAI(String.valueOf(result.getData().get(0)));
    }
}
