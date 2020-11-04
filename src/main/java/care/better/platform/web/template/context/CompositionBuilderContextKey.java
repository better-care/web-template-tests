package care.better.platform.web.template.context;

/**
 * @author Primoz Delopst
 */

public enum CompositionBuilderContextKey {
    LANGUAGE("language"),
    COMPOSER_NAME("composerName"),
    TERRITORY("territory");

    private final String key;

    CompositionBuilderContextKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
