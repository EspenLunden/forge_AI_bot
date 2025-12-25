package forge.ai.llm;

import java.util.*;

/**
 * Factory for creating LLM clients based on configuration
 */
public class LlmClientFactory {
    
    private static LlmClient instance;
    private static Map<String, LlmClient> clients = new HashMap<>();
    
    public static LlmClient create(String provider) {
        return create(provider, null);
    }
    
    public static LlmClient create(String provider, Map<String, String> config) {
        provider = provider.toLowerCase().trim();
        
        // Check cache first
        if (clients.containsKey(provider)) {
            return clients.get(provider);
        }
        
        LlmClient client = null;
        
        switch (provider) {
            case "local":
            case "local_llm":
                String trainingPath = config != null ? 
                    config.getOrDefault("trainingPath", "./llm_training_data/") : 
                    "./llm_training_data/";
                client = new LocalLlmClient(trainingPath);
                break;
                
            case "openai":
            case "gpt":
                String apiKey = config != null ? config.get("apiKey") : System.getenv("OPENAI_API_KEY");
                String model = config != null ? 
                    config.getOrDefault("model", "gpt-4") : 
                    "gpt-4";
                
                if (apiKey == null || apiKey.isEmpty()) {
                    System.err.println("OpenAI API key not configured. Falling back to LocalLlmClient.");
                    client = new LocalLlmClient();
                } else {
                    // Placeholder - implement OpenAiClient when needed
                    System.out.println("OpenAI client not yet implemented. Using LocalLlmClient instead.");
                    client = new LocalLlmClient();
                }
                break;
                
            case "claude":
                String claudeKey = config != null ? config.get("apiKey") : System.getenv("ANTHROPIC_API_KEY");
                if (claudeKey == null || claudeKey.isEmpty()) {
                    System.err.println("Claude API key not configured. Falling back to LocalLlmClient.");
                    client = new LocalLlmClient();
                } else {
                    // Placeholder - implement ClaudeClient when needed
                    System.out.println("Claude client not yet implemented. Using LocalLlmClient instead.");
                    client = new LocalLlmClient();
                }
                break;
                
            default:
                System.out.println("Unknown LLM provider: " + provider + ". Using LocalLlmClient.");
                client = new LocalLlmClient();
        }
        
        if (client != null) {
            clients.put(provider, client);
        }
        
        return client;
    }
    
    public static void setDefault(LlmClient client) {
        instance = client;
    }
    
    public static LlmClient getDefault() {
        if (instance == null) {
            instance = create("local");
        }
        return instance;
    }
    
    public static void clearCache() {
        clients.clear();
        instance = null;
    }
    
    public static void closeAll() {
        clients.clear();
        instance = null;
    }
}
