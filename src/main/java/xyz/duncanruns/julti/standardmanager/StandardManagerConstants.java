package xyz.duncanruns.julti.standardmanager;

import xyz.duncanruns.julti.JultiOptions;
import xyz.duncanruns.julti.util.MCKeyUtil;

import java.nio.file.Path;
import java.util.HashSet;

public interface StandardManagerConstants {
    Path STANDARD_MANAGER_PATH = JultiOptions.getJultiDir().resolve("standardmanager").toAbsolutePath();
    HashSet<String> VALID_MC_KEYS = new HashSet<>(MCKeyUtil.TRANSLATIONS_TO_GLFW.keySet());
}
