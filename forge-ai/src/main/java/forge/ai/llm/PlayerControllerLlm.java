package forge.ai.llm;

import forge.ai.PlayerControllerAi;
import forge.game.Game;
import forge.game.player.Player;
import forge.game.spellability.SpellAbility;
import forge.game.card.Card;
import forge.util.ITriggerEvent;
import forge.LobbyPlayer;

import java.util.*;

/**
 * LLM-integrated AI player controller
 * 
 * Extends PlayerControllerAi to provide LLM-based decision making with logging
 * of AI reasoning to a chat box for player transparency.
 */
public class PlayerControllerLlm extends PlayerControllerAi {
    
    private final LlmClient llmClient;
    private final GameStateSerializer serializer;
    private final boolean trainingMode;
    private List<String> decisionHistory;
    private static final List<String> globalDecisionLog = Collections.synchronizedList(new ArrayList<>());
    
    public PlayerControllerLlm(Game game, Player player, LobbyPlayer lobbyPlayer, LlmClient llmClient) {
        this(game, player, lobbyPlayer, llmClient, false);
    }
    
    public PlayerControllerLlm(Game game, Player player, LobbyPlayer lobbyPlayer, 
                               LlmClient llmClient, boolean trainingMode) {
        super(game, player, lobbyPlayer);
        this.llmClient = llmClient;
        this.serializer = new GameStateSerializer();
        this.trainingMode = trainingMode;
        this.decisionHistory = new ArrayList<>();
        
        logDecision("=== LLM AI Controller Initialized for " + player.getName() + " ===");
    }
    
    /**
     * Log decisions to both local history and global log (for chat display)
     */
    private void logDecision(String message) {
        String timestamped = String.format("[%s] %s", new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()), message);
        decisionHistory.add(timestamped);
        globalDecisionLog.add(timestamped);
        System.out.println("[LLM-AI] " + message);
    }
    
    /**
     * Override ability choice to add LLM reasoning
     */
    @Override
    public SpellAbility getAbilityToPlay(Card hostCard, List<SpellAbility> abilities, ITriggerEvent triggerEvent) {
        SpellAbility chosen = super.getAbilityToPlay(hostCard, abilities, triggerEvent);
        
        if (chosen != null) {
            String reasoning = String.format("AI chose to play: %s", chosen.getHostCard().getName());
            logDecision(reasoning);
            
            // Get LLM reasoning if available
            if (llmClient != null && llmClient.isAvailable()) {
                try {
                    GameContext context = new GameContext();
                    context.format = this.getGame().getFormat() != null ? this.getGame().getFormat().toString() : "Unknown";
                    context.turn = this.getGame().getPhaseHandler().getTurn();
                    context.phase = this.getGame().getPhaseHandler().getPhase().toString();
                    context.lifeTotal = this.getPlayer().getLife();
                    context.opponentLifeTotal = this.getGame().getPlayers().get(0).getLife(); // Simplified
                    context.cardsInHand = this.getPlayer().getHand().size();
                    context.cardsInLibrary = this.getPlayer().getZone(forge.game.zone.ZoneType.Library).size();
                    context.boardPresence = this.getPlayer().getCreaturesInPlay().size();
                    
                    List<String> actionNames = new ArrayList<>();
                    for (SpellAbility sa : abilities) {
                        if (sa.getHostCard() != null) {
                            actionNames.add(sa.getHostCard().getName());
                        }
                    }
                    
                    LlmDecision decision = llmClient.queryForDecision(context.toString(), actionNames, context);
                    if (decision != null && decision.reasoning != null && !decision.reasoning.isEmpty()) {
                        logDecision(String.format("LLM Reasoning: %s", decision.reasoning));
                    }
                } catch (Exception e) {
                    // Silently fail - don't break the game
                }
            }
        } else {
            logDecision("AI: Passing turn");
        }
        
        return chosen;
    }
    
    /**
     * Called when game ends - for training data collection
     */
    public void gameEnded(boolean playerWon) {
        if (trainingMode && llmClient != null) {
            logDecision("Game ended. Player " + (playerWon ? "WON" : "LOST"));
            llmClient.provideFeedback(playerWon, 0);
        }
    }
    
    public LlmClient getLlmClient() {
        return llmClient;
    }
    
    public GameStateSerializer getSerializer() {
        return serializer;
    }
    
    public List<String> getDecisionHistory() {
        return new ArrayList<>(decisionHistory);
    }
    
    /**
     * Get the global decision log for display in chat box
     */
    public static List<String> getGlobalDecisionLog() {
        return new ArrayList<>(globalDecisionLog);
    }
    
    /**
     * Clear the global decision log
     */
    public static void clearGlobalDecisionLog() {
        globalDecisionLog.clear();
    }
}
