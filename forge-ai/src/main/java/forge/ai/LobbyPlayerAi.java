package forge.ai;

import java.util.Set;

import forge.LobbyPlayer;
import forge.game.Game;
import forge.game.player.IGameEntitiesFactory;
import forge.game.player.Player;
import forge.game.player.PlayerController;

public class LobbyPlayerAi extends LobbyPlayer implements IGameEntitiesFactory {

    private String aiProfile = "";
    private boolean rotateProfileEachGame;
    private boolean allowCheatShuffle;
    private boolean useSimulation;
    private boolean useLlmAi;

    public LobbyPlayerAi(String name, Set<AIOption> options) {
        super(name);
        if (options != null) {
            this.useSimulation = options.contains(AIOption.USE_SIMULATION);
            this.useLlmAi = options.contains(AIOption.USE_LLM_AI);
        }
    }

    public boolean isAllowCheatShuffle() {
        return allowCheatShuffle;
    }
    public void setAllowCheatShuffle(boolean allowCheatShuffle) {
        this.allowCheatShuffle = allowCheatShuffle;
    }

    public void setAiProfile(String profileName) {
        aiProfile = profileName;
    }
    public String getAiProfile() {
        return aiProfile;
    }

    public void setRotateProfileEachGame(boolean rotateProfileEachGame) {
        this.rotateProfileEachGame = rotateProfileEachGame;
    }
    
    public boolean isUseLlmAi() {
        return useLlmAi;
    }
    
    public void setUseLlmAi(boolean useLlmAi) {
        this.useLlmAi = useLlmAi;
    }

    private PlayerController createControllerFor(Player ai) {
        if (useLlmAi) {
            return createLlmController(ai);
        }
        
        PlayerControllerAi result = new PlayerControllerAi(ai.getGame(), ai, this);
        result.setUseSimulation(useSimulation);
        result.allowCheatShuffle(allowCheatShuffle);
        return result;
    }
    
    private PlayerController createLlmController(Player ai) {
        try {
            // Dynamically load LLM controller if available
            Class<?> llmControllerClass = Class.forName("forge.ai.llm.PlayerControllerLlm");
            Class<?> llmClientFactoryClass = Class.forName("forge.ai.llm.LlmClientFactory");
            Class<?> llmClientClass = Class.forName("forge.ai.llm.LlmClient");
            
            // Get the factory method
            java.lang.reflect.Method createMethod = llmClientFactoryClass.getMethod("create", String.class);
            Object llmClient = createMethod.invoke(null, "local");
            
            // Create the LLM controller
            java.lang.reflect.Constructor<?> constructor = llmControllerClass.getConstructor(
                Object.class, Object.class, Object.class, llmClientClass, double.class, boolean.class
            );
            
            return (PlayerController) constructor.newInstance(ai.getGame(), ai, this, llmClient, 0.65, true);
        } catch (Exception e) {
            System.err.println("Could not initialize LLM AI controller: " + e.getMessage());
            // Fallback to standard AI
            PlayerControllerAi result = new PlayerControllerAi(ai.getGame(), ai, this);
            result.setUseSimulation(useSimulation);
            result.allowCheatShuffle(allowCheatShuffle);
            return result;
        }
    }

    @Override
    public PlayerController createMindSlaveController(Player master, Player slave) {
        return createControllerFor(slave);
    }

    @Override
    public Player createIngamePlayer(Game game, final int id) {
        Player ai = new Player(getName(), game, id);
        ai.setFirstController(createControllerFor(ai));

        if (rotateProfileEachGame) {
            setAiProfile(AiProfileUtil.getRandomProfile());
            /*System.out.println(String.format("AI profile %s was chosen for the lobby player %s.", getAiProfile(), getName()));*/
        }
        return ai;
    }

    @Override
    public void hear(LobbyPlayer player, String message) { /* Local AI is deaf. */ }
}