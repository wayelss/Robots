package gui;

import javax.swing.*;

public class UI {
    protected static void setUpRussianLanguage(){
        UIManager.put("InternalFrame.maxButtonToolTip", "Развернуть");
        UIManager.put("InternalFrame.closeButtonToolTip", "Закрыть");
        UIManager.put("InternalFrame.iconButtonToolTip", "Свернуть");
        UIManager.put("InternalFrameTitlePane.restoreButtonText", "Восстановить");
        UIManager.put("InternalFrameTitlePane.moveButtonText", "Переместить");
        UIManager.put("InternalFrameTitlePane.sizeButtonText", "Размер");
        UIManager.put("InternalFrameTitlePane.minimizeButtonText", "Свернуть");
        UIManager.put("InternalFrameTitlePane.maximizeButtonText", "Развернуть");
        UIManager.put("InternalFrameTitlePane.closeButtonText", "Закрыть");
        UIManager.put("OptionPane.yesButtonText"   , "Да"    );
        UIManager.put("OptionPane.noButtonText"    , "Нет"   );
        UIManager.put("OptionPane.close","Закрыть");
    }
}
