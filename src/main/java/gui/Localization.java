package gui;

import javax.swing.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class Localization {
    private static ResourceBundle resourceBundle;

    public static void setUpRussianLanguage() {
        resourceBundle = ResourceBundle.getBundle("messages", new Locale("ru"));
        applyUiLocalization();

    }

    public static void setUpEnglishLanguage() {
        resourceBundle = ResourceBundle.getBundle("messages", new Locale("en"));
        applyUiLocalization();
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    private static void applyUiLocalization() {
        UIManager.put("InternalFrame.maxButtonToolTip", resourceBundle.getString("ui.internalFrame.maxButtonToolTip"));
        UIManager.put("InternalFrame.closeButtonToolTip", resourceBundle.getString("ui.internalFrame.closeButtonToolTip"));
        UIManager.put("InternalFrame.iconButtonToolTip", resourceBundle.getString("ui.internalFrame.iconButtonToolTip"));
        UIManager.put("InternalFrameTitlePane.restoreButtonText", resourceBundle.getString("ui.internalFrameTitlePane.restoreButtonText"));
        UIManager.put("InternalFrameTitlePane.moveButtonText", resourceBundle.getString("ui.internalFrameTitlePane.moveButtonText"));
        UIManager.put("InternalFrameTitlePane.sizeButtonText", resourceBundle.getString("ui.internalFrameTitlePane.sizeButtonText"));
        UIManager.put("InternalFrameTitlePane.minimizeButtonText", resourceBundle.getString("ui.internalFrameTitlePane.minimizeButtonText"));
        UIManager.put("InternalFrameTitlePane.maximizeButtonText", resourceBundle.getString("ui.internalFrameTitlePane.maximizeButtonText"));
        UIManager.put("InternalFrameTitlePane.closeButtonText", resourceBundle.getString("ui.internalFrameTitlePane.closeButtonText"));
        UIManager.put("OptionPane.yesButtonText", resourceBundle.getString("ui.optionPane.yesButtonText"));
        UIManager.put("OptionPane.noButtonText", resourceBundle.getString("ui.optionPane.noButtonText"));
        UIManager.put("OptionPane.close", resourceBundle.getString("ui.optionPane.close"));
    }
}