package care.better.platform.web.template.context;

/**
 * @author Primoz Delopst
 */

public enum CompositionBuilderContextKey {
    LANGUAGE("language"),
    COMPOSER_NAME("composerName"),
    TERRITORY("territory"),
    ISM_TRANSITION("ism_transition"),
    START_TIME("start_time"),
    END_TIME("end_time"),
    LOCATION("location"),
    ENCODING("encoding"),
    LOCALE("locale"),
    ACTION_TO_INSTRUCTION_HANDLER("action_handler");

    private final String key;

    CompositionBuilderContextKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
