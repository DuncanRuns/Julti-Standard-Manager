package xyz.duncanruns.julti.standardmanager;

import java.util.function.Function;

import static xyz.duncanruns.julti.standardmanager.StandardManagerConstants.VALID_MC_KEYS;

public interface Validators {
    Function<String, Boolean> ALWAYS_VALID = s -> true;
    Function<String, Boolean> BOOLEAN_VALIDATOR = s -> s.equals("true") || s.equals("false");
    Function<String, Boolean> KEYBIND_VALIDATOR = VALID_MC_KEYS::contains;

    Function<String, Boolean> KEYBIND_REQUIRED_VALIDATOR = s -> {
        if (s.isEmpty() || s.equals("key.keyboard.unknown")) {
            return false;
        }
        return VALID_MC_KEYS.contains(s);
    };

    static Function<String, Boolean> ofDouble(double min, double max) {
        return s -> {
            try {
                double v = Double.parseDouble(s);
                return v >= min && v <= max;
            } catch (NumberFormatException e) {
                return false;
            }
        };
    }

    static Function<String, Boolean> ofInt(int min, int max) {
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
