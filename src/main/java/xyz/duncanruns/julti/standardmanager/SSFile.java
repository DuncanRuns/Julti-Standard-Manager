package xyz.duncanruns.julti.standardmanager;

import xyz.duncanruns.julti.instance.MinecraftInstance;
import xyz.duncanruns.julti.management.InstanceManager;
import xyz.duncanruns.julti.util.FileUtil;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

import static xyz.duncanruns.julti.standardmanager.StandardManagerConstants.STANDARD_MANAGER_PATH;

public class SSFile {
    private final Path path;
    private final Map<String, String> values = new HashMap<>();
    private final boolean inManagerFolder;

    public SSFile(Path path) {
        this.path = path.toAbsolutePath();
        this.inManagerFolder = path.toAbsolutePath().getParent().equals(STANDARD_MANAGER_PATH);
    }

    public SSFile(String name) {
        this.path = STANDARD_MANAGER_PATH.resolve(name).toAbsolutePath();
        this.inManagerFolder = true;
    }

    public static boolean isValidPath(String path) {
        try {
            Paths.get(path);
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
        return true;
    }

    public boolean isInManagerFolder() {
        return this.inManagerFolder;
    }

    public SSFile copyToAndSave(String name) {
        SSFile newSSFile = new SSFile(name);
        newSSFile.values.putAll(this.values);
        newSSFile.save();
        return newSSFile;
    }

    public SSFile load() {
        String fullFile;
        try {
            fullFile = FileUtil.readString(this.path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Pattern is essentially
        Pattern pattern = Pattern.compile("^.+:.*$");
        Arrays.stream(fullFile.trim().split("\n")).map(String::trim).filter(s -> pattern.matcher(s).matches()).forEach(this::addValueFromRawLine);
        return this;
    }

    private void addValueFromRawLine(String s) {
        // Split by colon, if only length 1, add as key with empty value, otherwise add with the found value
        String[] split = s.split(":");
        if (split.length == 1) {
            this.values.put(split[0], "");
        } else {
            this.values.put(split[0], split[1]);
        }
    }

    public boolean hasRequiredJultiOptions() {
        return this.get("fullscreen").equals("false")
                && this.get("pauseOnLostFocus").equals("false")
                && this.get("changeOnResize").equals("true")
                && this.get("key_Cycle ChunkMap Positions").equals("key.keyboard.unknown")
                && Validators.KEYBIND_REQUIRED_VALIDATOR.apply(this.get("key_Create New World"))
                && Validators.KEYBIND_REQUIRED_VALIDATOR.apply(this.get("key_Leave Preview"))
                && Validators.KEYBIND_REQUIRED_VALIDATOR.apply(this.get("key_key.fullscreen"));
    }

    public void fixRequiredForJulti() {
        this.set("fullscreen", "false");
        this.set("pauseOnLostFocus", "false");
        this.set("changeOnResize", "true");
        this.set("key_Cycle ChunkMap Positions", "key.keyboard.unknown");
        if (!Validators.KEYBIND_REQUIRED_VALIDATOR.apply(this.get("key_key.fullscreen"))) {
            this.set("key_key.fullscreen", "key.keyboard.f11");
        }
        if (!Validators.KEYBIND_REQUIRED_VALIDATOR.apply(this.get("key_Create New World"))) {
            this.set("key_Create New World", "key.keyboard.f13");
        }
        if (!Validators.KEYBIND_REQUIRED_VALIDATOR.apply(this.get("key_Leave Preview"))) {
            this.set("key_Leave Preview", "key.keyboard.f14");
        }
        this.save();
    }

    public List<EditableOption> getEditableOptions() {
        List<EditableOption> editableOptions = new LinkedList<>();
        editableOptions.add(new EditableOption("Render Distance (In Game)", "renderDistance", Validators.ofInt(2, 32)));
        editableOptions.add(new EditableOption("Render Distance (On Wall)", "renderDistanceOnWorldJoin", Validators.ofInt(2, 32)));
        editableOptions.add(new EditableOption("FOV (In Game)", "fovOnWorldJoin", Validators.ofInt(30, 110)));
        editableOptions.add(new EditableOption("FOV (On Wall)", "fov", Validators.ofInt(30, 110)));
        editableOptions.add(new EditableOption("Perspective", "perspective", Validators.ofInt(0, 2)));
        editableOptions.add(new EditableOption("Pie Directory", "piedirectory", Validators.ALWAYS_VALID));
        editableOptions.add(new EditableOption("Hitboxes", "hitboxes", Validators.BOOLEAN_VALIDATOR));
        editableOptions.add(new EditableOption("Chunk Borders", "chunkborders", Validators.BOOLEAN_VALIDATOR));
        editableOptions.add(new EditableOption("Entity Distance (In Game)", "entityDistanceScalingOnWorldJoin", Validators.ofDouble(0.5, 5)));
        editableOptions.add(new EditableOption("Entity Distance (On Wall)", "entityDistanceScaling", Validators.ofDouble(0.5, 5)));
        editableOptions.add(new EditableOption("GUI Scale (In Game)", "guiScaleOnWorldJoin", Validators.ofInt(1, 4)));
        editableOptions.add(new EditableOption("GUI Scale (On Wall)", "guiScale", Validators.ofInt(1, 4)));
        editableOptions.add(new EditableOption("Brightness", "gamma", Validators.ofDouble(0, 5)));
        editableOptions.add(new EditableOption("Max FPS", "maxFps", Validators.ofInt(10, 260)));
        editableOptions.add(new EditableOption("Entity Culling", "entityCulling", Validators.BOOLEAN_VALIDATOR));
        editableOptions.add(new EditableOption("Advanced Item Tooltips", "advancedItemTooltips", Validators.BOOLEAN_VALIDATOR));
        editableOptions.add(new EditableOption("Hide Hud", "f1", Validators.BOOLEAN_VALIDATOR));
        editableOptions.add(new EditableOption("Create New World Key", "key_Create New World", Validators.KEYBIND_VALIDATOR));
        editableOptions.add(new EditableOption("Leave Preview Key", "key_Leave Preview", Validators.KEYBIND_VALIDATOR));
        editableOptions.add(new EditableOption("Fullscreen Key", "key_key.fullscreen", Validators.KEYBIND_VALIDATOR));
        return editableOptions;
    }

    public void set(String key, String value) {
        this.values.put(key, value);
    }

    public String get(String key) {
        return this.values.getOrDefault(key, "");
    }

    public void save() {
        StringBuilder builder = new StringBuilder();
        // For all keys sorted alphabetically, append to the builder with the key, a colon, the value from that key, and a newline
        this.values.keySet().stream().sorted(String::compareTo).forEach((s) -> builder.append(s).append(":").append(this.values.get(s)).append("\n"));

        String out = builder.toString().trim();
        try {
            FileUtil.writeString(this.path, out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean appliesTo(MinecraftInstance instance) {
        Path soTxtPath = instance.getPath().resolve("config").resolve("standardoptions.txt").toAbsolutePath();
        if (soTxtPath.equals(this.path)) {
            return true;
        }

        String s;
        try {
            s = FileUtil.readString(soTxtPath).trim();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return isValidPath(s) && Paths.get(s).toAbsolutePath().equals(this.path);
    }


    public boolean isAppliedToAllInstances() {
        for (MinecraftInstance instance : InstanceManager.getInstanceManager().getInstances()) {
            if (!this.appliesTo(instance)) {
                return false;
            }
        }
        return true;
    }

    public String getName() {
        if (this.inManagerFolder) {
            return this.path.getFileName().toString();
        } else {
            return this.path.toString();
        }
    }

    @Override
    public int hashCode() {
        return this.path.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        SSFile ssFile = (SSFile) o;

        return this.path.equals(ssFile.path);
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public boolean exists() {
        return Files.exists(this.path);
    }

    public void delete() {
        if (this.exists()) {
            try {
                Files.delete(this.path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void editManually() {
        try {
            Desktop.getDesktop().open(this.path.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void applyToAllInstances() {
        for (MinecraftInstance instance : InstanceManager.getInstanceManager().getInstances()) {
            try {
                FileUtil.writeString(instance.getPath().resolve("config").resolve("standardoptions.txt"), this.path.toAbsolutePath().toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public class EditableOption {
        public final String name;
        public final String key;
        private final Function<String, Boolean> validator;

        public EditableOption(String name, String key, Function<String, Boolean> validator) {
            this.name = name;
            this.key = key;
            this.validator = validator;
        }

        public String get() {
            return SSFile.this.get(this.key);
        }

        public boolean set(String value) {
            if (!this.validator.apply(value)) {
                return false;
            }
            SSFile.this.set(this.key, value);
            SSFile.this.save();
            return true;
        }
    }
}
