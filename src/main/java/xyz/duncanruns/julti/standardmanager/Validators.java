package xyz.duncanruns.julti.standardmanager;

import java.util.function.Predicate;

import static xyz.duncanruns.julti.standardmanager.StandardManagerConstants.VALID_MC_KEYS;

public final class Validators {
    public static final Predicate<String> ALWAYS_VALID = s -> true;
    public static final Predicate<String> BOOLEAN_VALIDATOR = s -> s.equals("true") || s.equals("false");
    public static final Predicate<String> KEYBIND_VALIDATOR = ofInt(0, 223).or(ofInt(-100, -85)).or(VALID_MC_KEYS::contains);
    public static final Predicate<String> KEYBIND_REQUIRED_VALIDATOR = KEYBIND_VALIDATOR.and(s -> !(s.isEmpty() || s.equals("key.keyboard.unknown") || s.equals("0")));

    private Validators() {
    }

    public static Predicate<String> ofDouble(double min, double max) {
        return s -> {
            try {
                double v = Double.parseDouble(s);
                return v >= min && v <= max;
            } catch (NumberFormatException e) {
                return false;
            }
        };
    }

    public static Predicate<String> ofInt(int min, int max) {
        return s -> {
            try {
                int v = Integer.parseInt(s);
                return v >= min && v <= max;
            } catch (NumberFormatException e) {
                return false;
            }
        };
    }
}
