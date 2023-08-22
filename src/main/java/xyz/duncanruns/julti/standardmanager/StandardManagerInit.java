package xyz.duncanruns.julti.standardmanager;

import com.google.common.io.Resources;
import xyz.duncanruns.julti.JultiAppLaunch;
import xyz.duncanruns.julti.gui.JultiGUI;
import xyz.duncanruns.julti.management.InstanceManager;
import xyz.duncanruns.julti.plugin.PluginInitializer;
import xyz.duncanruns.julti.plugin.PluginManager;
import xyz.duncanruns.julti.standardmanager.gui.StandardManagerGUI;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import static xyz.duncanruns.julti.standardmanager.StandardManagerConstants.STANDARD_MANAGER_PATH;

public class StandardManagerInit implements PluginInitializer {
    private static StandardManagerGUI gui = null;

    public static void main(String[] args) throws IOException {
        // This is only used to test the plugin in the dev environment
        // ExamplePlugin.main itself is never used when users run Julti

        JultiAppLaunch.launchWithDevPlugin(args, PluginManager.JultiPluginData.fromString(
                Resources.toString(Resources.getResource(StandardManagerInit.class, "/julti.plugin.json"), Charset.defaultCharset())
        ), new StandardManagerInit());
    }

    public static StandardManagerGUI openStandardManager() {
        if (gui == null || gui.isClosed()) {
            gui = new StandardManagerGUI();
        } else {
            gui.requestFocus();
        }
        return gui;
    }

    @Override
    public void initialize() {
        // ensure standardmanager folder
        if (!Files.isDirectory(STANDARD_MANAGER_PATH)) {
            try {
                Files.createDirectory(STANDARD_MANAGER_PATH);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String getMenuButtonName() {
        return "Open";
    }

    @Override
    public void onMenuButtonPress() {
        if (InstanceManager.getInstanceManager().getInstances().isEmpty()) {
            JOptionPane.showMessageDialog(JultiGUI.getPluginsGUI(), "You don't have any instances!", "Julti Standard Manager: No Instances", JOptionPane.WARNING_MESSAGE);
        } else {
            openStandardManager();
        }
    }
}
